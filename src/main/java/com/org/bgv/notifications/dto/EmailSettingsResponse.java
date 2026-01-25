package com.org.bgv.notifications.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailSettingsResponse {

    private Long id;

    private String fromName;
    private String fromEmail;
    private String replyToEmail;
    private String supportEmail;

    private String smtpProvider;
    private boolean active;
    private boolean verified;
}
