package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;

import com.org.bgv.common.EvidenceLevel;
import com.org.bgv.constants.VerificationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.org.bgv.constants.VerificationStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceDTO {

    /* ===== Identity ===== */
    private Long evidenceId;

    /* ===== Classification ===== */
    private Long categoryId;              // Identity / Education / Work
    private Long docTypeId;               // Optional (AADHAR, DEGREE)
    private Long objectId;                // caseCheckId OR domain entity id
    private EvidenceLevel level;          // SECTION | DOC_TYPE

    /* ===== Evidence Type ===== */
    private Long evidenceTypeId;           // MANUAL_VERIFICATION, DISCREPANCY
    private String evidenceTypeCode;
    private String evidenceTypeLabel;

    /* ===== File Info ===== */
    private String fileName;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private String evidencePath;           // S3 / internal path

    /* ===== Verification ===== */
    private VerificationStatus status;     // PENDING, VERIFIED, REJECTED
    private String verifiedBy;
    private LocalDateTime verifiedAt;
    private String notes;

    /* ===== Audit ===== */
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}
