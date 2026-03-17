package com.org.bgv.company.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignCasePreviewResponseDTO {

    private Long candidateId;
    private String candidateName;
    private Long companyId;
    private Long employerPackageId;
    private String packageName;

    private Double basePrice;
    private Double addonPrice;
    private Double estimatedTotalPrice;
    private Boolean finalPriceDynamic;

    private List<CategoryPreviewDTO> categories;

    private PriceSummaryDTO priceSummary;
}
