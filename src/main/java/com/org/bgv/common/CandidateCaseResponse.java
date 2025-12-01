package com.org.bgv.common;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateCaseResponse {
    
    private Long caseId;
    private Long candidateId;
    private Long companyId;
    private EmployerPackageInfo employerPackage;
    private Double basePrice;
    private Double addonPrice;
    private Double totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private List<CandidateCaseDocumentResponse> documents;
}