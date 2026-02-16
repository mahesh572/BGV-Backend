package com.org.bgv.bgvpackage.dto;

import com.org.bgv.enums.PricingType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformCheckPricingResponse {

    private Long id;

    private Long checkCategoryId;
    private String checkCategoryName;
    private String checkCategoryCode;

    private Long ruleTypeId;
    private String ruleTypeName;
    private String ruleTypeCode;

    private PricingType pricingType;   // FLAT / PER_RECORD

    private Double unitPrice;

    private Boolean active;
}
