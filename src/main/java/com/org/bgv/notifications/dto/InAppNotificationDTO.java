package com.org.bgv.notifications.dto;

import java.time.Instant;

import com.org.bgv.notifications.NotificationPriority;

import lombok.Data;

@Data
public class InAppNotificationDTO {

    private Long id;
    private String title;
    private String message;
    private String deepLink;
    private NotificationPriority priority;
    private boolean read;
    private Instant createdAt;
}

