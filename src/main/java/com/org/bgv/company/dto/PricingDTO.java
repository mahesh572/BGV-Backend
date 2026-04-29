package com.org.bgv.company.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PricingDTO {

    private Double basePrice;
    private Double addonPrice;
    private Double totalPrice;
}