package com.org.bgv.notifications.service;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.org.bgv.auth.service.ResetTokenService;
import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.company.entity.Employee;
import com.org.bgv.company.repository.CompanyEmailSettingsRepository;
import com.org.bgv.company.repository.EmployeeRepository;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.User;
import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.dto.NotificationPlaceholder;
import com.org.bgv.notifications.placeholder.PlaceholderEngine;
import com.org.bgv.notifications.placeholder.ResolutionContext;
import com.org.bgv.repository.PlatformConfigRepository;
import com.org.bgv.repository.PlatformEmailSettingsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatcher {

    private final ResetTokenService resetTokenService;
    private final NotificationDispatcherService dispatcher;
    private final SupportEmailResolver supportEmailResolver;
    private final PlaceholderEngine placeholderEngine;

    private final CompanyEmailSettingsRepository companyEmailSettingsRepository;
    private final PlatformEmailSettingsRepository platformEmailSettingsRepository;
    private final PlatformConfigRepository platformConfigRepository;

    /* =====================================================
       EMPLOYEE ACCOUNT CREATED
       ===================================================== */
    public void dispatchEmployeeCreatedNotification(
            Company company,
            Employee employee,
            User user,
            String tempPassword
    ) {
        log.info(
                "📨 Dispatching EMPLOYEE_ACCOUNT_CREATED notification | companyId={} | userEmail={}",
                company.getId(), user.getEmail()
        );

        String resetLink = resetTokenService.generateResetLink(user.getUserId());
        log.debug("🔗 Generated reset password link for userId={}", user.getUserId());

        NotificationContext context = NotificationContext.builder()
                .event(NotificationEvent.EMPLOYEE_ACCOUNT_CREATED)
                .companyId(company.getId())
                .userEmailAddress(user.getEmail())
                .variables(Map.of(
                        NotificationPlaceholder.EMPLOYEE_NAME.key(),
                        employee.getFirstName() + " " + employee.getLastName(),

                        NotificationPlaceholder.EMPLOYEE_EMAIL.key(),
                        user.getEmail(),

                        NotificationPlaceholder.RESET_PASSWORD_LINK.key(),
                        resetLink,

                        NotificationPlaceholder.PASSWORD_LINK_EXPIRY_DURATION.key(),
                        "24 hours",

                        NotificationPlaceholder.EMPLOYER_SUPPORT_EMAIL.key(),
                        supportEmailResolver.resolve(company.getId()),

                        NotificationPlaceholder.TEMPORARY_PASSWORD.key(),
                        tempPassword
                ))
                .build();

        log.debug(
                "🧩 Employee notification placeholders resolved: {}",
                context.getVariables().keySet()
        );

        dispatcher.dispatch(NotificationEvent.EMPLOYEE_ACCOUNT_CREATED, context);

        log.info(
                "✅ EMPLOYEE_ACCOUNT_CREATED notification dispatched | companyId={} | userEmail={}",
                company.getId(), user.getEmail()
        );
    }

    /* =====================================================
       CANDIDATE BGV INVITATION
       ===================================================== */
    public void dispatchCandidateBgvInvitation(
            Company company,
            Candidate candidate,
            User user
    ) {
        log.info(
                "📨 Dispatching CANDIDATE_INVITE notification | companyId={} | candidateEmail={}",
                company.getId(), user.getEmail()
        );

        try {
            var companyEmailSettings = companyEmailSettingsRepository
                    .findByCompanyId(company.getId())
                    .orElse(null);

            log.debug(
                    "🏢 CompanyEmailSettings {} for companyId={}",
                    companyEmailSettings != null ? "found" : "not found (fallback to platform)",
                    company.getId()
            );

            var platformConfig = platformConfigRepository.findById(1L)
                    .orElseThrow(() ->
                            new IllegalStateException("PlatformConfig not initialized"));

            var platformEmailSettings = platformEmailSettingsRepository
                    .findActive()
                    .orElse(null);
            

            Set<NotificationPlaceholder> placeholders = EnumSet.of(
                    NotificationPlaceholder.CANDIDATE_NAME,
                    NotificationPlaceholder.CANDIDATE_EMAIL,

                    NotificationPlaceholder.EMPLOYER_BRAND_NAME,
                    NotificationPlaceholder.EMPLOYER_LEGAL_NAME,
                    NotificationPlaceholder.EMPLOYER_SUPPORT_EMAIL,

                    NotificationPlaceholder.PLATFORM_BRAND_NAME,
                    NotificationPlaceholder.PLATFORM_LEGAL_NAME,
                    NotificationPlaceholder.PLATFORM_SUPPORT_EMAIL,
                    NotificationPlaceholder.VERIFICATION_LINK,
                    NotificationPlaceholder.VERIFICATION_LINK_EXPIRY_DATE,
                    NotificationPlaceholder.CURRENT_YEAR
            );

            log.debug(
                    "🧩 Resolving placeholders for CANDIDATE_INVITE: {}",
                    placeholders
            );
            
            String resetLink = resetTokenService.generateResetLink(user.getUserId());
            
            Map<String, Object> runtimeplaceholders = new HashMap();
            runtimeplaceholders.put(
                NotificationPlaceholder.VERIFICATION_LINK.key(),
                resetLink
            );
            runtimeplaceholders.put(
                NotificationPlaceholder.VERIFICATION_LINK_EXPIRY_DATE.key(),
                ""
            );
            
            log.info("runtimeplaceholders::::::::::::::{}",runtimeplaceholders);

            Map<String, Object> vars = placeholderEngine.resolveAll(
                    placeholders,
                    ResolutionContext.builder()
                            .company(company)
                            .candidate(candidate)
                            .companyEmailSettings(companyEmailSettings)
                            .platformConfig(platformConfig)
                            .platformEmailSettings(platformEmailSettings)
                            .runtimeValues(runtimeplaceholders)
                            .build()
            );

            log.debug(
                    "🧩 Candidate invite placeholders resolved successfully: {}",
                    vars.keySet()
            );

            NotificationContext context = NotificationContext.builder()
                    .event(NotificationEvent.CANDIDATE_INVITE)
                    .companyId(company.getId())
                    .userEmailAddress(user.getEmail())
                    .variables(vars)
                    .build();

            dispatcher.dispatch(NotificationEvent.CANDIDATE_INVITE, context);

            log.info(
                    "✅ CANDIDATE_INVITE notification dispatched | companyId={} | candidateEmail={}",
                    company.getId(), user.getEmail()
            );

        } catch (Exception e) {
            log.error(
                    "❌ Failed to dispatch CANDIDATE_INVITE | companyId={} | candidateEmail={}",
                    company.getId(),
                    user.getEmail(),
                    e
            );
            throw e; // optional: rethrow so caller can react
        }
    }
}

