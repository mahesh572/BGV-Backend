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
   // private String smtpConfigJson;   // encrypted later
    
 // SMTP Configuration
    private String host;
    private Integer port;
    private String username;
    private String password;
    private Boolean auth;
    private Boolean startTls;
    private String protocol;

    private boolean active;
}