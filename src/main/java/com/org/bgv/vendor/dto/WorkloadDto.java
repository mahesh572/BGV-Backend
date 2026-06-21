package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadDto {

    private long totalAssigned;

    private long pending;

    private long inProgress;

    private long completed;
}