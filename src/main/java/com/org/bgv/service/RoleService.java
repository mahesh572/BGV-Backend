package com.org.bgv.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.org.bgv.common.RoleConstants;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.UserRole;
import com.org.bgv.mapper.RoleMapper;
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

	
	private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleMapper roleMapper;

    
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
        // Get all roles
        List<Role> allRoles = roleRepository.findAll();
        
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
}
