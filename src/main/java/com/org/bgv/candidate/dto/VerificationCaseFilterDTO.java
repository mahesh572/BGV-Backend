package com.org.bgv.candidate.dto;

import java.time.LocalDateTime;

import com.org.bgv.constants.CaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCaseFilterDTO {
    private Long candidateId;
    private String searchTerm;
    private CaseStatus status;
    private String companyName;
    private String verificationType;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Boolean includeCompleted;
    private Boolean includeInProgress;
    private Boolean includePending;
    
    // Pagination
    private Integer page;
    private Integer pageSize;
    private String sortBy;
    private String sortDirection;
}