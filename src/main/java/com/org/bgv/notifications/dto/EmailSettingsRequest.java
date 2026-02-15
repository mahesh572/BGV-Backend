package com.org.bgv.notifications.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailSettingsRequest {

    private String fromName;
    private String fromEmail;
    private String replyToEmail;
    private String supportEmail;

    private String smtpProvider;     // SMTP, SES, SENDGRID
    private String smtpConfigJson;   // encrypted later

    private boolean active;
}