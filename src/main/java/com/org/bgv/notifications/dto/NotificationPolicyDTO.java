package com.org.bgv.notifications.dto;

import java.util.List;

import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.PolicySource;

import lombok.Data;

@Data
public class NotificationPolicyDTO {

    private Long id;
    private NotificationEvent event;
    private Long companyId; // null = platform default
    private boolean active;
    private PolicySource source; // PLATFORM_DEFAULT / EMPLOYER_OVERRIDE
    
    private List<NotificationRecipientPolicyDTO> recipients;

   // private List<NotificationPolicyChannelDTO> channels;
}
