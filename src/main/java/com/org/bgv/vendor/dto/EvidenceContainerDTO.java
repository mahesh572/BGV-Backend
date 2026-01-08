package com.org.bgv.vendor.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class EvidenceContainerDTO {

    private SectionEvidenceDTO section;
    private List<ObjectEvidenceDTO> objects;
    
}

