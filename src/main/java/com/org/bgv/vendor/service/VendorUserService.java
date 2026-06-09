package com.org.bgv.vendor.service;

import java.util.List;

import org.apache.catalina.security.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.org.bgv.common.CommonUtils;
import com.org.bgv.common.entity.StateRegion;
import com.org.bgv.common.repository.StateRegionRepository;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.dto.CreateVendorUserRequest;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.CompanyUser;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.notifications.service.NotificationDispatcher;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.CompanyUserRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.UserRoleRepository;
import com.org.bgv.vendor.entity.VendorUser;
import com.org.bgv.vendor.repository.VendorUserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VendorUserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final VendorUserRepository vendorUserRepository;
    private final RoleRepository roleRepository;
    private final StateRegionRepository stateRegionRepository;
    private final UserRoleRepository userRoleRepository;
    private final CompanyUserRepository companyUserRepository;
    private final CompanyRepository companyRepository;
    private final NotificationDispatcher notificationDispatcher;
    private final PasswordEncoder passwordEncoder;

    
    @Transactional
    public VendorUser createVendorUser(CreateVendorUserRequest request) {

        log.info("Creating vendor user for email={}", request.getEmail());

        Long companyId = SecurityUtils.getCurrentUserCompanyId();

        log.info("Current company id={}", companyId);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + companyId));

        String tempPassword = CommonUtils.generateTempPassword();

        log.info("Generated temporary password for user={}", request.getEmail());

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .status(request.getStatus())
                .passwordResetrequired(request.getPasswordResetRequired())
                .password(passwordEncoder.encode(tempPassword))
                .build();

        user = userRepository.save(user);

        log.info("User created successfully. userId={}", user.getUserId());

        // Create profile
        Profile profile = Profile.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .nationality(request.getNationality())
                .dateOfBirth(request.getDateOfBirth())
                .status("ACTIVE")
                .build();

        profileRepository.save(profile);

        log.info("Profile created for userId={}", user.getUserId());

        // Assign roles
        List<Role> roles = roleRepository.findAllById(request.getRoleIds());

        log.info("Assigning {} roles to userId={}", roles.size(), user.getUserId());

        for (Role role : roles) {

            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();

            userRoleRepository.save(userRole);

            log.debug("Assigned role {} to userId={}",
                    role.getName(),
                    user.getUserId());
        }

        // Company mapping
        CompanyUser companyUser = CompanyUser.builder()
                .company(company)
                .user(user)
                .build();

        companyUserRepository.save(companyUser);

        log.info("Mapped userId={} to companyId={}",
                user.getUserId(),
                company.getId());

        // Regions
        List<StateRegion> regions =
                stateRegionRepository.findAllById(request.getRegionIds());

        log.info("Assigning {} regions to vendor user", regions.size());

        // Vendor user
        VendorUser vendorUser = VendorUser.builder()
                .user(user)
                .operationalRole(request.getOperationalRole())
                .experienceYears(request.getExperienceYears())
                .fieldVerificationAvailable(request.getFieldVerificationAvailable())
                .vehicleAvailable(request.getVehicleAvailable())
                .maxDailyCapacity(request.getMaxDailyCapacity())
                .regions(regions)
                .build();

        VendorUser savedVendorUser = vendorUserRepository.save(vendorUser);

        log.info("Vendor user created successfully. vendorUserId={}",
                savedVendorUser.getId());

        notificationDispatcher.dispatchUserCreatedNotification(
                company,
                profile,
                user,
                tempPassword);

        log.info("User created notification dispatched for userId={}",
                user.getUserId());

        return savedVendorUser;
    }
}
