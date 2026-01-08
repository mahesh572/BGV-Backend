package com.org.bgv.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.org.bgv.common.RuleTypeResponse;
import com.org.bgv.entity.RuleTypes;

@Component
public class RuleTypeMapper {
    
    public RuleTypeResponse toResponse(RuleTypes ruleType) {
        if (ruleType == null) {
            return null;
        }
        
        return RuleTypeResponse.builder()
                .ruleTypeId(ruleType.getRuleTypeId())
                .name(ruleType.getName())
                .code(ruleType.getCode())
                .label(ruleType.getLabel())
                .build();
    }
    
    public List<RuleTypeResponse> toResponseList(List<RuleTypes> ruleTypes) {
        if (ruleTypes == null) {
            return null;
        }
        
        return ruleTypes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
