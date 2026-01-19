package com.org.bgv.notifications.dto;

import com.org.bgv.notifications.PolicySource;

import lombok.Data;

@Data
public class InAppTemplateDTO {

    private Long id;
    private String templateCode;
    private Long companyId;
    private String title;
    private String message;
    private String deepLink;
    private PolicySource source;
    private boolean active;
}
