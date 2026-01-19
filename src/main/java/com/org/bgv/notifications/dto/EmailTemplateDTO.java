package com.org.bgv.notifications.dto;

import com.org.bgv.notifications.PolicySource;

import lombok.Data;

@Data
public class EmailTemplateDTO {

    private Long id;
    private String templateCode;
    private Long companyId;
    private String subject;
    private String bodyHtml;
    private PolicySource source;
    private boolean active;
}
