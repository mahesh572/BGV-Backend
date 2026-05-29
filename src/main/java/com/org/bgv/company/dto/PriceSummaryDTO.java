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
public class PriceSummaryDTO {

    private BigDecimal basePrice;
    private BigDecimal addonPrice;
    private BigDecimal estimatedTotal;

    private Boolean finalPriceCalculatedAfterSubmission;
    private String note;
}
