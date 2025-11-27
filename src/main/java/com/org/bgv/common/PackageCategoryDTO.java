package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageCategoryDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String categoryCode;
    private String rulesData;
    private List<PackageRuleTypeDTO> ruleTypes;
    private List<PackageDocumentDTO> allowedDocuments;
}
