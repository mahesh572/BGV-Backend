package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateCaseStatisticsResponse {
    private Long totalCases;
    private Long assignedCases;
    private Long inProgressCases;
    private Long underReviewCases;
    private Long completedCases;
    private Long cancelledCases;
    private Double completionRate;
}