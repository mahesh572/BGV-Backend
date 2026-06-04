package com.org.bgv.vendor.evidence.dto;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationExecutionEvidenceDto {

    private Long evidenceId;

    private Long executionId;

    private Long checkId;

    private String fileName;

    private String fileUrl;

    private String contentType;

    private Long fileSize;

    private String notes;

    private LocalDateTime uploadedAt;

    private String uploadedBy;
    
    private String evidenceType; // EMAIL_REPLY, CALL_RECORDING, SCREENSHOT, DOCUMENT
}