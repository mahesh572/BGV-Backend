package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationStatisticsResponse {
    private Long totalDocuments;
    private Long pendingDocuments;
    private Long uploadedDocuments;
    private Long underReviewDocuments;
    private Long verifiedDocuments;
    private Long rejectedDocuments;
    private Long completedDocuments;
    private Double completionPercentage;
    private String overallStatus;
    
    // Additional calculated fields for better insights
    private Double averageVerificationTime; // in hours
    private Long pendingUploadCount;
    private Long pendingVerificationCount;
    private Double successRate; // percentage of verified vs total processed
}