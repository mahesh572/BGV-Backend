package com.org.bgv.vendor.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveCaseDto {

    private Long caseId;

    private String caseRef;

    private String candidateId;

    private String candidateName;

    private String employerName;

    private String priority;

    private String slaStatus;

    private long daysRemaining;

    private List<CheckSummaryDto> checks;
}
