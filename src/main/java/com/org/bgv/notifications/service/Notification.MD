package com.org.bgv.notifications.dto;

import java.util.Map;

import com.org.bgv.notifications.NotificationEvent;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationContext {

    private Long companyId;
    private NotificationEvent event;

    // Template variables
    private Map<String, Object> variables;

    // Entity references
  
    private Long documentId;
    
    private String userEmailAddress;

    // 🔐 Resolved USER IDs (for In-App / Push)
    private Long candidateUserId;
    private Long employerAdminUserId;
    private Long recruiterUserId;
    private Long vendorUserId;

    // 📧 Resolved EMAILs (for Email channel)
    private String candidateEmail;
    private String employerAdminEmail;
    private String recruiterEmail;
    private String vendorEmail;

    // 📱 (Optional future)
    private String candidateMobile;
    private String recruiterMobile;
    private String vendorMobile;
    
    // EMPLOYER
    private String companySupportEmail;
    
    

 // ✅ ADD THESE
     private Long candidateId;
     private Long caseId;
     private Long checkId;
     
     
private NotificationType notificationType;

}





package com.org.bgv.notifications;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "notification_log")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    @Enumerated(EnumType.STRING)
    private NotificationEvent event;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    private String recipient;

    private String templateCode;

    private boolean success;

    private String failureReason;

    private Instant sentAt;
    

    // ✅ ADD THESE
        private Long candidateId;
        private Long caseId;
        private Long checkId;


@Enumerated(EnumType.STRING)
private NotificationType notificationType;

}



public enum NotificationType {

    /**
     * First time notification when check becomes ACTION_REQUIRED
     */
    FIRST_ALERT,

    /**
     * Auto reminder (scheduler)
     */
    REMINDER,

    /**
     * Vendor manually resends notification
     */
    MANUAL_RESEND,

    /**
     * Optional: notification when issue is resolved
     */
    RESOLVED,

    /**
     * Optional: internal system alerts
     */
    SYSTEM_ALERT
}



