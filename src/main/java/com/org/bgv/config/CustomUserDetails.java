package com.org.bgv.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
	private Long userId;
	private Long companyId;

	private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    
    // Extra fields
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean active;
    private String department;
    private String userType;
    
   
}
