package com.org.bgv.vendor.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class DocumentEvidenceDTO {

    private EvidenceDocumentTypeDTO documentType;
    private EvidenceGroupDTO documentEvidence;
}

