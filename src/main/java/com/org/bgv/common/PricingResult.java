package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PricingResult {
    private Double basePrice;
    private Double addonPrice;
    private Double totalPrice;
}