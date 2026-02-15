package com.org.bgv.data.seed;

import static java.util.Map.entry;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionType;
import com.org.bgv.vendor.dto.ReasonLevel;
import com.org.bgv.vendor.entity.ActionReason;
import com.org.bgv.vendor.entity.CategoryActionReason;
import com.org.bgv.vendor.repository.ActionReasonRepository;
import com.org.bgv.vendor.repository.CategoryActionReasonRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

@Component
@RequiredArgsConstructor
@Slf4j
@DependsOn("entityManagerFactory") // ensure JPA tables are ready
public class ActionReasonSeeder implements CommandLineRunner {

    private final ActionReasonRepository actionReasonRepo;
    private final CategoryActionReasonRepository categoryActionReasonRepo;
    private final CheckCategoryRepository categoryRepo;

   // @EventListener(ApplicationReadyEvent.class)
    @Override
    @Transactional
    public void run(String... args) {

        log.info("🔹 ActionReasonSeeder started");

        // ------------------------------------------------------------------
        // 1️⃣ DEFINE MASTER ACTION REASONS (IN-MEMORY)
        // ------------------------------------------------------------------
        Map<String, ActionReason> masterReasons = Map.ofEntries(

            // ---------- REJECT (terminal) ----------
            entry("FAKE_DOCUMENT",
                build("FAKE_DOCUMENT", "Fake / Forged Document",
                      ActionType.REJECT, ActionLevel.DOCUMENT, true)),

            entry("THIRD_PARTY_NEGATIVE",
                build("THIRD_PARTY_NEGATIVE", "Negative Third-Party Response",
                      ActionType.REJECT, ActionLevel.SECTION, true)),

            entry("MANUAL_REVIEW_FAILED",
                build("MANUAL_REVIEW_FAILED", "Manual Review Failed",
                      ActionType.REJECT, ActionLevel.SECTION, true)),

            // ---------- INSUFFICIENT (fixable) ----------
            entry("DOCUMENT_NOT_CLEAR",
                build("DOCUMENT_NOT_CLEAR", "Document Not Clear",
                      ActionType.INSUFFICIENT, ActionLevel.DOCUMENT, false)),

            entry("INCOMPLETE_DOCUMENT",
                build("INCOMPLETE_DOCUMENT", "Incomplete Document",
                      ActionType.INSUFFICIENT, ActionLevel.DOCUMENT, false)),

            entry("EXPIRED_DOCUMENT",
                build("EXPIRED_DOCUMENT", "Expired Document",
                      ActionType.INSUFFICIENT, ActionLevel.DOCUMENT, false)),

            entry("INFORMATION_MISMATCH",
                build("INFORMATION_MISMATCH", "Information Mismatch",
                      ActionType.INSUFFICIENT, ActionLevel.OBJECT, false)),

            // ---------- REQUEST INFO ----------
            entry("RECORD_NOT_FOUND",
                build("RECORD_NOT_FOUND", "Record Not Found",
                      ActionType.REQUEST_INFO, ActionLevel.SECTION, false)),

            entry("UNVERIFIABLE",
                build("UNVERIFIABLE", "Unable to Verify",
                      ActionType.REQUEST_INFO, ActionLevel.SECTION, false)),
            entry("DOCUMENT_CLARIFICATION_REQUIRED",
            	    build("DOCUMENT_CLARIFICATION_REQUIRED",
            	          "Document clarification required",
            	          ActionType.REQUEST_INFO,
            	          ActionLevel.DOCUMENT,
            	          false)),
           
            // ---------- CATEGORY-SPECIFIC ----------
            entry("UNIVERSITY_NOT_RECOGNIZED",
                build("UNIVERSITY_NOT_RECOGNIZED", "University Not Recognized",
                      ActionType.REJECT, ActionLevel.SECTION, true)),

            entry("DEGREE_NOT_MATCHING",
                build("DEGREE_NOT_MATCHING", "Degree Does Not Match",
                      ActionType.INSUFFICIENT, ActionLevel.OBJECT, false)),

            entry("COMPANY_NOT_FOUND",
                build("COMPANY_NOT_FOUND", "Company Not Found",
                      ActionType.REQUEST_INFO, ActionLevel.SECTION, false)),

            entry("EMPLOYMENT_MISMATCH",
                build("EMPLOYMENT_MISMATCH", "Employment Details Mismatch",
                      ActionType.INSUFFICIENT, ActionLevel.OBJECT, false)),

            entry("ADDRESS_NOT_FOUND",
                build("ADDRESS_NOT_FOUND", "Address Not Found",
                      ActionType.REQUEST_INFO, ActionLevel.SECTION, false)),

            entry("ADDRESS_MISMATCH",
                build("ADDRESS_MISMATCH", "Address Mismatch",
                      ActionType.INSUFFICIENT, ActionLevel.OBJECT, false)),

            entry("OTHER",
                build("OTHER", "Other",
                      ActionType.REQUEST_INFO, ActionLevel.CASE, false)),
        
            // ---------- VERIFY ----------
            entry("DOCUMENT_VERIFIED",
                build("DOCUMENT_VERIFIED", "Document Verified",
                      ActionType.VERIFY, ActionLevel.DOCUMENT, true)),

            entry("SECTION_VERIFIED",
                build("SECTION_VERIFIED", "Section Verified",
                      ActionType.VERIFY, ActionLevel.SECTION, true)),

            entry("CASE_VERIFIED",
                build("CASE_VERIFIED", "Case Verified",
                      ActionType.VERIFY, ActionLevel.CASE, true))
        );

        // ------------------------------------------------------------------
        // 2️⃣ SAVE MASTER ACTION REASONS (FK SAFE)
        // ------------------------------------------------------------------
        Map<String, ActionReason> persistedReasons =
            masterReasons.values().stream()
                .map(this::saveOrGet)
                .collect(Collectors.toMap(ActionReason::getCode, r -> r));

        log.info("✅ ActionReason master data ready: {}", persistedReasons.size());

        // ------------------------------------------------------------------
        // 3️⃣ CATEGORY → ACTION REASON MAPPING
        // ------------------------------------------------------------------
        map("Identity", persistedReasons,
            "FAKE_DOCUMENT",
            "DOCUMENT_NOT_CLEAR",
            "INFORMATION_MISMATCH",
            "EXPIRED_DOCUMENT",
            "RECORD_NOT_FOUND"
        );

        map("Education", persistedReasons,
            "UNIVERSITY_NOT_RECOGNIZED",
            "DEGREE_NOT_MATCHING",
            "RECORD_NOT_FOUND",
            "FAKE_DOCUMENT",
            "INCOMPLETE_DOCUMENT"
        );

        map("Work Experience", persistedReasons,
            "COMPANY_NOT_FOUND",
            "EMPLOYMENT_MISMATCH",
            "RECORD_NOT_FOUND",
            "THIRD_PARTY_NEGATIVE",
            "FAKE_DOCUMENT"
        );

        map("Address", persistedReasons,
            "ADDRESS_NOT_FOUND",
            "ADDRESS_MISMATCH",
            "DOCUMENT_NOT_CLEAR",
            "RECORD_NOT_FOUND"
        );

        log.info("🎯 ActionReasonSeeder completed successfully");
    }

