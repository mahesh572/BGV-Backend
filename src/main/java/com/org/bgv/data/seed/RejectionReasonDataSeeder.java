package com.org.bgv.data.seed;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;


import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.vendor.dto.RejectionLevel;
import com.org.bgv.vendor.entity.RejectionReason;
import com.org.bgv.vendor.entity.RejectionReasonRepository;
import com.org.bgv.entity.CheckCategory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RejectionReasonDataSeeder implements CommandLineRunner {

    private final RejectionReasonRepository repository;
    private final CheckCategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) {

        if (repository.count() > 0) return;

        CheckCategory identity = categoryRepository.findByName("Identity").orElseThrow();
        CheckCategory education = categoryRepository.findByName("Education").orElseThrow();
        CheckCategory work = categoryRepository.findByName("Work Experience").orElseThrow();

        repository.saveAll(List.of(

            // =====================
            // COMMON – SECTION LEVEL
            // =====================
            reason("SEC_INSUFFICIENT_DATA",
                    "Insufficient data provided",
                    "Required details are missing",
                    RejectionLevel.SECTION,
                    null,
                    1),

            reason("SEC_CANDIDATE_NOT_RESPONDING",
                    "Candidate not responding",
                    "Candidate failed to respond within SLA",
                    RejectionLevel.SECTION,
                    null,
                    2),

            // =====================
            // IDENTITY – DOCUMENT
            // =====================
            reason("DOC_BLURRY",
                    "Blurry / unreadable document",
                    "Uploaded document is unclear",
                    RejectionLevel.DOCUMENT,
                    identity,
                    10),

            reason("DOC_MISMATCH",
                    "Details mismatch",
                    "Document details do not match candidate data",
                    RejectionLevel.DOCUMENT,
                    identity,
                    11),

            reason("DOC_EXPIRED",
                    "Document expired",
                    "Document validity has expired",
                    RejectionLevel.DOCUMENT,
                    identity,
                    12),

            // =====================
            // EDUCATION – OBJECT
            // =====================
            reason("OBJ_INSTITUTE_NOT_RECOGNIZED",
                    "Institute not recognized",
                    "Institute not listed or invalid",
                    RejectionLevel.OBJECT,
                    education,
                    20),

            reason("OBJ_UNIVERSITY_NO_RESPONSE",
                    "University not responding",
                    "No confirmation from university",
                    RejectionLevel.OBJECT,
                    education,
                    21),

            // =====================
            // EDUCATION – DOCUMENT
            // =====================
            reason("DOC_MARKSHEET_MISSING",
                    "Marksheet missing",
                    "Required marksheet not uploaded",
                    RejectionLevel.DOCUMENT,
                    education,
                    22),

            // =====================
            // WORK EXPERIENCE – OBJECT
            // =====================
            reason("OBJ_EMPLOYER_NOT_REACHABLE",
                    "Employer not reachable",
                    "Unable to contact employer",
                    RejectionLevel.OBJECT,
                    work,
                    30),

            reason("OBJ_EMPLOYMENT_MISMATCH",
                    "Employment details mismatch",
                    "Employment duration or role mismatch",
                    RejectionLevel.OBJECT,
                    work,
                    31),

            // =====================
            // WORK EXPERIENCE – DOCUMENT
            // =====================
            reason("DOC_OFFER_LETTER_INVALID",
                    "Invalid offer letter",
                    "Offer letter not acceptable",
                    RejectionLevel.DOCUMENT,
                    work,
                    32)
        ));
    }

    private RejectionReason reason(
            String code,
            String label,
            String description,
            RejectionLevel level,
            CheckCategory category,
            int order
    ) {
        return RejectionReason.builder()
                .code(code)
                .label(label)
                .description(description)
                .level(level)
                .category(category)
                .sortOrder(order)
                .active(true)
                .build();
    }
}