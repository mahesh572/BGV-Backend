package com.org.bgv.candidate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseStatisticsDTO {
 private Long totalCases;
 private Long completedCases;
 private Long inProgressCases;
 private Long pendingCases;
 private Long rejectedCases;
}
