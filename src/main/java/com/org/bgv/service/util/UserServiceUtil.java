package com.org.bgv.service.util;

import java.util.List;

import org.springframework.stereotype.Service;

import com.org.bgv.common.RoleConstants;
import com.org.bgv.common.UserDto;
import com.org.bgv.config.CustomUserDetails;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.exceptions.ResourceNotFoundException;
import com.org.bgv.mapper.UserMapper;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.CompanyUserRepository;
import com.org.bgv.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceUtil {
	
	
	private final UserRepository userRepository;
	
	private final UserMapper userMapper;
	
	private final CompanyRepository companyRepository;
	private final CompanyUserRepository companyUserRepository;
	
	
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
	
	 public boolean hasRole(CustomUserDetails user, String role) {
		    return user.getAuthorities().stream()
		            .anyMatch(a -> a.getAuthority().equals(role));
		}
	

	public List<UserDto> getVendorAgentUsersToAssign(Long companyId) {

			    Company company = companyRepository.findById(companyId)
			            .orElseThrow(() ->
			                    new ResourceNotFoundException(
			                            "Company not found with id: " + companyId));

			    List<User> vendorAgentUsersList =
			            companyUserRepository.findUsersByCompanyIdAndRole(
			                    companyId, RoleConstants.ROLE_VENDOR_AGENT);
			    
			    log.info("vendorAgentUsersList::::::::::::::::::::::::::{}",vendorAgentUsersList.size());

			    return vendorAgentUsersList.stream()
			            .map(userMapper::toDto)
			            .toList(); 
			}

}
