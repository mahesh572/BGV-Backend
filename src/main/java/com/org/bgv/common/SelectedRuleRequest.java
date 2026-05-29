package com.org.bgv.common;

import lombok.Data;

@Data
public class SelectedRuleRequest {

    private Long ruleTypeId;

    private Integer selectedCount;
}
