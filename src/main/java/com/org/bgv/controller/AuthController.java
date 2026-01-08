package com.org.bgv.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.ChangePasswordRequest;
import com.org.bgv.config.CustomUserDetails;
import com.org.bgv.config.JwtUtil;
import com.org.bgv.dto.AuthRequest;
import com.org.bgv.dto.AuthResponse;
import com.org.bgv.service.CompanyService;
import com.org.bgv.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired 
    private AuthenticationManager authenticationManager;
    @Autowired 
    private UserService userService;
    
    @Autowired 
    private JwtUtil jwtUtil;
    
    @Autowired
    private CompanyService companyService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        try {
            logger.info("🔐 Login attempt for email: {}", authRequest.getEmail());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Get user's company ID (assuming user is assigned to at least one company)
           // Long companyId = getUserCompanyId(userDetails.getUserId());

            // Generate tokens with company ID
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            // Send refresh token as HTTP-only cookie
            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true) // set to true in production
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            logger.info("✅ Login successful for {} with companyId: {}", userDetails.getUsername());
            
            return ResponseEntity.ok(AuthResponse.builder()
                    .token(accessToken)
                   // .companyId(companyId)
                  //  .userId(userDetails.getUserId())
                   // .firstName(userDetails.getFirstName())
                  //  .lastName(userDetails.getLastName())
                  //  .email(userDetails.getEmail())
                    .build());

        } catch (AuthenticationException e) {
            logger.error("❌ Invalid credentials for {}", authRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            logger.error("❌ Error during login for {}: {}", authRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);
      //  Long companyId = jwtUtil.extractCompanyId(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(username);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(newAccessToken)
             //   .companyId(companyId)
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // delete refresh token cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/{userId}/reset-password")
    @Operation(summary = "Change user password", description = "Change password for a specific user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or current password incorrect"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomApiResponse<Void>> changePassword(
            @Parameter(description = "ID of the user", required = true, example = "123")
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
        	logger.info("reset-password controller:::::::{}",userId);
            userService.resetPassword(userId, request);
            return ResponseEntity.ok(CustomApiResponse.success("Password Reset successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to Reset password: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}