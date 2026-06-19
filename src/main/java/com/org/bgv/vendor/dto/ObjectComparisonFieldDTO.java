package com.org.bgv.vendor.dto;

import com.org.bgv.enums.ComparisonStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjectComparisonFieldDTO {

    private Long comparisonId;

    private String fieldName;

    private String displayName;

    private String candidateValue;

    private String sourceValue;

    private String result;

    private Boolean verified;

    private String remarks;
    
    private ComparisonStatus status;

}
