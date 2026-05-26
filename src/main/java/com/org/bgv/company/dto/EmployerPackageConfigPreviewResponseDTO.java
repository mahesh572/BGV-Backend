package com.org.bgv.company.dto;
import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerPackageConfigPreviewResponseDTO {

	private Long candidateId;
    private String candidateName;
    private Long companyId;
    private Long employerPackageId;
    private String packageName;

    private BigDecimal basePrice;
    private BigDecimal addonPrice;
    private BigDecimal estimatedTotalPrice;
    private Boolean finalPriceDynamic;

    private List<CategoryPreviewDTO> categories;

    private PriceSummaryDTO priceSummary;
	
	
}
