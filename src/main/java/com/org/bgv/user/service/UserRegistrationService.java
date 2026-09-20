package com.org.bgv.user.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.common.RoleConstants;
import com.org.bgv.common.UserDto;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.User;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.mapper.UserMapper;
import com.org.bgv.notifications.service.NotificationDispatcher;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.service.CandidateService;
import com.org.bgv.service.ProfileService;
import com.org.bgv.service.UserService;
import com.org.bgv.service.util.UserServiceUtil;
import com.org.bgv.user.requests.RegistrationContext;
import com.org.bgv.user.requests.UserRegistrationRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationService {

    private final RegistrationContextResolver contextResolver;
    private final UserService userService;
    private final ProfileService profileService;
    private final UserRepository userRepository;
    private final CompanyMembershipService companyMembershipService;
    private final RoleAssignmentService roleAssignmentService;
    private final CandidateService candidateService;
    private final UserMapper userMapper;
    private final NotificationDispatcher notificationDispatcher;
    private final CandidateRepository candidateRepository;

    @Transactional
    public UserDto register(UserRegistrationRequest request) {

        log.info("Starting user registration");

        try {
            User currentUser = null;

            Long userId = SecurityUtils.getCurrentUserId();

            log.debug("Current authenticated user id: {}", userId);

            if (userId != null) {
                currentUser = userRepository.findById(userId)
                        .orElseThrow(() -> {
                            log.error("Authenticated user not found for userId: {}", userId);
                            return new BusinessException("User not found");
                        });

                log.debug("Authenticated user found. userId: {}", userId);
            } else {
                log.debug("No authenticated user found. Processing as self-registration");
            }

            // Resolve registration context
            RegistrationContext context = contextResolver.resolve(currentUser);

            log.info(
                    "Registration context resolved. companyId: {}, roles: {}",
                    context.getCompany() != null ? context.getCompany().getId() : null,
                    context.getRoles()
            );

            // Create user
            User user = userService.createUser(request, context);

            log.info("User created successfully. userId: {}", user.getUserId());

            // Create profile
            Profile profile = profileService.createProfile(
                    user,
                    request,
                    context
            );

            log.info(
                    "Profile created successfully. profileId: {}, userId: {}",
                    profile.getProfileId(),
                    user.getUserId()
            );

            // Assign user to company
            companyMembershipService.assignUserToCompany(
                    user,
                    context.getCompany()
            );

            log.info(
                    "User assigned to company successfully. userId: {}, companyId: {}",
                    user.getUserId(),
                    context.getCompany() != null
                            ? context.getCompany().getId()
                            : null
            );

            // Add candidate role
         // Add candidate role
            Set<String> roles = context.getRoles() != null
                    ? new HashSet<>(context.getRoles())
                    : new HashSet<>();

            log.info(
                    "Registration context roles before candidate role. userId: {}, roles: {}",
                    user.getUserId(),
                    roles
            );

            roles.add(RoleConstants.ROLE_CANDIDATE);

            log.info(
                    "Candidate role added. userId: {}, roles: {}",
                    user.getUserId(),
                    roles
            );

            // Create candidate
            Candidate candidate = candidateService.createCandidateforSelf(
                    user,
                    context.getCompany()
            );

            log.info(
                    "Candidate created successfully. candidateId: {}, userId: {}",
                    candidate.getCandidateId(),
                    user.getUserId()
            );

            // Populate candidate details from profile
            candidate.setFirstName(profile.getFirstName());
            candidate.setLastName(profile.getLastName());
            candidate.setPhoneNumber(profile.getPhoneNumber());

            candidateRepository.save(candidate);

            log.debug(
                    "Candidate details updated successfully. candidateId: {}",
                    candidate.getCandidateId()
            );

            // Update context roles
            context.setRoles(roles);

            // Assign roles
            roleAssignmentService.assignRoles(
                    user,
                    context
            );

            log.info(
                    "Roles assigned successfully. userId: {}, roles: {}",
                    user.getUserId(),
                    roles
            );

            // Send activation notification
            notificationDispatcher.dispatchUserAccountActivationNotification(
                    user,
                    context.getCompany()
            );

            log.info(
                    "User account activation notification dispatched. userId: {}, companyId: {}",
                    user.getUserId(),
                    context.getCompany() != null
                            ? context.getCompany().getId()
                            : null
            );

            log.info(
                    "User registration completed successfully. userId: {}",
                    user.getUserId()
            );

            return userMapper.toDto(user);

        } catch (BusinessException e) {

            log.warn(
                    "Business error during user registration. message: {}",
                    e.getMessage(),
                    e
            );

            throw e;

        } catch (Exception e) {

            log.error(
                    "Unexpected error during user registration",
                    e
            );

            throw new BusinessException(
                    "Unable to complete user registration"
            );
        }
    }
}
