package com.org.bgv.vendor.dto;


import com.org.bgv.enums.ComparisonStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateFieldComparisonRequest {

    @NotNull
    private Long objectId;    // this is education,work objectId

    @NotNull
    private Long comparisonId;

    // Value received from source (HR, university, database, etc.)
    private String sourceValue;

    @NotNull
    private ComparisonStatus status;

    // Optional remarks from vendor
    private String remarks;
}
