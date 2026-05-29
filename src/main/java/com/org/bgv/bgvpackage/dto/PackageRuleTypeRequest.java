package com.org.bgv.bgvpackage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageRuleTypeRequest {

    private Long ruleTypeId;
    private String ruleName;
    private String ruleCode;
    private Boolean enabled;
}
