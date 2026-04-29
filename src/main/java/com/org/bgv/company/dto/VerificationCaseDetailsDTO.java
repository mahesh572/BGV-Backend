package com.org.bgv.company.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.org.bgv.common.ActivityTimelineDTO;
import com.org.bgv.common.VPackageDTO;
import com.org.bgv.common.VerificationCheckDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCaseDetailsDTO {
	private Long caseId;
    private String caseNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    private CandidateSummary candidate;
    @JsonProperty("verificationChecks")
    private List<VerificationCheckDTO> verificationChecks;
    @JsonProperty("activityTimeline")
    private List<ActivityTimelineDTO> activityTimeline;
    
    private VPackageDTO vpackage;
    private PricingDTO pricing;
}
