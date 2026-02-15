package com.org.bgv.notifications.dto;

import com.org.bgv.notifications.NotificationChannel;
import com.org.bgv.notifications.NotificationPriority;

import lombok.Data;

@Data
public class NotificationChannelPolicyDTO {

    private NotificationChannel channel;
    private boolean enabled;

    private String templateCode;      // template selected in UI
    private NotificationPriority priority;
}

