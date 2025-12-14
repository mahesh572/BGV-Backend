package com.org.bgv.common;

import java.util.List;

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
    private List<RuleTypeResponse> ruleTypes;
    private Double price;
}
