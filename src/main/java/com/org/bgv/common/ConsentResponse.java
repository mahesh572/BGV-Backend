package com.org.bgv.common;

import lombok.Data;
import java.time.LocalDateTime;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConsentResponse {
    private Long id;
    private Long candidateId;
    private String consentType;
    private String signatureUrl;
    private String documentUrl;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime consentedAt;
    private String message;
}