package com.org.bgv.common;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateCaseDocumentResponse {
    
    private Long caseDocumentId;
    private CategoryInfo checkCategory;
    private DocumentTypeInfo documentType;
    private Boolean isAddOn;
    private Boolean required;
    private Double documentPrice;
    private String verificationStatus;
    private String documentUrl;
    private LocalDateTime uploadedAt;
}