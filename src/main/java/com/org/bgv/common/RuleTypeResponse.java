package com.org.bgv.common;

import java.math.BigDecimal;

import com.org.bgv.enums.PricingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleTypeResponse {
	private Long pricingId;
    private Long ruleTypeId;
    private String name;
    private String code;
    private String label;
    private Integer minCount;
    private Integer maxCount;
    private PricingType pricingType; // FLAT / PER_RECORD
    private BigDecimal  unitPrice; 
    private Boolean active;
    private Boolean requiresCount;
}