package com.org.bgv.data.seed;

import com.org.bgv.common.CheckCategoryRequest;
import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.repository.*;
import com.org.bgv.service.CheckCategoryService;
import com.org.bgv.vendor.entity.CategoryEvidenceType;
import com.org.bgv.vendor.entity.EvidenceType;
import com.org.bgv.vendor.repository.CategoryEvidenceTypeRepository;
import com.org.bgv.vendor.repository.EvidenceTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;

@Configuration
@RequiredArgsConstructor
@Slf4j
@DependsOn("entityManagerFactory") // ensure JPA tables are ready
public class EvidenceMetadataSeeder implements CommandLineRunner {

    private final EvidenceTypeRepository evidenceTypeRepository;
    private final CategoryEvidenceTypeRepository categoryEvidenceTypeRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final CheckCategoryService checkCategoryService;

    @Override
   // @EventListener(ApplicationReadyEvent.class)
    public void run(String... args) {

        log.info("🌱 Seeding Evidence Metadata...");
        
        
     // 1️⃣ Define categories
        List<CheckCategoryRequest> defaultCategories = List.of(
                CheckCategoryRequest.builder()
                        .name("Identity")
                        .label("Identity")
                        .description("Identity")
                        .code("IDENTITY")
                        .hasDocuments(true)
                        .isActive(true)
                        .price(0.0)
                        .build(),
                CheckCategoryRequest.builder()
                        .name("Education")
                        .label("Education")
                        .description("Education")
                        .code("EDUCATION")
                        .hasDocuments(true)
                        .isActive(true)
                        .price(0.0)
                        .build(),
                CheckCategoryRequest.builder()
                        .name("Work Experience")
                        .label("Professional/Work Experience")
                        .description("Professional/Work Experience")
                        .code("WORK")
                        .hasDocuments(true)
                        .isActive(true)
                        .price(0.0)
                        .build(),
                CheckCategoryRequest.builder()
                        .name("Other")
                        .label("Other")
                        .description("Other")
                        .code("OTHER")
                        .hasDocuments(false)
                        .isActive(true)
                        .price(0.0)
                        .build(),
                CheckCategoryRequest.builder()
                        .name("Address")
                        .label("Address")
                        .description("Address")
                        .code("ADDRESS")
                        .hasDocuments(true)
                        .isActive(true)
                        .price(0.0)
                        .build(),
                CheckCategoryRequest.builder()
                        .name("Court")
                        .label("Court")
                        .description("Court")
                        .code("COURT")
                        .hasDocuments(false)
                        .isActive(true)
                        .price(0.0)
                        .build()
        );

        // 2️⃣ Create categories if they do not exist
        for (CheckCategoryRequest req : defaultCategories) {
            if (!checkCategoryRepository.existsByCode(req.getCode())) {
                try {
                    CheckCategoryResponse resp = checkCategoryService.createCheckCategory(req);
                    log.info("Created category: {}", resp.getCode());
                } catch (Exception e) {
                    log.warn("Skipping category {}: {}", req.getCode(), e.getMessage());
                }
            } else {
                log.info("Category {} already exists, skipping", req.getCode());
            }
        }

        log.info("✅ Check categories seeding completed");

        // 1️⃣ Create Evidence Types
        EvidenceType manualVerification = createEvidenceType(
                "MANUAL_VERIFICATION",
                "Manual Verification",
                "Used when manual verification is required",
                true,
                true
        );

        EvidenceType discrepancyProof = createEvidenceType(
                "DISCREPANCY_PROOF",
                "Discrepancy Proof",
                "Supporting proof for detected discrepancies",
                true,
                true
        );

        EvidenceType clarificationDoc = createEvidenceType(
                "CLARIFICATION_DOC",
                "Clarification Document",
                "Uploaded to clarify verifier queries",
                true,
                false
        );

        // 2️⃣ Map Evidence Types to Categories
        mapToCategory("Identity", manualVerification, false, 3);
        mapToCategory("Identity", clarificationDoc, false, 2);

        mapToCategory("Education", discrepancyProof, true, 5);
        mapToCategory("Education", clarificationDoc, false, 3);

        mapToCategory("Work Experience", manualVerification, true, 5);
        mapToCategory("Work Experience", discrepancyProof, false, 3);

        log.info("✅ Evidence Metadata Seeding Completed");
    }

   // @EventListener(ApplicationReadyEvent.class)
    private EvidenceType createEvidenceType(
            String code,
            String label,
            String description,
            boolean requiresFile,
            boolean requiresRemarks
    ) {
        return evidenceTypeRepository.findByCode(code)
                .orElseGet(() -> {
                    EvidenceType type = EvidenceType.builder()
                            .code(code)
                            .label(label)
                            .description(description)
                            .requiresFile(requiresFile)
                            .requiresRemarks(requiresRemarks)
                            .active(true)
                            .build();

                    evidenceTypeRepository.save(type);
                    log.info("Inserted EvidenceType: {}", code);
                    return type;
                });
    }

    private void mapToCategory(
            String categoryName,
            EvidenceType evidenceType,
            boolean mandatory,
            Integer maxFiles
    ) {
        CheckCategory category = checkCategoryRepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalStateException(
                        "Category not found: " + categoryName));

        if (!categoryEvidenceTypeRepository
                .existsByCategoryAndEvidenceType(category, evidenceType)) {

            CategoryEvidenceType mapping = CategoryEvidenceType.builder()
                    .category(category)
                    .evidenceType(evidenceType)
                    .mandatory(mandatory)
                    .maxFiles(maxFiles)
                    .active(true)
                    .build();

            categoryEvidenceTypeRepository.save(mapping);
            log.info("Mapped EvidenceType [{}] to Category [{}]",
                    evidenceType.getCode(), categoryName);
        }
    }
}
