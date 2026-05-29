package com.org.bgv.global.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.org.bgv.candidate.entity.CandidatePackageRule;
import com.org.bgv.candidate.entity.EducationHistory;
import com.org.bgv.candidate.repository.EducationHistoryRepository;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseSelection;
import com.org.bgv.repository.VerificationCaseSelectionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EducationRuleStrategy implements RuleExecutionStrategy {

    private final EducationHistoryRepository educationRepo;
    private final VerificationCaseSelectionRepository selectionRepo;

    @Override
    public boolean supports(CheckCategory category, RuleTypes ruleType) {
        return "EDUCATION".equalsIgnoreCase(category.getName());
    }

    @Override
    public void execute(VerificationCase verificationCase,
                        CandidatePackageRule rule,
                        RuleTypes ruleType) {

        int count = rule.getSelectedCount() != null ? rule.getSelectedCount() : 1;

        List<EducationHistory> records;

        switch (ruleType.getCode()) {

            case "HIGHEST_EDUCATION":
                records = getLatest(verificationCase.getCaseId(), 1);
                break;

            case "ALL":
                records = educationRepo.findByVerificationCaseCaseId(verificationCase.getCaseId());
                break;

            case "LAST_N":
                records = getLatest(verificationCase.getCaseId(), count);
                break;

            default:
                return;
        }

        for (EducationHistory edu : records) {

            VerificationCaseSelection selection = selectionRepo.save(
                    VerificationCaseSelection.builder()
                            .verificationCase(verificationCase)
                            .type(CheckCategoryEnum.EDUCATION)
                            .referenceId(edu.getId())
                            .status("PENDING")
                            .build()
            );

            edu.setVerificationCase(verificationCase);
            educationRepo.save(edu);
        }
    }

    private List<EducationHistory> getLatest(Long caseId, int limit) {
        return educationRepo.findByVerificationCaseCaseId(caseId)
                .stream()
                .sorted(Comparator.comparing(EducationHistory::getToDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .toList();
    }
}
