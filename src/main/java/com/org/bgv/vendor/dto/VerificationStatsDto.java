package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationStatsDto {

    private String checkType;

    private long assigned;

    private long pending;

    private long inProgress;

    private long completed;
}
