package com.org.bgv.vendor.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@Builder
public class CreateRejectionRequest {

    private Long caseId;
    private Long caseCheckId; // null only for CASE level

    private ReasonLevel level; 
    // CASE, SECTION, OBJECT, DOCUMENT

    private Long objectId;    // Education / Work id
    private Long documentId;  // VerificationDocument id

    private Long reasonId;

    private String remarks;
}
