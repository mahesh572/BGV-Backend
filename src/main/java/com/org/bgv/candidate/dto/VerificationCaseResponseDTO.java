package com.org.bgv.candidate.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCaseResponseDTO {
 private List<VerificationCaseDTO> cases;
 private CaseStatisticsDTO statistics;
 private Integer totalCount;
 private Integer page;
 private Integer pageSize;
 private Integer totalPages;
}