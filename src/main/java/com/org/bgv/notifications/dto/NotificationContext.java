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
    private Long caseId;
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

}


