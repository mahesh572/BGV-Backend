package com.org.bgv.notifications.dto;

import com.org.bgv.notifications.PolicySource;

import lombok.Data;

@Data
public class SmsTemplateDTO {

    private Long id;
    private String templateCode;
    private Long companyId;
    private String message;
    private PolicySource source;
    private boolean active;
}

