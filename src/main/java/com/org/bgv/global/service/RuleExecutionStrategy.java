package com.org.bgv.global.service;

import com.org.bgv.candidate.entity.CandidatePackageRule;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.entity.VerificationCase;

public interface RuleExecutionStrategy {

    boolean supports(CheckCategory category, RuleTypes ruleType);

    void execute(
            VerificationCase verificationCase,
            CandidatePackageRule rule,
            RuleTypes ruleType
    );
}