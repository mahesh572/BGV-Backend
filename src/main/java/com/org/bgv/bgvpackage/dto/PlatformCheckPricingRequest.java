package com.org.bgv.bgvpackage.dto;

import com.org.bgv.enums.PricingType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PlatformCheckPricingRequest {

    @NotNull(message = "Check Category ID is required")
    private Long checkCategoryId;

    @NotNull(message = "Rule Type ID is required")
    private Long ruleTypeId;

    @NotNull(message = "Pricing type is required")
    private PricingType pricingType;   // FLAT / PER_RECORD

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be greater than 0")
    private Double unitPrice;
}
