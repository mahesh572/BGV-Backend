package com.org.bgv.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.auth.dto.ResetPasswordRequest;
import com.org.bgv.auth.dto.TokenValidationResult;
import com.org.bgv.auth.service.ResetTokenService;
import com.org.bgv.common.ChangePasswordRequest;
import com.org.bgv.common.UserDto;
import com.org.bgv.common.navigation.PortalType;
import com.org.bgv.company.repository.EmployeeRepository;
import com.org.bgv.config.CustomUserDetails;
import com.org.bgv.config.JwtUtil;
import com.org.bgv.config.PortalAuthenticationToken;
import com.org.bgv.dto.AuthRequest;
import com.org.bgv.dto.AuthResponse;
import com.org.bgv.service.CompanyService;
import com.org.bgv.service.UserService;
import com.org.bgv.user.requests.ActivationRequest;
import com.org.bgv.user.requests.UserRegistrationRequest;
import com.org.bgv.user.service.AccountActivationService;
import com.org.bgv.user.service.UserRegistrationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
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
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private ResetTokenService resetTokenService;
    
    @Autowired
    private UserRegistrationService registrationService;
    
    @Autowired
    private AccountActivationService accountActivationService;
   
    

    @PostMapping("/login")
    public ResponseEntity<CustomApiResponse<AuthResponse>> login(
            @RequestBody AuthRequest authRequest,
            HttpServletResponse response) {

        try {
            logger.info("auth/login::::::{}", authRequest);

            PortalType portal = authRequest.getPortal();
                   
            Authentication authentication =
                    authenticationManager.authenticate(
                        new PortalAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword(),
                            portal
                        )
                    );

            CustomUserDetails user =
                    (CustomUserDetails) authentication.getPrincipal();

            String accessToken = jwtUtil.generateToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            AuthResponse authResponse = AuthResponse.builder()
                    .token(accessToken)
                    .build();

            logger.info("auth/login::::::SUCCESS::::userId={}", user.getUserId());
            
            logger.info("authResponse:::::::::::::::::::::{}",authResponse);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Login successful",
                            authResponse,
                            HttpStatus.OK
                    )
            );

        } catch (AuthenticationException e) {
            logger.warn("auth/login::::::INVALID_CREDENTIALS::::{}", authRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomApiResponse.failure(
                            "Invalid credentials",
                            HttpStatus.UNAUTHORIZED
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            "Invalid portal type",
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(CustomApiResponse.failure(
                        e.getMessage(),
                        HttpStatus.FORBIDDEN
                    ));
            }
        
        catch (Exception e) {
            logger.error("auth/login::::::FAILED", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Login failed: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
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
    public ResponseEntity<CustomApiResponse<Void>> logout(HttpServletResponse response) {

        try {
            logger.info("auth/logout::::START");

            ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .secure(true)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Logged out successfully",
                            null,
                            HttpStatus.OK
                    )
            );

        } catch (Exception e) {
            logger.error("auth/logout::::FAILED", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Logout failed",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
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
    
    @PostMapping("/reset-password-token")
    public ResponseEntity<?> resetPassword(
            @RequestParam String token,
            @Valid @RequestBody ResetPasswordRequest request) {

    	userService.resetPassword(token, request);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Password reset successful",
                        null,
                        HttpStatus.OK
                )
        );
    }
    
    
    @GetMapping("/token/validate")
    public ResponseEntity<CustomApiResponse<Void>> validateResetToken(
            @RequestParam String token
    ) {
        TokenValidationResult result = resetTokenService.validateToken(token);

        if (!result.valid()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            CustomApiResponse.failure(
                                    result.message(),
                                    HttpStatus.BAD_REQUEST
                            )
                    );
        }

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        result.message(),
                        null,
                        HttpStatus.OK
                )
        );
    }
    
    
    @PostMapping("/register")
    public ResponseEntity<CustomApiResponse<?>> register(
            @RequestBody UserRegistrationRequest request) {

        try {
            log.info("Register API called for email: {}", request.getEmail());

            registrationService.register(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success(
                            "Registration successful. Please check your email to activate your account.",
                            null,
                            HttpStatus.CREATED
                    ));

        } catch (RuntimeException e) {

            log.error("Business error during registration: ", e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (Exception e) {

            log.error("Unexpected error during registration: ", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to register user. Please try again later.",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    @PostMapping("/activate")
    public ResponseEntity<CustomApiResponse<?>> activateAccount(
            @RequestBody ActivationRequest request) {

        accountActivationService.activateAccount(request.getToken());

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Your account has been activated successfully. You can now log in.",
                        null,
                        HttpStatus.OK
                )
        );
    }
    
    
    
    private void validatePortalAccess(String portal, CustomUserDetails userDetails) {

    	logger.info("validatePortalAccess:::::::::::::::::::::::{}",portal);
        if (portal == null) {
            throw new IllegalStateException("Portal is required");
        }

        PortalType portalType;
        try {
            portalType = PortalType.valueOf(portal.toUpperCase());
            logger.info("portalType:::::::::::::::::::{}",portalType);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid portal type");
        }

        switch (portalType) {

            case COMPANY:
            case EMPLOYER: // if both behave same
                employeeRepository
                    .findByUserUserIdAndStatus(
                            userDetails.getUserId(),
                            "ACTIVE"
                    )
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "User is not an active employee"
                            )
                    );
                break;

            case CANDIDATE:
                // TODO: validate candidate table
                break;

            case VENDOR:
                // TODO: validate vendor table
                break;

            case ADMIN:
                // TODO: role/permission validation
                break;

            default:
                throw new IllegalStateException("Invalid portal access");
        }
    }

}