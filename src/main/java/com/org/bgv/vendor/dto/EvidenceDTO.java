package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceDTO {
    private String id;
    private String type;
    private String source;
    private String verifiedBy;
    private LocalDateTime verifiedAt;
    private String status;
    private String notes;
    private String evidencePath; // Internal path for evidence
}
