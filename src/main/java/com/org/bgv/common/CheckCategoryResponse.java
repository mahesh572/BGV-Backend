package com.org.bgv.common;

import java.util.List;

import com.org.bgv.enums.PricingLevel;
import com.org.bgv.pricing.dto.DocumentPricingDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckCategoryResponse {
    private Long categoryId;
    private String name;
    private String description;
    private String label;
    private String code;
    private Boolean isActive;
    private Boolean hasDocuments;
    private PricingLevel pricingLevel; //  "DOCUMENT",   // RULE or DOCUMENT
    private List<RuleTypeResponse> ruleTypes;
    private List<DocumentPricingDTO> documents;
    private Double price;
}
