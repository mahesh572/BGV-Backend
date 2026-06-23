package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;

import com.org.bgv.common.EvidenceLevel;
import com.org.bgv.common.EvidenceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvidenceResponseDTO {
    private Long id;
    private String evidenceType; // "ACTION" or "METHOD_EXECUTION"
    private String source; // "CANDIDATE_DOCUMENT" or "VENDOR_UPLOAD"
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String uploadedBy;
    private String uploadedAt;
    private String remarks;
    private String notes;
    private String status;
    private String checkType;
    private String candidateName;
    private String employerName;
    private String actionStatus;
    private Long checkId;
    private Long executionId;
    private String objectType;
    private Long objectId;
}
