package com.org.bgv.data.seed;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.RecipientType;
import com.org.bgv.notifications.entity.NotificationEventMaster;
import com.org.bgv.notifications.repository.NotificationEventMasterRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEventSeeder implements ApplicationRunner {

    private final NotificationEventMasterRepository repository;

    @Override
    public void run(ApplicationArguments args) {
        seed();
    }

    private void seed() {

        // ================= ACCOUNT =================
        create(NotificationEvent.USER_REGISTERED,
                "User Registered", "Account",
                "Triggered when a user registers",
                "INFO",
                List.of("EMAIL", "IN_APP"),
                List.of(RecipientType.USER));

        create(NotificationEvent.USER_ACTIVATED,
                "User Activated", "Account",
                "Triggered when a user account is activated",
                "INFO",
                List.of("EMAIL", "IN_APP"),
                List.of(RecipientType.USER));

        create(NotificationEvent.USER_DEACTIVATED,
                "User Deactivated", "Account",
                "Triggered when a user is deactivated",
                "WARNING",
                List.of("EMAIL"),
                List.of(RecipientType.USER));

        create(NotificationEvent.PASSWORD_RESET_REQUESTED,
                "Password Reset Requested", "Account",
                "Triggered when password reset is requested",
                "INFO",
                List.of("EMAIL"),
                List.of(RecipientType.USER));

        create(NotificationEvent.PASSWORD_CHANGED,
                "Password Changed", "Account",
                "Triggered when password is changed",
                "INFO",
                List.of("EMAIL"),
                List.of(RecipientType.USER));

        create(NotificationEvent.LOGIN_FAILED,
                "Login Failed", "Account",
                "Triggered on failed login attempts",
                "WARNING",
                List.of("IN_APP"),
                List.of(RecipientType.USER));

        create(NotificationEvent.ACCOUNT_LOCKED,
                "Account Locked", "Account",
                "Triggered when account is locked",
                "CRITICAL",
                List.of("EMAIL", "IN_APP"),
                List.of(RecipientType.USER, RecipientType.ADMIN));

        // ================= CANDIDATE =================
        create(NotificationEvent.CANDIDATE_INVITE,
                "Candidate Invitation", "Candidate",
                "Sent when candidate is invited",
                "INFO",
                List.of("EMAIL", "SMS"),
                List.of(RecipientType.CANDIDATE));

        create(NotificationEvent.CANDIDATE_REGISTERED,
                "Candidate Registered", "Candidate",
                "Candidate completed registration",
                "INFO",
                List.of("EMAIL", "IN_APP"),
                List.of(RecipientType.EMPLOYER));

        create(NotificationEvent.CANDIDATE_PROFILE_COMPLETED,
                "Candidate Profile Completed", "Candidate",
                "Candidate completed profile",
                "INFO",
                List.of("EMAIL"),
                List.of(RecipientType.EMPLOYER));

        create(NotificationEvent.CANDIDATE_REMINDER,
                "Candidate Reminder", "Candidate",
                "Reminder sent to candidate",
                "WARNING",
                List.of("EMAIL", "SMS"),
                List.of(RecipientType.CANDIDATE));

        // ================= DOCUMENT =================
        create(NotificationEvent.DOCUMENT_UPLOADED,
                "Document Uploaded", "Document",
                "Document uploaded by candidate",
                "INFO",
                List.of("EMAIL", "IN_APP"),
                List.of(RecipientType.EMPLOYER));

        create(NotificationEvent.DOCUMENT_APPROVED,
                "Document Approved", "Document",
                "Document approved",
                "INFO",
                List.of("EMAIL", "IN_APP"),
                List.of(RecipientType.CANDIDATE));

        create(NotificationEvent.DOCUMENT_REJECTED,
                "Document Rejected", "Document",
                "Document rejected",
                "WARNING",
                List.of("EMAIL", "SMS", "IN_APP"),
                List.of(RecipientType.CANDIDATE, RecipientType.EMPLOYER));

        create(NotificationEvent.DOCUMENT_REUPLOAD_REQUESTED,
                "Re-upload Requested", "Document",
                "Re-upload requested",
                "WARNING",
                List.of("EMAIL", "SMS"),
                List.of(RecipientType.CANDIDATE));

        // ================= VERIFICATION =================
        create(NotificationEvent.VERIFICATION_STARTED,
                "Verification Started", "Verification",
                "Verification started",
                "INFO",
                List.of("EMAIL", "IN_APP"),
                List.of(RecipientType.EMPLOYER));

        create(NotificationEvent.VERIFICATION_IN_PROGRESS,
                "Verification In Progress", "Verification",
                "Verification in progress",
                "INFO",
                List.of("IN_APP"),
                List.of(RecipientType.EMPLOYER));

        create(NotificationEvent.VERIFICATION_FAILED,
                "Verification Failed", "Verification",
                "Verification failed",
                "CRITICAL",
                List.of("EMAIL", "IN_APP"),
                List.of(RecipientType.EMPLOYER));

        create(NotificationEvent.VERIFICATION_COMPLETED,
                "Verification Completed", "Verification",
                "Verification completed",
                "INFO",
                List.of("EMAIL", "IN_APP"),
                List.of(RecipientType.EMPLOYER, RecipientType.CANDIDATE));

        // ================= CASE =================
        create(NotificationEvent.CASE_CREATED,
                "Case Created", "Case",
                "Case created",
                "INFO",
                List.of("EMAIL", "IN_APP"),
                List.of(RecipientType.EMPLOYER));

        create(NotificationEvent.CASE_ASSIGNED,
                "Case Assigned", "Case",
                "Case assigned to vendor",
                "INFO",
                List.of("EMAIL"),
                List.of(RecipientType.VENDOR));

        create(NotificationEvent.CASE_ON_HOLD,
                "Case On Hold", "Case",
                "Case put on hold",
                "WARNING",
                List.of("EMAIL"),
                List.of(RecipientType.EMPLOYER));

        create(NotificationEvent.CASE_COMPLETED,
                "Case Completed", "Case",
                "Case completed",
                "INFO",
                List.of("EMAIL", "IN_APP"),
                List.of(RecipientType.EMPLOYER, RecipientType.CANDIDATE));

        create(NotificationEvent.CASE_CANCELLED,
                "Case Cancelled", "Case",
                "Case cancelled",
                "WARNING",
                List.of("EMAIL"),
                List.of(RecipientType.EMPLOYER));

        // ================= SLA =================
        create(NotificationEvent.SLA_WARNING,
                "SLA Warning", "SLA",
                "SLA approaching breach",
                "WARNING",
                List.of("EMAIL", "IN_APP"),
                List.of(RecipientType.EMPLOYER));

        create(NotificationEvent.SLA_BREACHED,
                "SLA Breached", "SLA",
                "SLA breached",
                "CRITICAL",
                List.of("EMAIL", "IN_APP"),
                List.of(RecipientType.EMPLOYER, RecipientType.ADMIN));

        // ================= REPORT =================
        create(NotificationEvent.REPORT_GENERATED,
                "Report Generated", "Report",
                "Report generated",
                "INFO",
                List.of("EMAIL"),
                List.of(RecipientType.EMPLOYER));

        create(NotificationEvent.REPORT_SHARED,
                "Report Shared", "Report",
                "Report shared",
                "INFO",
                List.of("EMAIL"),
                List.of(RecipientType.EMPLOYER));
    }

    private void create(
            NotificationEvent event,
            String displayName,
            String category,
            String description,
            String severity,
            List<String> channels,
            List<RecipientType> recipients
    ) {
        if (repository.existsByEventCode(event)) {
            return;
        }
        

        NotificationEventMaster e = new NotificationEventMaster();
        e.setEventCode(event);
        e.setDisplayName(displayName);
        e.setCategory(category);
        e.setDescription(description);
        e.setSeverity(severity);
        e.setSupportedChannels(channels);
        e.setSupportedRecipients(recipients);
        e.setIsActive(true);

        repository.save(e);
    }
}

