package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriminalContextDTO {
    private String verificationStage; // police_verification, court_check
    private String policeStationDetails;
    private String courtDetails;
    private Boolean requiresFingerprintCheck;
    private String verificationMethod; // online, offline
    private Integer expectedTimelineDays;
    private Boolean requiresPhysicalVisit;
}