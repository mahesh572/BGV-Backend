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
public class VendorDashboardResponse {

    private WorkloadDto workload;

    private List<ActiveCaseDto> activeCases;

   // private List<VerificationQueueDto> verificationQueue;

    private List<VerificationStatsDto> verificationStats;

  //  private List<RecentVerificationDto> recentVerifications;
}