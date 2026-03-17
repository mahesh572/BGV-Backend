package com.org.bgv.bgvpackage.dto;

import java.math.BigDecimal;

import com.org.bgv.enums.PricingType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformCheckPricingResponse {

    private Long id;
    private Long pricingId;

    private Long checkCategoryId;
    private String checkCategoryName;
    private String checkCategoryCode;

    private Long ruleTypeId;
    private String ruleTypeName;
    private String ruleTypeCode;
    private String documentLabel;

    private PricingType pricingType;   // FLAT / PER_RECORD

    private BigDecimal  unitPrice;

    private Boolean active;
    
    private String level; // its only for Identity section
}
