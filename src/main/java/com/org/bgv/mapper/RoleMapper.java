package com.org.bgv.mapper;


import com.org.bgv.entity.Role;
import com.org.bgv.role.dto.RoleCreateRequest;
import com.org.bgv.role.dto.RoleDetailDto;
import com.org.bgv.role.dto.RoleDto;

import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public Role toEntity(RoleCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Role.builder()
                .name(request.getName())
                .label(request.getLabel())
                .type(request.getType())
                .build();
    }

    public RoleDto toDto(Role role) {
        if (role == null) {
            return null;
        }

        return RoleDto.builder()
                .roleid(role.getId())
                .name(role.getName())
                .label(role.getLabel())
                .type(role.getType())
                .build();
    }

    public RoleDetailDto toDetailDto(Role role, Integer assignedCount) {
        if (role == null) {
            return null;
        }

        return RoleDetailDto.builder()
                .roleid(role.getId())
                .name(role.getName())
                .label(role.getLabel())
                .assigned(assignedCount != null ? assignedCount : 0)
                .build();
    }

    public void updateEntityFromRequest(RoleCreateRequest request, Role role) {
        if (request == null || role == null) {
            return;
        }

        role.setName(request.getName());
        role.setLabel(request.getLabel());
        role.setType(request.getType());
    }
}
