package com.org.bgv.config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.org.bgv.common.navigation.PortalType;
import com.org.bgv.company.repository.EmployeeRepository;
import com.org.bgv.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PortalAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;

    @Override
    public Authentication authenticate(Authentication authentication) {

        PortalAuthenticationToken token = (PortalAuthenticationToken) authentication;

        String email = token.getName();
        String rawPassword = token.getCredentials().toString();
        PortalType portal = token.getPortal();

        log.info("🔐 Login attempt | email={} | portal={}", email, portal);

        CustomUserDetails user =
                (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            log.warn("❌ Invalid credentials | {}", email);
            throw new BadCredentialsException("Invalid credentials");
        }

        // 🔐 PORTAL RULES — SINGLE SOURCE OF TRUTH
        switch (portal) {
            case COMPANY:
            case EMPLOYER:
                employeeRepository
                    .findByUserUserIdAndStatus(user.getUserId(), "ACTIVE")
                    .orElseThrow(() -> {
                        log.warn("❌ Not active employee | userId={}", user.getUserId());
                        return new AccessDeniedException("User is not an active employee");
                    });
                break;

            case USER:
            case ADMIN:
            case VENDOR:
            case CANDIDATE:
                // allowed (future validation here)
                break;

            default:
                throw new AccessDeniedException("Invalid portal");
        }

        log.info("✅ Authentication success | userId={} | portal={}",
                user.getUserId(), portal);

        // RETURN AUTHENTICATED TOKEN
        return new PortalAuthenticationToken(user, portal);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PortalAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
