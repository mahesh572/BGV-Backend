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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.config.CustomUserDetails;
import com.org.bgv.config.JwtUtil;
import com.org.bgv.dto.AuthRequest;
import com.org.bgv.dto.AuthResponse;
import com.org.bgv.service.CompanyService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired 
    private AuthenticationManager authenticationManager;
    
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

    /**
     * Get the company ID for the user
     * For admin users, return the default admin company ID
     * For regular users, return their assigned company ID
     */
    /*
    private Long getUserCompanyId(Long userId) {
        try {
            // Get user's companies
            var userCompanies = companyService.getCompaniesByUser(userId);
            
            if (userCompanies.isEmpty()) {
                logger.warn("⚠️ No company found for user ID: {}, using default admin company", userId);
                // Return default admin company if no company assigned
                return companyService.getDefaultAdminCompany()
                        .orElseThrow(() -> new RuntimeException("Default admin company not found"))
                        .getId();
            }
            
            // For now, return the first company
            // You might want to implement company selection logic here
            return userCompanies.get(0).getCompany().getId();
            
        } catch (Exception e) {
            logger.error("❌ Error getting company for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Unable to determine user's company");
        }
    }
    */
}