package com.org.bgv.global.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.org.bgv.candidate.entity.CandidatePackageRule;
import com.org.bgv.candidate.entity.CandidatePackageRuleDocument;
import com.org.bgv.candidate.repository.CandidatePackageRuleDocumentRepository;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.Document;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseSelection;
import com.org.bgv.enums.RuleGroup;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.DocumentRepository;
import com.org.bgv.repository.VerificationCaseSelectionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentRuleStrategy implements RuleExecutionStrategy {

    private final DocumentRepository documentRepository;
    private final VerificationCaseSelectionRepository selectionRepo;
    private final CheckCategoryRepository checkCategoryRepository;

    @Override
    public boolean supports(CheckCategory category, RuleTypes ruleType) {
        return ruleType.getRuleGroup() == RuleGroup.DOCUMENT_SELECTION;
    }

    @Override
    public void execute(VerificationCase verificationCase,
                        CandidatePackageRule rule,
                        RuleTypes ruleType) {

        // ✅ Get documentTypeId directly from RuleTypes
        Long documentTypeId = ruleType.getDocumentTypeId();

        if (documentTypeId == null) {
            throw new RuntimeException("DocumentTypeId missing for rule: " + ruleType.getCode());
        }

        // ✅ Resolve category properly
        CheckCategory category =
                checkCategoryRepository.findByCategoryId(rule.getCheckCategoryId());

        CheckCategoryEnum categoryEnum =
                CheckCategoryEnum.valueOf(category.getName().toUpperCase());

        // ✅ Create selection
        VerificationCaseSelection selection =
                createOrGetSelection(verificationCase, categoryEnum, documentTypeId);

        // ✅ Fetch uploaded documents
        List<Document> uploadedDocs =
                documentRepository.findByVerificationCase_CaseIdAndDocTypeIdDocTypeId(
                        verificationCase.getCaseId(),
                        documentTypeId
                );

        // ✅ Attach documents
        for (Document doc : uploadedDocs) {
            if (doc.getSelection() == null) {
                doc.setSelection(selection);
            }
        }

        documentRepository.saveAll(uploadedDocs); // 🔥 batch save
    }

    private VerificationCaseSelection createOrGetSelection(
            VerificationCase caseObj,
            CheckCategoryEnum type,
            Long refId) {

        return selectionRepo
                .findByVerificationCaseAndTypeAndReferenceId(caseObj, type, refId)
                .orElseGet(() ->
                        selectionRepo.save(
                                VerificationCaseSelection.builder()
                                        .verificationCase(caseObj)
                                        .type(type)
                                        .referenceId(refId)
                                        .status("PENDING")
                                        .build()
                        )
                );
    }
}