package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckSummaryDto {

    private Long checkId;

    private String checkRef;

    private String checkType;

    private String status;
    
    private String type;

    private String slaStatus;
}