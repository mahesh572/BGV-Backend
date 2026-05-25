package com.org.bgv.company.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllowedAddOnRuleDTO {

    private Long ruleTypeId;
    private String ruleCode;
    private String ruleLabel;
    private String ruleGroup;

    private String pricingType;   // FLAT / PER_RECORD
    private BigDecimal  unitPrice;

    private Boolean dynamicPricing;
    private Boolean selected;
    private Boolean requiresCount;
    private boolean disabled;
}