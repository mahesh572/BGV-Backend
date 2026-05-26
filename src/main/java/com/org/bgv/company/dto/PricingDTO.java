package com.org.bgv.company.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PricingDTO {

    private BigDecimal basePrice;
    private BigDecimal addonPrice;
    private BigDecimal totalPrice;
}