package com.org.bgv.global.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.org.bgv.candidate.entity.CandidatePackageRule;
import com.org.bgv.candidate.entity.WorkExperience;
import com.org.bgv.candidate.repository.WorkExperienceRepository;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseSelection;
import com.org.bgv.repository.VerificationCaseSelectionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorkRuleStrategy implements RuleExecutionStrategy {

    private final WorkExperienceRepository workRepo;
    private final VerificationCaseSelectionRepository selectionRepo;

    @Override
    public boolean supports(CheckCategory category, RuleTypes ruleType) {
        return "WORK".equalsIgnoreCase(category.getName());
    }

    @Override
    public void execute(VerificationCase verificationCase,
                        CandidatePackageRule rule,
                        RuleTypes ruleType) {

        int count = rule.getSelectedCount() != null ? rule.getSelectedCount() : 2;

        List<WorkExperience> records;

        if ("ALL".equalsIgnoreCase(ruleType.getCode())) {
            records = workRepo.findByVerificationCaseCaseId(verificationCase.getCaseId());
        } else {
            records = getLatest(verificationCase.getCaseId(), count);
        }

        for (WorkExperience work : records) {

            VerificationCaseSelection selection = selectionRepo.save(
                    VerificationCaseSelection.builder()
                            .verificationCase(verificationCase)
                            .type(CheckCategoryEnum.WORK)
                            .referenceId(work.getExperienceId())
                            .status("PENDING")
                            .build()
            );

            work.setVerificationCase(verificationCase);
            workRepo.save(work);
        }
    }

    private List<WorkExperience> getLatest(Long caseId, int limit) {
        return workRepo.findByVerificationCaseCaseId(caseId)
                .stream()
                .sorted(Comparator.comparing(WorkExperience::getEnd_date,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .toList();
    }
}