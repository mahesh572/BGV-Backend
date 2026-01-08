package com.org.bgv.vendor.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class VerificationEvidenceResponseDTO {

    private Long caseId;
    private String caseRef;

    private Long checkId;
    private String checkRef;
    private String checkType;
    private String checkName;

    private EvidenceContainerDTO evidence;
}

