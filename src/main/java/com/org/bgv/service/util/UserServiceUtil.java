package com.org.bgv.service.util;

import org.springframework.stereotype.Service;

import com.org.bgv.common.UserDto;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.exceptions.ResourceNotFoundException;
import com.org.bgv.mapper.UserMapper;
import com.org.bgv.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceUtil {
	
	
	private final UserRepository userRepository;
	
	private final UserMapper userMapper;
	
	
	public UserDto getUserDetails(Long userId) {

	    User user = userRepository.findById(userId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "User not found with id: " + userId));

	    return userMapper.toDto(user);
	}
	
	
	 public boolean hasRole(Long userId, String roleName) {

	        User user = userRepository.findById(userId)
	                .orElseThrow(() ->
	                        new ResourceNotFoundException(
	                                "User not found with id: " + userId));

	        return user.getRoles()
	                .stream()
	                .map(UserRole::getRole)
	                .map(Role::getName)
	                .anyMatch(role -> role.equalsIgnoreCase(roleName));
	    }
	

}
