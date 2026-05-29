package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.org.bgv.bgvpackage.dto.PackageRuleTypeRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageCategoryRequest {
    private Long categoryId;
    private String rulesData;
    private List<Long> ruleTypeIds;
    private List<PackageDocumentRequest> allowedDocuments;
    private List<PackageRuleTypeRequest> allowedRules;
    private List<RuleTypesDTO> ruleTypes;
}