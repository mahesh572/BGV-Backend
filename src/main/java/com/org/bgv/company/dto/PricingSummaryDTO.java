package com.org.bgv.company.dto;


import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class PricingSummaryDTO {
 private Long caseId;
 private String caseNumber;
 private BigDecimal baseTotal;
 private BigDecimal addonTotal;
 private BigDecimal taxAmount;
 private BigDecimal grandTotal;
 private String currency;
 private Map<String, CategoryPricingDTO> pricingByCategory;
}




