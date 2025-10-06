package com.org.bgv.service;

import com.org.bgv.config.JwtUtil;
import com.org.bgv.dto.UserDto;
import com.org.bgv.entity.CompanyUser;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.mapper.UserMapper;
import com.org.bgv.repository.CompanyUserRepository;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtUtil jwtUtil;
    private final ProfileService profileService;
    private final CompanyUserRepository companyUserRepository;

    public List<UserDto> getAll() {
        try {
            return userRepository.findAll()
                    .stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch users: " + e.getMessage(), e);
        }
    }

    public UserDto getById(Long id) {
        try {
        	UserDto userDto = userRepository.findById(id)
                    .map(userMapper::toDto)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        	userDto.setRoles(getUserRoles(id));
        	
            return userDto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user: " + e.getMessage(), e);
        }
    }

    public UserDto create(UserDto userDto) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new RuntimeException("User with email " + userDto.getEmail() + " already exists");
            }

            User user = userMapper.toEntity(userDto);
            User saved = userRepository.save(user);
            return userMapper.toDto(saved);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    public UserDto update(Long id, UserDto userDto) {
        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

            // Check if email is being changed and if it already exists for another user
            if (!existingUser.getEmail().equals(userDto.getEmail()) && 
                userRepository.existsByEmailAndUserIdNot(userDto.getEmail(), id)) {
                throw new RuntimeException("Email " + userDto.getEmail() + " already exists for another user");
            }

            // Update fields
            existingUser.setFirstName(userDto.getFirstName());
            existingUser.setLastName(userDto.getLastName());
            existingUser.setEmail(userDto.getEmail());
            
            // Update other fields as needed
            if (userDto.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(userDto.getPhoneNumber());
            }
			/*
			 * if (userDto.getDateOfBirth() != null) {
			 * existingUser.setDateOfBirth(userDto.getDateOfBirth()); }
			 */

            User updated = userRepository.save(existingUser);
            return userMapper.toDto(updated);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    public void delete(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                throw new RuntimeException("User not found with id: " + id);
            }
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    public UserDto getUserByEmail(String email) {
    	UserDto userDto=null;
    	try {
        	
    		userDto = userRepository.findByEmail(email)
                    .map(userMapper::toDto)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            userDto.setRoles(getUserRoles(userDto.getUserId()));
            return userDto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user by email: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public void assignRoleToUser(Long userId, String roleName) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName));

            // Check if user already has this role
            boolean alreadyHasRole = user.getRoles().stream()
                    .anyMatch(userRole -> userRole.getRole().getName().equals(roleName));
            
            if (alreadyHasRole) {
                throw new RuntimeException("User already has role: " + roleName);
            }

            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();

            user.getRoles().add(userRole);
            userRoleRepository.save(userRole);
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign role to user: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void removeRoleFromUser(Long userId, String roleName) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            UserRole userRole = user.getRoles().stream()
                    .filter(ur -> ur.getRole().getName().equals(roleName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User does not have role: " + roleName));

            user.getRoles().remove(userRole);
            userRoleRepository.delete(userRole);
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove role from user: " + e.getMessage(), e);
        }
    }

    public List<String> getUserRoles(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            return user.getRoles().stream()
                    .map(userRole -> userRole.getRole().getName())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user roles: " + e.getMessage(), e);
        }
    }

    public boolean userExists(Long id) {
        try {
            return userRepository.existsById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to check user existence: " + e.getMessage(), e);
        }
    }
    public UserDto getUserFromToken(String token) {
        UserDto userDto = null;

        if (jwtUtil.validateToken(token)) {
            String email = jwtUtil.getUsernameFromToken(token);
            userDto = getUserByEmail(email);

            // ✅ Try setting profileId, skip if not found
            try {
                Long profileId = profileService.getProfileIdByUserId(userDto.getUserId());
                if (profileId != null) {
                    userDto.setProfileId(profileId);
                }
            } catch (Exception e) {
                System.out.println("⚠️ No profile found for userId: " + userDto.getUserId());
            }

            // ✅ Set company info if available
            List<CompanyUser> companyUsers = companyUserRepository.findByUserUserId(userDto.getUserId());
            if (companyUsers != null && !companyUsers.isEmpty()) {
                userDto.setCompanyId(companyUsers.get(0).getCompanyId());
            }
        }

        return userDto;
    }

}