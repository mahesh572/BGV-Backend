package com.org.bgv.notifications.dto;

import java.util.Map;

import com.org.bgv.notifications.NotificationEvent;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationContext {

    private Long companyId;
    private Map<String, Object> variables;

    // entity references
    private Long caseId;
    private Long documentId;
    private Long userId;
    
    private NotificationEvent event;
    
 // Resolved contacts
    private String candidateEmail;
    private String employerAdminEmail;
    private String recruiterEmail;
    private String vendorEmail;
}

