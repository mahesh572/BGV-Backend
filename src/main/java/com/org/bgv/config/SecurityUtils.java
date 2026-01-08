package com.org.bgv.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    // Get current username (email in your case)
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }

    // Get current authentication
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // Check if user is authenticated
    public static boolean isAuthenticated() {
        Authentication authentication = getCurrentAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !authentication.getPrincipal().equals("anonymousUser");
    }

    // Check if user has specific role/authority
    public static boolean hasRole(String role) {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
        }
        return false;
    }

    // Check if user has any of the given roles
    public static boolean hasAnyRole(String... roles) {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            for (String role : roles) {
                if (authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(role))) {
                    return true;
                }
            }
        }
        return false;
    }

    // Get current user details (generic UserDetails)
    public static UserDetails getCurrentUserDetails() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && 
            authentication.isAuthenticated() && 
            authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }

    // Get current CustomUserDetails specifically
    public static CustomUserDetails getCurrentCustomUserDetails() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && 
            authentication.isAuthenticated() && 
            authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    // Get current user's email
    public static String getCurrentUserEmail() {
        CustomUserDetails userDetails = getCurrentCustomUserDetails();
        return userDetails != null ? userDetails.getEmail() : null;
    }

    // Get current user's company ID
    public static Long getCurrentUserCompanyId() {
        CustomUserDetails userDetails = getCurrentCustomUserDetails();
        return userDetails != null ? userDetails.getCompanyId() : null;
    }

    // Check if current user is enabled/active
    public static boolean isCurrentUserEnabled() {
        CustomUserDetails userDetails = getCurrentCustomUserDetails();
        return userDetails != null && userDetails.isEnabled();
    }

    // Get current user ID (if you add this field to CustomUserDetails)
    public static Long getCurrentUserId() {
        CustomUserDetails userDetails = getCurrentCustomUserDetails();
        return userDetails != null ? userDetails.getUserId() : null;
    }

    // Get current user's authorities as string list
    public static java.util.List<String> getCurrentUserAuthorities() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(java.util.stream.Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    // Check if current user has company ID
    public static boolean hasCompany() {
        return getCurrentUserCompanyId() != null;
    }

    // Validate if current user belongs to specific company
    public static boolean belongsToCompany(String companyId) {
        Long currentUserCompanyId = getCurrentUserCompanyId();
        return currentUserCompanyId != null && currentUserCompanyId.equals(companyId);
    }
}