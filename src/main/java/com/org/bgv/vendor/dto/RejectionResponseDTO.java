package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class RejectionResponseDTO {

    private Long id;
    private RejectionLevel level;

    private Long objectId;
    private Long documentId;

    private String reasonCode;
    private String reasonLabel;

    private String remarks;

    private RejectionStatus status;

    private String rejectedBy;
    private LocalDateTime rejectedAt;

    private LocalDateTime resolvedAt;
}

