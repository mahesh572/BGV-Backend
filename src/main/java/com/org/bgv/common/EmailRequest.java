package com.org.bgv.common;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailRequest {
    private String to;
    private String from;
    private String subject;
    private String body;
    private String templateType;
    private Map<String, Object> placeholders;
    private Boolean isHtml;
}
