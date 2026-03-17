package com.org.bgv.company.dto;

import java.math.BigDecimal;

import com.org.bgv.enums.PricingType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PricingInfo {

    private PricingType pricingType;   // FLAT / PER_RECORD
    private BigDecimal  unitPrice;

    // Only used for employer pricing (optional)
    private Double minCharge;
    private Double maxCharge;
}