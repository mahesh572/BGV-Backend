package com.org.bgv.company.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.bgvpackage.entity.EmployerCheckPricing;
import com.org.bgv.bgvpackage.entity.PlatformCheckPricing;
import com.org.bgv.bgvpackage.repository.EmployerCheckPricingRepository;
import com.org.bgv.bgvpackage.repository.PackageCheckCategoryAllowedRuleTypeRepository;
import com.org.bgv.bgvpackage.repository.PlatformCheckPricingRepository;
import com.org.bgv.company.dto.AllowedAddOnRuleDTO;
import com.org.bgv.company.dto.AssignCasePreviewResponseDTO;
import com.org.bgv.company.dto.CategoryPreviewDTO;
import com.org.bgv.company.dto.DocumentPreviewDTO;
import com.org.bgv.company.dto.EmployerPackageConfigPreviewResponseDTO;
import com.org.bgv.company.dto.EmployerPackageConfigurationRequestDTO;
import com.org.bgv.company.dto.PriceSummaryDTO;
import com.org.bgv.company.dto.PricingInfo;
import com.org.bgv.company.dto.SelectedRuleDTO;
import com.org.bgv.company.entity.EmployerPackageCheckCategoryAllowedRuleType;
import com.org.bgv.company.entity.EmployerPackageRule;
import com.org.bgv.company.entity.EmployerPackageSelectedRule;
import com.org.bgv.company.repository.EmployerPackageCheckCategoryAllowedRuleTypeRepository;
import com.org.bgv.company.repository.EmployerPackageRuleRepository;
import com.org.bgv.company.repository.EmployerPackageSelectedRuleRepository;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.EmployerPackage;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.PricingType;
import com.org.bgv.enums.RuleGroup;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.EmployerPackageRepository;
import com.org.bgv.repository.PackageCheckCategoryRuleTypeRepository;
import com.org.bgv.repository.RuleTypesRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class EmployerPackageConfigurationService {

    private final EmployerPackageRepository employerPackageRepository;
    private final EmployerPackageSelectedRuleRepository employerPackageSelectedRuleRepository;
    private final RuleTypesRepository ruleTypesRepository;
    private final PackageCheckCategoryRuleTypeRepository packageRuleRepository;
    private final PackageCheckCategoryAllowedRuleTypeRepository allowedRuleRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final PlatformCheckPricingRepository platformCheckPricingRepository;
    private final EmployerCheckPricingRepository employerCheckPricingRepository;
    private final EmployerPackageRuleRepository employerPackageRuleRepository;
    private final EmployerPackageCheckCategoryAllowedRuleTypeRepository employerPackageCheckCategoryAllowedRuleTypeRepository;
    private final CheckCategoryRepository checkCategoryRepository;

    @Transactional
    public void updateEmployerPackageConfiguration(
            Long employerPackageId,
            Long companyId,
            EmployerPackageConfigurationRequestDTO request) {

        log.info("🔄 Starting package configuration update. employerPackageId={}, companyId={}",
                employerPackageId, companyId);

        // 1️⃣ Fetch package
        EmployerPackage employerPackage = employerPackageRepository
                .findById(employerPackageId)
                .orElseThrow(() -> {
                    log.error("❌ Employer Package not found. employerPackageId={}", employerPackageId);
                    return new RuntimeException("Employer Package not found");
                });
        
        List<EmployerPackageRule> baseRules =
                employerPackageRuleRepository.findByEmployerPackage_Id(employerPackageId);
        
        Map<Long, List<EmployerPackageRule>> baseRuleMap =
                baseRules.stream()
                        .collect(Collectors.groupingBy(EmployerPackageRule::getCheckCategoryId));

        if (!employerPackage.getCompanyId().equals(companyId)) {
            log.error("❌ Unauthorized access. employerPackageId={}, requestCompanyId={}, actualCompanyId={}",
                    employerPackageId, companyId, employerPackage.getCompanyId());
            throw new RuntimeException("Unauthorized access to package");
        }

        log.info("✅ Employer package fetched successfully. basePrice={}, status={}",
                employerPackage.getBasePrice(), employerPackage.getStatus());

        // 2️⃣ Delete old selections
        log.info("🗑️ Deleting existing rule selections for employerPackageId={}", employerPackageId);
        employerPackageSelectedRuleRepository.deleteByEmployerPackageId(employerPackageId);

        BigDecimal totalAddonPrice = BigDecimal.ZERO;
        int totalRulesSaved = 0;

        // 3️⃣ Save new selections
        if (request.getCategories() == null || request.getCategories().isEmpty()) {
            log.warn("⚠️ No categories provided in request for employerPackageId={}", employerPackageId);
        }

        for (EmployerPackageConfigurationRequestDTO.CategorySelectionDTO category : request.getCategories()) {

            log.info("📂 Processing categoryId={}", category.getCategoryId());

            if (category.getSelectedRules() == null || category.getSelectedRules().isEmpty()) {
                log.warn("⚠️ No rules selected for categoryId={}", category.getCategoryId());
                continue;
            }

            for (EmployerPackageConfigurationRequestDTO.SelectedRuleDTO ruleDto : category.getSelectedRules()) {

                log.info("➡️ Processing ruleTypeId={} for categoryId={}",
                        ruleDto.getRuleTypeId(), category.getCategoryId());
                
                List<EmployerPackageRule> epackageRules =
                        baseRuleMap.getOrDefault(category.getCategoryId(), Collections.emptyList());
                
                EmployerPackageRule employerPackageRule =
                        epackageRules.stream()
                            .findFirst()
                            .orElse(null);
                
                log.info("employerPackageRule:::::::::::::::::::::{}",employerPackageRule);

                // 🔹 Fetch rule master
                RuleTypes ruleType = ruleTypesRepository.findByRuleTypeId(ruleDto.getRuleTypeId())
                        .orElseThrow(() -> {
                            log.error("❌ Rule not found. ruleTypeId={}", ruleDto.getRuleTypeId());
                            return new RuntimeException("Rule not found");
                        });

                int count = ruleDto.getSelectedCount() != null ? ruleDto.getSelectedCount() : 1;

                log.debug("🔢 Rule details: ruleTypeId={}, requiresCount={}, selectedCount={}",
                        ruleType.getRuleTypeId(), ruleType.getRequiresCount(), count);

                
                Optional<EmployerPackageSelectedRule> existing =
                        employerPackageSelectedRuleRepository
                                .findByEmployerPackageIdAndCheckCategoryIdAndRuleTypeRuleTypeId(
                                        employerPackage.getId(),
                                        category.getCategoryId(),
                                        ruleType.getRuleTypeId()
                                );
                
                EmployerPackageSelectedRule entity;

                if (existing.isPresent()) {
                    // 🔁 UPDATE
                    entity = existing.get();

                    log.info("♻️ Updating existing rule: packageId={}, categoryId={}, ruleTypeId={}",
                            employerPackage.getId(), category.getCategoryId(), ruleType.getRuleTypeId());

                } else {
                    // ➕ INSERT
                    entity = new EmployerPackageSelectedRule();

                    entity.setEmployerPackage(employerPackage);
                    entity.setCheckCategoryId(category.getCategoryId());
                    entity.setRuleType(ruleType);

                    log.info("➕ Creating new rule: packageId={}, categoryId={}, ruleTypeId={}",
                            employerPackage.getId(), category.getCategoryId(), ruleType.getRuleTypeId());
                }

                // 🔥 Common fields
                entity.setSelectedCount(count);
                entity.setIncludedInBase(true);
                entity.setRequired(false);
                entity.setRequiresCount(ruleType.getRequiresCount());
                entity.setPackageRule(employerPackageRule);

                employerPackageSelectedRuleRepository.save(entity);
                totalRulesSaved++;

                log.info("✅ Rule saved: employerPackageId={}, categoryId={}, ruleTypeId={}, count={}",
                        employerPackageId, category.getCategoryId(), ruleType.getRuleTypeId(), count);
            }
        }

        // 4️⃣ Update package pricing
        log.info("💰 Calculating final pricing for employerPackageId={}", employerPackageId);

        employerPackage.setAddonPrice(totalAddonPrice);

        BigDecimal basePrice = employerPackage.getBasePrice() != null
                ? employerPackage.getBasePrice()
                : BigDecimal.ZERO;

        BigDecimal finalPrice = basePrice.add(totalAddonPrice);

        employerPackage.setTotalPrice(finalPrice);

        employerPackageRepository.save(employerPackage);

        log.info("🎉 Package configuration update completed successfully. employerPackageId={}, totalRulesSaved={}, basePrice={}, addonPrice={}, totalPrice={}",
                employerPackageId,
                totalRulesSaved,
                basePrice,
                totalAddonPrice,
                finalPrice
        );
    }
    
    public EmployerPackageConfigPreviewResponseDTO buildPreview(
            Long employerPackageId,
            Long companyId
    ) {

        EmployerPackage employerPackage =
                employerPackageRepository.findById(employerPackageId)
                        .orElseThrow(() -> new RuntimeException("Employer Package not found"));

        // 1️⃣ Fetch base included rules
       // List<PackageCheckCategoryRuleType> baseRules = packageRuleRepository.findByBgvPackagePackageId(employerPackage.getBgvPackage().getPackageId());

        List<EmployerPackageRule> baseRules =
                employerPackageRuleRepository.findByEmployerPackage_Id(employerPackageId);
        
        // 2️⃣ Fetch allowed add-on rules
       // List<PackageCheckCategoryAllowedRuleType> allowedRules = allowedRuleRepository.findByBgvPackage_PackageId(employerPackage.getBgvPackage().getPackageId());

        List<EmployerPackageCheckCategoryAllowedRuleType> allowedRules =
        		employerPackageCheckCategoryAllowedRuleTypeRepository.findByEmployerPackage_Id(employerPackageId);
        
        // 3️⃣ Group by category
      //  Map<Long, List<PackageCheckCategoryRuleType>> baseRuleMap = baseRules.stream().collect(Collectors.groupingBy(PackageCheckCategoryRuleType::getCheckCategoryId));

      //  Map<Long, List<PackageCheckCategoryAllowedRuleType>> allowedRuleMap = allowedRules.stream().collect(Collectors.groupingBy(r -> r.getCheckCategory().getCategoryId()));

        Map<Long, List<EmployerPackageRule>> baseRuleMap =
                baseRules.stream()
                        .collect(Collectors.groupingBy(EmployerPackageRule::getCheckCategoryId));

        Map<Long, List<EmployerPackageCheckCategoryAllowedRuleType>> allowedRuleMap =
                allowedRules.stream()
                        .collect(Collectors.groupingBy(EmployerPackageCheckCategoryAllowedRuleType::getCheckCategoryId));
        
        
        List<CategoryPreviewDTO> categoryPreviews = new ArrayList();
        
        Set<Long> allCategoryIds = new HashSet();
        allCategoryIds.addAll(baseRuleMap.keySet());
        allCategoryIds.addAll(allowedRuleMap.keySet());

        for (Long categoryId : allCategoryIds) {

            List<EmployerPackageRule> baseCategoryRules = baseRuleMap.get(categoryId);
            List<EmployerPackageCheckCategoryAllowedRuleType> allowedCategoryRules = allowedRuleMap.getOrDefault(categoryId, Collections.emptyList());

            CategoryPreviewDTO categoryDTO =
                    buildCategoryPreview(categoryId, baseCategoryRules, allowedCategoryRules);

            categoryPreviews.add(categoryDTO);
        }

        return EmployerPackageConfigPreviewResponseDTO.builder()
               // .candidateId(candidateId)
                .companyId(companyId)
                .employerPackageId(employerPackageId)
                .packageName(employerPackage.getBgvPackage().getName())
                .basePrice(employerPackage.getBasePrice())
                .addonPrice(BigDecimal.ZERO)
                .estimatedTotalPrice(employerPackage.getBasePrice())
                .finalPriceDynamic(true)
                .categories(categoryPreviews)
                .priceSummary(
                        PriceSummaryDTO.builder()
                                .basePrice(employerPackage.getBasePrice())
                                .addonPrice(BigDecimal.ZERO)
                                .estimatedTotal(employerPackage.getBasePrice())
                                .finalPriceCalculatedAfterSubmission(true)
                                .note("Final price depends on record count.")
                                .build()
                )
                .build();
    }
    

    private CategoryPreviewDTO buildCategoryPreview(
    		Long categoryId,
    	    List<EmployerPackageRule> baseRules,
    	    List<EmployerPackageCheckCategoryAllowedRuleType> allowedRules
    ) {
    	
    	Long companyId = SecurityUtils.getCurrentUserCompanyId();
    	
    	EmployerPackageRule packageCheckCategoryRuleType = baseRules.get(0);

    	CheckCategory category = checkCategoryRepository.findById(categoryId)
    	        .orElseThrow(() -> new RuntimeException("Category not found"));

        // Base rule (usually one)
        RuleTypes ruleType =
                ruleTypesRepository.findById(packageCheckCategoryRuleType.getRuleTypeId())
                        .orElseThrow();
        
        PricingInfo selectedpricingInfo = resolvePricing(
                companyId,
                categoryId,
                ruleType
        );

        SelectedRuleDTO packageRuleDTO =
                SelectedRuleDTO.builder()
                        .ruleTypeId(ruleType.getRuleTypeId())
                        .ruleCode(ruleType.getCode())
                        .ruleLabel(ruleType.getLabel())
                        .pricingType(selectedpricingInfo.getPricingType()!=null?selectedpricingInfo.getPricingType().name():null)
                        .minCount(ruleType.getMinCount())
                        .maxCount(ruleType.getMaxCount())
                        .ruleGroup(ruleType.getRuleGroup()!=null?ruleType.getRuleGroup().name():"")
                        .includedInPackage(true)
                        .requiresCount(packageCheckCategoryRuleType.getRequiresCount())
                        .selectedCount(packageCheckCategoryRuleType.getSelectedCount())
                        .addon(false)
                        .requiresCount(ruleType.getRequiresCount()==null?Boolean.FALSE:ruleType.getRequiresCount())
                        .build();
        
        List<SelectedRuleDTO> selectedRuleDTO = mapFromSelectedRules(packageCheckCategoryRuleType.getEmployerPackage().getId(), categoryId);

        CategoryPreviewDTO.CategoryPreviewDTOBuilder builder =
                CategoryPreviewDTO.builder()
                        .categoryId(category.getCategoryId())
                        .categoryName(category.getName())
                        .ruleGroup(ruleType.getRuleGroup()!=null?ruleType.getRuleGroup().name():"")
                        .includedInPackage(true)
                        .mandatory(true)
                        .selectedRule(selectedRuleDTO)
                        .packageRules(packageRuleDTO);
                        
                        
     //  log.info("ruleType.getRuleGroup().name()::::::::::::{}",ruleType.getRuleGroup().name());
       log.info("RuleGroup.DOCUMENT_SELECTION::::::::::::{}",RuleGroup.DOCUMENT_SELECTION);
        // If DOCUMENT_SELECTION → Load documents
        if (ruleType.getRuleGroup() == RuleGroup.DOCUMENT_SELECTION) {
        	
        	log.info("DOCUMENT_SELECTION:::::::::::::::::::::::::::::::::::::::YES");

            List<DocumentType> documents =
                    documentTypeRepository.findByCategoryCategoryId(categoryId);
            
            log.info("documents:::::::::::::::::::::::::::{}",documents.size());
            
         // Extract allowed rule names
            List<String> allowedRuleNames = allowedRules.stream()
                            .map(r -> r.getRuleType().getName().toLowerCase())
                            .collect(Collectors.toList());

            List<DocumentPreviewDTO> docDTOs =
                    documents.stream()
                    .filter(doc -> {

                        String docName = doc.getName().toLowerCase();

                        // check if any rule name matches document name
                        return allowedRuleNames.stream()
                                .anyMatch(ruleName -> docName.contains(ruleName)
                                        || ruleName.contains(docName));
                    })
                            .map(doc -> DocumentPreviewDTO.builder()
                                    .documentTypeId(doc.getDocTypeId())
                                    .documentName(doc.getName())
                                    .selected(false)
                                    .price(doc.getPrice())
                                    .build())
                            .collect(Collectors.toList());

          //  builder.documents(docDTOs);
        }
        log.info("AssignCaseService::::::::::::category.getName():::::::::::{}",category.getName());
        log.info("AssignCaseService::::::::::::allowedRules:::::::::::{}",allowedRules);

        // Add-ons
        List<AllowedAddOnRuleDTO> addOnDTOs =
                allowedRules.stream()
                        .map(rule -> {

                            RuleTypes addOnRuleType = rule.getRuleType();

                            PricingInfo pricingInfo = resolvePricing(
                                    companyId,
                                    categoryId,
                                    addOnRuleType
                            );
                            PricingType pricingType = pricingInfo != null ? pricingInfo.getPricingType() : null;
                            return AllowedAddOnRuleDTO.builder()
                                    .ruleTypeId(addOnRuleType.getRuleTypeId())
                                    .ruleCode(addOnRuleType.getCode())
                                    .ruleLabel(addOnRuleType.getLabel())
                                    .ruleGroup(ruleType.getRuleGroup()!=null?ruleType.getRuleGroup().name():"")
                                    .pricingType(pricingInfo.getPricingType()!=null?pricingInfo.getPricingType().name():null)
                                    .unitPrice(pricingInfo.getUnitPrice()!=null?pricingInfo.getUnitPrice():null)
                                    .dynamicPricing(
                                    		PricingType.PER_RECORD.equals(pricingType)
                                    )
                                    .selected(false)
                                    .requiresCount(addOnRuleType.getRequiresCount()==null?Boolean.FALSE:addOnRuleType.getRequiresCount())
                                    .build();
                        })
                        .collect(Collectors.toList());

        builder.allowedAddOnRules(addOnDTOs);

        return builder.build();
    }

    private PricingInfo resolvePricing(
            Long companyId,
            Long categoryId,
            RuleTypes ruleType
    ) {

        // 1️⃣ Try Employer specific pricing
        Optional<EmployerCheckPricing> employerPricing =
                employerCheckPricingRepository
                        .findByCompany_IdAndCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(
                                companyId,
                                categoryId,
                                ruleType.getRuleTypeId()
                        );

        if (employerPricing.isPresent()) {
            EmployerCheckPricing pricing = employerPricing.get();
            
            return  PricingInfo.builder()
            .pricingType(pricing.getPricingType())
            .unitPrice(pricing.getUnitPrice())
            .minCharge(pricing.getMinCharge())
            .maxCharge(pricing.getMaxCharge())
            .build();
            
            
        }

        // 2️⃣ Fallback to Platform default pricing
        PlatformCheckPricing platformPricing =
                platformCheckPricingRepository
                        .findByCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(
                                categoryId,
                                ruleType.getRuleTypeId()
                        )
                        .orElse(null);
        
        return  PricingInfo.builder()
        		.pricingType(platformPricing!=null?platformPricing.getPricingType():null)
                .unitPrice(platformPricing!=null?platformPricing.getUnitPrice():null)
        		
        		.build();

        
    }
    
    private List<SelectedRuleDTO> mapFromSelectedRules(
            Long employerPackageId,
            Long categoryId
    ) {

        log.info("Fetching selected rules for employerPackageId={}, categoryId={}",
                employerPackageId, categoryId);

        List<EmployerPackageSelectedRule> selectedRules =
                employerPackageSelectedRuleRepository
                        .findByEmployerPackageIdAndCheckCategoryId(
                                employerPackageId, categoryId
                        );

        if (selectedRules.isEmpty()) {
            log.info("No selected rules found for categoryId={}", categoryId);
            return Collections.emptyList();
        }

        List<SelectedRuleDTO> response = new ArrayList<>();

        for (EmployerPackageSelectedRule entity : selectedRules) {

            RuleTypes ruleType = entity.getRuleType();

            SelectedRuleDTO dto = SelectedRuleDTO.builder()
                    .ruleTypeId(ruleType.getRuleTypeId())
                    .ruleCode(ruleType.getCode())
                    .ruleLabel(ruleType.getLabel())
                    .ruleGroup(ruleType.getRuleGroup() != null
                            ? ruleType.getRuleGroup().name() : null)
                    .pricingType(entity.getUnitPrice() != null ? "FIXED" : null)
                    .minCount(ruleType.getMinCount())
                    .maxCount(ruleType.getMaxCount())
                    .includedInPackage(Boolean.TRUE.equals(entity.getIncludedInBase()))
                    .requiresCount(entity.getRequiresCount())
                    .selectedCount(entity.getSelectedCount())
                    .addon(!Boolean.TRUE.equals(entity.getIncludedInBase()))
                    .build();

            response.add(dto);
        }

        log.info("Mapped {} selected rules for categoryId={}", response.size(), categoryId);

        return response;
    }
}