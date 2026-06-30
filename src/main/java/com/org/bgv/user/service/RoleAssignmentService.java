package com.org.bgv.user.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.org.bgv.common.RoleConstants;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRoleRepository;
import com.org.bgv.user.requests.RegistrationContext;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleAssignmentService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional
    public void assignRoles(User user, RegistrationContext context) {

        if (context.getRoles() == null || context.getRoles().isEmpty()) {
            return;
        }

        for (String roleName : context.getRoles()) {

            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() ->
                            new BusinessException("Role not found : " + roleName));

            boolean exists = userRoleRepository.existsByUserUserIdAndRoleId(
                    user.getUserId(),
                    role.getId());

            if (!exists) {
                userRoleRepository.save(
                        UserRole.builder()
                                .user(user)
                                .role(role)
                                .build()
                );
            }
        }
    }
    
    public void assignRoles(User user, Set<String> roles) {

        for (String roleName : roles) {

            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() ->
                            new BusinessException("Role not found: " + roleName));

            if (!userRoleRepository.existsByUserUserIdAndRoleId(
                    user.getUserId(), role.getId())) {

                userRoleRepository.save(
                    UserRole.builder()
                            .user(user)
                            .role(role)
                            .build()
                );
            }
        }
    }

   
}