    // ------------------------------------------------------------------
    // 🔧 HELPERS
    // ------------------------------------------------------------------

    private ActionReason build(
            String code,
            String label,
            ActionType actionType,
            ActionLevel level,
            boolean terminal
    ) {
        return ActionReason.builder()
            .code(code)
            .label(label)
            .description(label)
            .actionType(actionType)
            .level(level)
            .terminal(terminal)
            .requiresEvidence(actionType != ActionType.REQUEST_INFO)
            .requiresRemarks(true)
            .active(true)
            .build();
    }

    /**
     * Save or fetch existing ActionReason by code
     */
    private ActionReason saveOrGet(ActionReason reason) {
        return actionReasonRepo.findByCode(reason.getCode())
            .orElseGet(() -> actionReasonRepo.save(reason));
    }

    /**
     * Create CategoryActionReason mapping (FK safe)
     */
    private void map(
            String categoryName,
            Map<String, ActionReason> reasons,
            String... codes
    ) {
        CheckCategory category = categoryRepo.findByName(categoryName)
            .orElseThrow(() ->
                new IllegalStateException("❌ CheckCategory not found: " + categoryName)
            );

        for (String code : codes) {
            ActionReason reason = reasons.get(code);

            if (reason == null) {
                throw new IllegalStateException("❌ ActionReason not found: " + code);
            }

            
            boolean exists = categoryActionReasonRepo
                .existsByCategory_CategoryIdAndReason_ActionReasonId(
                    category.getCategoryId(),
                    reason.getActionReasonId()
                );

            if (!exists) {
                categoryActionReasonRepo.save(
                    CategoryActionReason.builder()
                        .category(category)
                        .reason(reason)
                        .requiresEvidence(reason.getActionType() != ActionType.REQUEST_INFO)
                        .requiresRemarks(true)
                        .active(true)
                        .build()
                );
            }
        }
    }
}
