package com.org.bgv.data.seed;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.RuleGroup;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.RuleTypesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RuleTypeSeeder implements CommandLineRunner {

    private final RuleTypesRepository ruleRepo;
    private final CheckCategoryRepository categoryRepo;

    @Override
    public void run(String... args) {

        CheckCategory education = categoryRepo.findByName(CheckCategoryEnum.EDUCATION.getName()).orElseThrow();
        CheckCategory work = categoryRepo.findByName(CheckCategoryEnum.WORK_EXPERIENCE.getName()).orElseThrow();
        CheckCategory identity = categoryRepo.findByName(CheckCategoryEnum.IDENTITY.getName()).orElseThrow();
        CheckCategory address = categoryRepo.findByName(CheckCategoryEnum.ADDRESS.getName()).orElseThrow();

        // =========================
        // EDUCATION
        // =========================
        saveOrUpdate(RuleTypes.builder()
                .name("Highest Education")
                .code("HIGHEST_EDUCATION")
                .label("Verify highest qualification")
                .category(education)
                .ruleGroup(RuleGroup.EDUCATION_RECORD_SELECTION)
                .requiresCount(false)
                .minCount(1)
                .maxCount(1)
                .build());

        saveOrUpdate(RuleTypes.builder()
                .name("All Education")
                .code("ALL")
                .label("Verify all qualifications")
                .category(education)
                .ruleGroup(RuleGroup.ALL)
                .requiresCount(false)
                .build());

        saveOrUpdate(RuleTypes.builder()
                .name("Last N Education")
                .code("LAST_N")
                .label("Verify last N qualifications")
                .category(education)
                .ruleGroup(RuleGroup.EDUCATION_RECORD_SELECTION)
                .requiresCount(true)
                .build());

        // =========================
        // WORK
        // =========================
        saveOrUpdate(RuleTypes.builder()
                .name("Last N Companies")
                .code("LAST_N")
                .label("Verify last N companies")
                .category(work)
                .ruleGroup(RuleGroup.RECORD_COUNT)
                .requiresCount(true)
                .build());

        saveOrUpdate(RuleTypes.builder()
                .name("All Companies")
                .code("ALL")
                .label("Verify all employment")
                .category(work)
                .ruleGroup(RuleGroup.ALL)
                .requiresCount(false)
                .build());

        // =========================
        // IDENTITY
        // =========================
        saveOrUpdate(rule(identity, "AADHAR", "Aadhar Card"));
        saveOrUpdate(rule(identity, "PAN", "PAN Card"));
        saveOrUpdate(rule(identity, "PASSPORT", "Passport"));
        saveOrUpdate(rule(identity, "VOTER", "Voter ID"));

        saveOrUpdate(RuleTypes.builder()
                .name("Any One")
                .code("ANY_1")
                .label("Select Any One Document")
                .category(identity)
                .ruleGroup(RuleGroup.RULE)
                .requiresCount(false)
                .minCount(1)
                .maxCount(1)
                .build());

        saveOrUpdate(RuleTypes.builder()
                .name("All")
                .code("ALL")
                .label("Select All Documents")
                .category(identity)
                .ruleGroup(RuleGroup.DOCUMENT_SELECTION)
                .requiresCount(false)
                .minCount(1)
                .maxCount(5)
                .build());

        // =========================
        // ADDRESS
        // =========================
        saveOrUpdate(RuleTypes.builder()
                .name("Last N Addresses")
                .code("LAST_N")
                .label("Verify last N addresses")
                .category(address)
                .ruleGroup(RuleGroup.ADDRESS_RECORD_COUNT)
                .requiresCount(true)
                .build());

        saveOrUpdate(RuleTypes.builder()
                .name("Address Duration")
                .code("ADDRESS_DURATION")
                .label("Verify address for X years")
                .category(address)
                .ruleGroup(RuleGroup.ADDRESS_DURATION_SELECTION)
                .requiresCount(true)
                .build());
    }

    // 🔥 COMMON IDENTITY BUILDER
    private RuleTypes rule(CheckCategory category, String code, String label) {
        return RuleTypes.builder()
                .name(code)
                .code(code)
                .label(label)
                .category(category)
                .ruleGroup(RuleGroup.DOCUMENT_SELECTION)
                .requiresCount(false)
                .minCount(1)
                .maxCount(1)
                .build();
    }

    // 🔥 SAVE OR UPDATE LOGIC
    private void saveOrUpdate(RuleTypes rule) {

        // Normalize
        String code = rule.getCode().trim().toUpperCase();
        rule.setCode(code);

        log.info("Seeding Rule -> Category: {}, Code: {}", 
                 rule.getCategory().getName(), code);

        List<RuleTypes> existingList =
                ruleRepo.findAllByCategoryAndCodeIgnoreCase(rule.getCategory(), code);

        if (!existingList.isEmpty()) {

            RuleTypes existing = existingList.get(0);

            // UPDATE
            existing.setName(rule.getName());
            existing.setLabel(rule.getLabel());
            existing.setRuleGroup(rule.getRuleGroup());
            existing.setRequiresCount(rule.getRequiresCount());
            existing.setMinCount(rule.getMinCount());
            existing.setMaxCount(rule.getMaxCount());

            ruleRepo.save(existing);

        } else {
            // INSERT
            ruleRepo.save(rule);
        }
    }
}