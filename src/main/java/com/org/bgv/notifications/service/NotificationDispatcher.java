package com.org.bgv.notifications.service;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.org.bgv.auth.service.ResetTokenService;
import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.company.entity.Employee;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.User;
import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.dto.NotificationPlaceholder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatcher {

  //  private final PlaceholderResolver placeholderResolver;
 //   private final RecipientResolver recipientResolver;
 //   private final NotificationTemplateService templateService;
 //   private final EmailNotificationChannel emailChannel;
	 private final ResetTokenService resetTokenService;
	 private final NotificationDispatcherService dispatcher;
	 private final SupportEmailResolver supportEmailResolver;


    public void dispatchEmployeeCreatedNotification(
            Company company,
            Employee employee,
            User user,
            String tempPassword
    ) {

        String resetLink = resetTokenService.generateResetLink(user.getUserId());

        NotificationContext context = NotificationContext.builder()
                .event(NotificationEvent.EMPLOYEE_ACCOUNT_CREATED)
                .companyId(company.getId())

                // Recipient
               // .userId(user.getUserId())
                .userEmailAddress(user.getEmail())

                // Business placeholders only
                .variables(Map.of(
                        NotificationPlaceholder.EMPLOYEE_NAME.key(),
                        employee.getFirstName() + " " + employee.getLastName(),

                        NotificationPlaceholder.EMPLOYEE_EMAIL.key(),
                        user.getEmail(),

                        NotificationPlaceholder.RESET_PASSWORD_LINK.key(),
                        resetLink,

                        NotificationPlaceholder.LINK_EXPIRY_DURATION.key(),
                        "24 hours",
                        NotificationPlaceholder.EMPLOYER_SUPPORT_EMAIL.key(),supportEmailResolver.resolve(company.getId()),
                        NotificationPlaceholder.TEMPORARY_PASSWORD.key(),tempPassword
                ))
                .build();

        dispatcher.dispatch(NotificationEvent.EMPLOYEE_ACCOUNT_CREATED, context);
    }
    
    
    public void dispatchCandidateBgvInvitation(
            Company company,
            Candidate candidate,
            User user,
            String verificationLink,
            LocalDate expiryDate
    ) {
/*
        NotificationContext context = NotificationContext.builder()
                .event(NotificationEvent.CANDIDATE_INVITE)
                .companyId(company.getId())

                // Recipient
                .userEmailAddress(user.getEmail())

                // Template placeholders
                .variables(Map.of(
                        // Candidate
                        NotificationPlaceholder.CANDIDATE_NAME.key(),
                        candidate.getFullName(), // or from Profile

                        // Employer
                        NotificationPlaceholder.EMPLOYER_BRAND_NAME.key(),
                        company.getBrandName(),   // shown to candidate

                        NotificationPlaceholder.EMPLOYER_LEGAL_NAME.key(),
                        company.getLegalName(),   // legal disclosure

                        // Platform
                        NotificationPlaceholder.PLATFORM_BRAND_NAME.key(),
                        platformConfig.getBrandName(),

                        NotificationPlaceholder.PLATFORM_LOGO_URL.key(),
                        platformConfig.getLogoUrl(), // public or signed URL

                        // Links & Dates
                        NotificationPlaceholder.VERIFICATION_LINK.key(),
                        verificationLink,

                        NotificationPlaceholder.EXPIRY_DATE.key(),
                        expiryDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),

                        // Support
                        NotificationPlaceholder.EMPLOYER_SUPPORT_EMAIL.key(),
                        supportEmailResolver.resolve(company.getId()),

                        NotificationPlaceholder.PLATFORM_SUPPORT_EMAIL.key(),
                        platformConfig.getSupportEmail(),

                        // Misc
                        NotificationPlaceholder.CURRENT_YEAR.key(),
                        String.valueOf(Year.now().getValue())
                ))
                .build();

        dispatcher.dispatch(NotificationEvent.CANDIDATE_INVITE, context);
   */ }
    

}

