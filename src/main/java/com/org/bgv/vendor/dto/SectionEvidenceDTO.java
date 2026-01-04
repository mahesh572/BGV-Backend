package com.org.bgv.vendor.dto;

import java.util.List;

import com.org.bgv.common.EvidenceLevel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class SectionEvidenceDTO {

    private EvidenceLevel level; // SECTION
    private List<EvidenceDTO> evidences;
}

