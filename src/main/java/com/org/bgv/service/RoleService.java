package com.org.bgv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.org.bgv.common.RoleConstants;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.CandidateStatus;
import com.org.bgv.constants.Constants;
import com.org.bgv.controller.RoleController;
import com.org.bgv.entity.Candidate;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.mapper.RoleMapper;
import com.org.bgv.repository.CandidateRepository;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.CompanyUserRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.UserRoleRepository;
import com.org.bgv.role.dto.RoleCreateRequest;
import com.org.bgv.role.dto.RoleDetailDto;
import com.org.bgv.role.dto.RoleDto;
import com.org.bgv.role.dto.RoleResponse;

import lombok.Builder;
import lombok.RequiredArgsConstructor;


@Builder
@RequiredArgsConstructor
@Service
public class RoleService {

	private static final Logger log = LoggerFactory.getLogger(RoleService.class);
	
	private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleMapper roleMapper;
    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;

    
    public RoleDto createRole(RoleCreateRequest request) {
        // Check if role with same name already exists
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Role with name " + request.getName() + " already exists");
        }

        Role role = Role.builder()
                .name(request.getName())
                .label(request.getLabel())
                .type(request.getType())
                .build();

       
        Role savedRole = roleRepository.save(role);
        
        return roleMapper.toDto(savedRole);
    }

    
    public Optional<RoleDto> getRoleByName(String name) {
        return roleRepository.findByName(name)
                .map(roleMapper::toDto);
    }

   
    public List<RoleDto> getAllRoles() {
    	
    	
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    public RoleDto updateRole(Long id, RoleCreateRequest request) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        // Check if name is being changed and if new name already exists
        if (!existingRole.getName().equals(request.getName()) && 
            roleRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Role with name " + request.getName() + " already exists");
        }

        roleMapper.updateEntityFromRequest(request, existingRole);
        Role updatedRole = roleRepository.save(existingRole);
        
        return roleMapper.toDto(updatedRole);
    }

    public List<RoleResponse> getAllRolesGroupedByType() {
    	
    	List<Role> allRoles = null;
    	
    	if (SecurityUtils.hasRole("Administrator")) {
    		 
    		allRoles = roleRepository.findAll();
    	 }else if(SecurityUtils.hasRole("Company Administrator")) {
    		 allRoles = roleRepository.findByType(RoleConstants.TYPE_COMPANY);
    	 }
    	
        
        
        // Group roles by type and convert to RoleTypeGroupDto
        List<RoleResponse> roleGroups = allRoles.stream()
                .collect(Collectors.groupingBy(Role::getType))
                .entrySet()
                .stream()
                .map(entry -> {
                    Long type = entry.getKey();
                    List<Role> roles = entry.getValue();
                    
                    String roleType = mapConstantToRoleType(type);
                    String typeLabel = getTypeLabelByConstant(type);
                    
                    List<RoleDetailDto> roleDetailDtos = roles.stream()
                            .map(role -> {
                                Integer assignedCount = userRoleRepository.countByRole(role);
                                return roleMapper.toDetailDto(role, assignedCount);
                            })
                            .collect(Collectors.toList());
                    
                    return RoleResponse.builder()
                            .roleType(roleType)
                            .label(typeLabel)
                            .roles(roleDetailDtos)
                            .build();
                })
                .collect(Collectors.toList());
        
        return roleGroups;
    }
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        
        // Check if role is assigned to any user
        List<UserRole> userRoles = userRoleRepository.findByRole(role);
        if (!userRoles.isEmpty()) {
            throw new RuntimeException("Cannot delete role. It is assigned to " + userRoles.size() + " users.");
        }
        
        roleRepository.delete(role);
    }

    public RoleResponse getRolesByType(String roleType) {
        Long type = mapRoleTypeToConstant(roleType);
        String typeLabel = getTypeLabel(roleType);
        
        List<Role> roles = roleRepository.findByType(type);
        
        List<RoleDetailDto> roleDetailDtos = roles.stream()
                .map(role -> {
                    // Count users with this role
                    Integer assignedCount = userRoleRepository.countByRole(role);
                    return roleMapper.toDetailDto(role, assignedCount);
                })
                .collect(Collectors.toList());
        
        return RoleResponse.builder()
                .roleType(roleType)
                .label(typeLabel)
                .roles(roleDetailDtos)
                .build();
    }

    public Optional<RoleDto> getRoleById(Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDto);
    }

    private Long mapRoleTypeToConstant(String roleType) {
        switch (roleType.toLowerCase()) {
            case "regular":
                return RoleConstants.TYPE_REGULAR;
            case "company":
                return RoleConstants.TYPE_COMPANY;
            case "vendor":
                return RoleConstants.TYPE_VENDOR;
            default:
                return RoleConstants.TYPE_REGULAR;
        }
    }

    private String getTypeLabel(String roleType) {
        switch (roleType.toLowerCase()) {
            case "regular":
                return RoleConstants.TYPE_REGULAR_LABEL;
            case "company":
                return RoleConstants.TYPE_COMPANY_LABEL;
            case "vendor":
                return RoleConstants.TYPE_VENDOR_LABEL;
            default:
                return RoleConstants.TYPE_REGULAR_LABEL;
        }
    }
    private String mapConstantToRoleType(Long type) {
        if (RoleConstants.TYPE_REGULAR.equals(type)) {
            return "regular";
        } else if (RoleConstants.TYPE_COMPANY.equals(type)) {
            return "company";
        } else if (RoleConstants.TYPE_VENDOR.equals(type)) {
            return "vendor";
        } else {
            return "regular";
        }
    }
    private String getTypeLabelByConstant(Long type) {
        if (RoleConstants.TYPE_REGULAR.equals(type)) {
            return RoleConstants.TYPE_REGULAR_LABEL;
        } else if (RoleConstants.TYPE_COMPANY.equals(type)) {
            return RoleConstants.TYPE_COMPANY_LABEL;
        } else if (RoleConstants.TYPE_VENDOR.equals(type)) {
            return RoleConstants.TYPE_VENDOR_LABEL;
        } else {
            return RoleConstants.TYPE_REGULAR_LABEL;
        }
    }
    
    public List<RoleDto> getUserRoles(Long userId) {
        try {
            List<Role> roles = getRolesByUserId(userId);
            return roles.stream()
            		 .map(roleMapper::toDto)
                    .toList();
        } catch (Exception e) {
            // Log the error
            System.err.println("Error fetching roles for user " + userId + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }
    public List<Role> getRolesByUserId(Long userId) {
        // Optional: Check if user exists first
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        
        return roleRepository.findRolesByUserId(userId);
    }
    
    /**
     * Update user roles by adding new roles and removing specified roles
     */
    public void updateUserRoles(Long userId, List<Long> roleIdsToAdd, List<Long> roleIdsToRemove) {
        log.info("Updating roles for user ID: {}, roles to add: {}, roles to remove: {}", 
                userId, roleIdsToAdd, roleIdsToRemove);

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Process role additions
        if (roleIdsToAdd != null && !roleIdsToAdd.isEmpty()) {
            addRolesToUser(user, roleIdsToAdd);
        }

        // Process role removals
        if (roleIdsToRemove != null && !roleIdsToRemove.isEmpty()) {
            removeRolesFromUser(userId, roleIdsToRemove);
        }

        log.info("Successfully updated roles for user: {}", user.getEmail());
    }

    /**
     * Add roles to user
     */
    private void addRolesToUser(User user, List<Long> roleIdsToAdd) {
        List<Role> rolesToAdd = roleRepository.findAllById(roleIdsToAdd);
        
        // Check if all roles exist
        if (rolesToAdd.size() != roleIdsToAdd.size()) {
            List<Long> foundRoleIds = rolesToAdd.stream().map(Role::getId).toList();
            List<Long> missingRoleIds = roleIdsToAdd.stream()
                    .filter(roleId -> !foundRoleIds.contains(roleId))
                    .toList();
            throw new RuntimeException("Roles not found with IDs: " + missingRoleIds);
        }

        List<UserRole> newUserRoles = new ArrayList<>();
        for (Role role : rolesToAdd) {
            // Check if role already assigned using custom query
            if (!userRoleRepository.existsByUserIdAndRoleId(user.getUserId(), role.getId())) {
                UserRole userRole = UserRole.builder()
                        .user(user)
                        .role(role)
                        .build();
                newUserRoles.add(userRole);
                // if role is candidate then create candidate object
                if(role.getName().contains(RoleConstants.ROLE_CANDIDATE)) {
                	Candidate candidate = Candidate.builder()
                	.user(user)
                	//.createdAt(new Localdatet)
                	.isActive(Boolean.FALSE)
                	.isVerified(Boolean.FALSE)
                	.sourceType(Constants.CANDIDATE_SOURCE_ADMIN)
                	.build();
                	candidateRepository.save(candidate);
                	
                }
                log.debug("Adding role '{}' to user '{}'", role.getName(), user.getEmail());
            } else {
                log.debug("Role '{}' already assigned to user '{}'", role.getName(), user.getEmail());
            }
        }

        if (!newUserRoles.isEmpty()) {
            userRoleRepository.saveAll(newUserRoles);
            log.info("Added {} roles to user: {}", newUserRoles.size(), user.getEmail());
        }
    }

    /**
     * Remove roles from user
     */
    private void removeRolesFromUser(Long userId, List<Long> roleIdsToRemove) {
        // Use the custom repository method
        List<UserRole> userRolesToRemove = userRoleRepository.findByUserIdAndRoleIds(userId, roleIdsToRemove);
        
        if (!userRolesToRemove.isEmpty()) {
            userRoleRepository.deleteAll(userRolesToRemove);
            log.info("Removed {} roles from user ID: {}", userRolesToRemove.size(), userId);
            
            // Log the removed roles for auditing
            userRolesToRemove.forEach(userRole -> 
                log.debug("Removed role '{}' from user '{}'", 
                        userRole.getRole().getName(), userRole.getUser().getEmail()));
        } else {
            log.debug("No roles to remove for user ID: {}", userId);
        }
    }
}
