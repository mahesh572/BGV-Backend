package com.org.bgv.common;

import java.math.BigDecimal;
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
public class VerificationCaseResponse {
    
    private Long caseId;
    private Long candidateId;
    private Long companyId;
    private EmployerPackageInfo employerPackage;
    private BigDecimal basePrice;
    private BigDecimal addonPrice;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private List<VerificationCaseDocumentResponse> documents;
}