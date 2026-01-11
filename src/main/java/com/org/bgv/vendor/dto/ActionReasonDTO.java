package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionReasonDTO {

    private Long id;

    private String code;      // DOC_EXPIRED
    private String label;     // "Document is expired"
    private String description;

    /* ===== UI Behavior Flags ===== */
    private boolean requiresEvidence;
    private boolean requiresRemarks;
    private boolean terminal;

    /* ===== Optional UI Hints ===== */
    private Integer sortOrder;
}

