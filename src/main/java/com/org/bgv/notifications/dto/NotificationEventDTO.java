package com.org.bgv.notifications.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

import com.org.bgv.notifications.NotificationEvent;

@Data
@Builder
public class NotificationEventDTO {

    private Long id;
    private NotificationEvent eventCode;
    private String displayName;
    private String category;
    private String description;
    private String severity; // INFO / WARNING / CRITICAL
    private List<String> supportedChannels; // EMAIL, SMS, IN_APP
}
