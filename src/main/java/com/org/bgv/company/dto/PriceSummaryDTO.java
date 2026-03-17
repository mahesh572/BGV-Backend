package com.org.bgv.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceSummaryDTO {

    private Double basePrice;
    private Double addonPrice;
    private Double estimatedTotal;

    private Boolean finalPriceCalculatedAfterSubmission;
    private String note;
}
