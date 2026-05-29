package com.org.bgv.global.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RuleExecutionStrategyFactory {

    private final List<RuleExecutionStrategy> strategies;

    public RuleExecutionStrategy getStrategy(CheckCategory category, RuleTypes ruleType) {

        return strategies.stream()
                .filter(s -> s.supports(category, ruleType))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("No strategy found for category: "
                                + category.getName() + " and rule: " + ruleType.getCode()));
    }
}