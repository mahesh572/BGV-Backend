package com.org.bgv.vendor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExecutionActionDto {

    private String action;   // enum name or code

    private String label;    // UI text

    private String icon;

    private String type;     // PRIMARY, SECONDARY, DANGER
}