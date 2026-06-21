package com.org.bgv.onboarding.service;

import org.springframework.stereotype.Service;

import com.org.bgv.common.RoleConstants;
import com.org.bgv.constants.UserStatus;
import com.org.bgv.entity.CompanyUser;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.onboarding.dto.CompanyAdminResponse;
import com.org.bgv.onboarding.dto.CreateCompanyAdminRequest;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.CompanyUserRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.UserRoleRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
public class CompanyAdminService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProfileRepository profileRepository;
    private final CompanyUserRepository companyUserRepository;

    @Transactional
    public CompanyAdminResponse createAdminUser(
            Long companyId,
            CreateCompanyAdminRequest request) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
/*
        Role role;

        switch (company.getCompanyType()) {

            case VENDOR:
                role = roleRepository
                        .findByType(RoleConstants.TYPE_VENDOR)
                        .stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException("Vendor role not found"));
                break;

            case EMPLOYER:
                role = roleRepository
                        .findByType(RoleConstants.TYPE_EMPLOYER)
                        .stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException("Employer role not found"));
                break;

            default:
                throw new RuntimeException(
                        "Unsupported company type: "
                                + company.getCompanyType());
        }
        
        */
        
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + request.getRoleId()));

        // Create user
        User user = new User();

        user.setEmail(request.getEmail());
        user.setPassword(null);
        user.setStatus(UserStatus.PENDING_ACTIVATION);

        user = userRepository.save(user);

        // Create profile
        Profile profile = new Profile();

        profile.setUser(user);
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhoneNumber(request.getPhoneNumber());

        profileRepository.save(profile);

        // Assign role
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        userRoleRepository.save(userRole);

        user.getRoles().add(userRole);
        
        CompanyUser companyUser = new CompanyUser();
        companyUser.setCompany(company);
        companyUser.setUser(user);

        companyUserRepository.save(companyUser);

        return CompanyAdminResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(role.getName())
                .status(user.getStatus().name())
                .build();
    }
}