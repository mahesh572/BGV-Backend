package com.org.bgv.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.common.RoleConstants;
import com.org.bgv.config.CustomUserDetails;
import com.org.bgv.entity.CompanyUser;
import com.org.bgv.entity.User;
import com.org.bgv.repository.CompanyUserRepository;
import com.org.bgv.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final CompanyUserRepository companyUserRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("Loading user by email: {}", email);

        try {
            User user = userRepository.findByEmailWithRoles(email)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "User not found with email: " + email
                            )
                    );

            log.info("User found: {}", user.getEmail());

            // 🔐 Map roles → authorities
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(userRole -> {
                        String roleName = userRole.getRole().getName();
                        log.debug("Mapping role: {}", roleName);
                        return new SimpleGrantedAuthority(roleName);
                    })
                    .collect(Collectors.toList());

            boolean isAdmin = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals(RoleConstants.ADMINISTRATOR));

            Long companyId = null;

            // 🏢 Resolve company ONLY if needed
            if (!isAdmin) {
                companyId = getCompanyIdForUser(user);
            }

            log.info(
                "User authenticated | email={} | admin={} | companyId={}",
                email, isAdmin, companyId
            );

            return buildCustomUserDetails(user, authorities, companyId);

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error loading user by email: {}", email, e);
            throw new UsernameNotFoundException(
                    "Error loading user: " + e.getMessage(), e
            );
        }
    }

    private Long getCompanyIdForUser(User user) {
        try {
            List<CompanyUser> companyUsers = companyUserRepository.findByUserUserId(user.getUserId());
            if (companyUsers != null && !companyUsers.isEmpty()) {
                return companyUsers.get(0).getCompanyId();
            }
            log.warn("No company found for user: {}", user.getEmail());
            return null;
        } catch (Exception e) {
            log.error("Error fetching company for user: {}", user.getEmail(), e);
            e.printStackTrace();
            return null;
        }
    }
    
    private CustomUserDetails buildCustomUserDetails(User user, List<GrantedAuthority> authorities, Long companyId) {
        return CustomUserDetails.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .companyId(companyId)
                .userId(user.getUserId())
                .username(user.getEmail())
                .userType(user.getUserType())
             //   .enabled(user.isActive()) // Make sure you have this field in User entity
                .build();
    }
}