package com.org.bgv.vendor.dto;


import java.time.LocalDateTime;

import com.org.bgv.enums.VendorNoteType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerificationExecutionNoteDto {

    private Long noteId;

    private Long caseId;

    private Long checkId;

    private Long executionId;

    private Long objectId;

    private String note;

    private VendorNoteType type;

    private Long createdBy;

    private String createdByRole;

    private LocalDateTime createdAt;
}