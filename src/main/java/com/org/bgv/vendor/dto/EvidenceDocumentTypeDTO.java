package com.org.bgv.vendor.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class EvidenceDocumentTypeDTO {

    private Long id;
    private String code;
    private String name;
}

