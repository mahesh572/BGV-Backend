package com.org.bgv.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectedRuleDTO {

    private Long ruleTypeId;
    private String ruleCode;
    private String ruleLabel;

    private Integer minCount;
    private Integer maxCount;

    private Boolean includedInPackage;
    private Boolean addon;
}
