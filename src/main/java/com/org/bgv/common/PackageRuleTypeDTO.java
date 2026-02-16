package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageRuleTypeDTO {
    private Long id;
    private Long ruleTypeId;
    private String ruleName;
    private String ruleCode;
    private Integer minCount;
    private Integer maxCount;
    private Boolean required;
    private Integer priorityOrder;
    private Boolean selected;
}