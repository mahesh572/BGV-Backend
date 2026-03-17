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
public class CategoryPreviewDTO {

    private Long categoryId;
    private String categoryName;
    private String ruleGroup; // DOCUMENT_SELECTION / RECORD_COUNT / ADDRESS_TYPE

    private Boolean includedInPackage;
    private Boolean mandatory;

    private SelectedRuleDTO selectedRule;

    private List<DocumentPreviewDTO> documents;          // For Identity
    private List<AllowedAddOnRuleDTO> allowedAddOnRules; // For Employment/Education/Address
}
