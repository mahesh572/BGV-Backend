package com.org.bgv.notifications.dto;

import com.org.bgv.notifications.NotificationChannel;
import com.org.bgv.notifications.NotificationPriority;
import com.org.bgv.notifications.RecipientType;

import lombok.Data;

@Data
public class NotificationPolicyChannelDTO {

    private Long id;
    private NotificationChannel channel;
    private boolean enabled;
    private RecipientType recipient;
    private String templateCode;
    private NotificationPriority priority;
}

