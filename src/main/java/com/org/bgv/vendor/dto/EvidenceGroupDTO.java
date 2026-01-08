package com.org.bgv.vendor.dto;

import java.util.List;

import com.org.bgv.common.EvidenceLevel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class EvidenceGroupDTO {

    private EvidenceLevel level; // SECTION / OBJECT / DOCUMENT
    private Long objectId;       // nullable
    private Long documentTypeId; // nullable

    private List<EvidenceDTO> evidences;
}

