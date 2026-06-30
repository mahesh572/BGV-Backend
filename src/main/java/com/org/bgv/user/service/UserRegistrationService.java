package com.org.bgv.user.service;

import org.springframework.stereotype.Service;

import com.org.bgv.common.UserDto;
import com.org.bgv.config.SecurityUtils;
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

@Service
@RequiredArgsConstructor
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

    @Transactional
    public UserDto register(UserRegistrationRequest request) {
    	
    	User currentUser = null;
    	
    	 Long userId = SecurityUtils.getCurrentUserId();

         if (userId != null) {
             currentUser = userRepository.findById(userId)
                     .orElseThrow(() -> new BusinessException("User not found"));
         }


        RegistrationContext context =
                contextResolver.resolve(currentUser);

        User user = userService.createUser(request, context);

       
        profileService.createProfile(user, request,context);
        
       

        companyMembershipService.assignUserToCompany(
                user,
                context.getCompany());
        
       

        roleAssignmentService.assignRoles(
                user,
                context);
        
        notificationDispatcher.dispatchUserAccountActivationNotification(user, context.getCompany());
        
        /*

        if (request.isCandidate()) {
            candidateService.createCandidate(user, context);
        }
*/
        
        
        return userMapper.toDto(user);
    }
}
