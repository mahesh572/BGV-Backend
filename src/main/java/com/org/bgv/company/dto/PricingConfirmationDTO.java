package com.org.bgv.company.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;



@Data
@Builder
public class PricingConfirmationDTO {
 private Long caseId;
 private Boolean confirmed;
 private LocalDateTime confirmedAt;
 private PricingSummaryDTO pricingSummary;
}
