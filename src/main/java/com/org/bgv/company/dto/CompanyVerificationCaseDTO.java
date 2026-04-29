package com.org.bgv.company.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyVerificationCaseDTO {
    private Long caseId;
    private String caseNumber;
    private Long candidateId;
    private Long companyId;
    private String status;
    private Double totalPrice;
    private LocalDateTime createdAt;
    private String candidateName;
}