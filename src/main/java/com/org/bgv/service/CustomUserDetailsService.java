package com.org.bgv.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.entity.User;
import com.org.bgv.repository.UserRepository;



@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true) // Add this annotation
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🔍 UserDetailsService - Loading user by email: " + email);
        
        try {
            // Use a custom query with JOIN FETCH to load roles eagerly
            User user = userRepository.findByEmailWithRoles(email)
                    .orElseThrow(() -> {
                        System.out.println("❌ User not found with email: " + email);
                        return new UsernameNotFoundException("User not found with email: " + email);
                    });
            
            System.out.println("✅ User found: " + user.getEmail());
            System.out.println("✅ User roles count: " + (user.getRoles() != null ? user.getRoles().size() : 0));
            
            // Convert UserRoles to Spring Security authorities
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(userRole -> {
                        String roleName = userRole.getRole().getName();
                        System.out.println("✅ User role: " + roleName);
                        return new SimpleGrantedAuthority(roleName);
                    })
                    .collect(Collectors.toList());
            
            System.out.println("✅ Authorities: " + authorities);
            
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    authorities
            );
            
        } catch (Exception e) {
            System.out.println("❌ Error in UserDetailsService: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}

