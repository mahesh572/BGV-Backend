package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

@Data
public class StartVerificationMethodRequest {

	private Long objectId;
    private Long checkId;

    private Long methodId;

    private String methodCode;

    private String methodName;

    private String checkType;

    private LocalDateTime startedAt;

    private Map<String, Object> fields;
}