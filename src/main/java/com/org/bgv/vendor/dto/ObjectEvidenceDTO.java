package com.org.bgv.vendor.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class ObjectEvidenceDTO {

    private Long objectId;
    private String objectType;       // WORK_EXPERIENCE / DEGREE / IDENTITY
    private String displayName;

    private EvidenceGroupDTO objectEvidence;
    private List<DocumentEvidenceDTO> documents;
}
