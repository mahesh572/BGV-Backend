package com.org.bgv.company.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.bgvpackage.entity.EmployerCheckPricing;
import com.org.bgv.bgvpackage.entity.PackageCheckCategoryAllowedRuleType;
import com.org.bgv.bgvpackage.entity.PlatformCheckPricing;
import com.org.bgv.bgvpackage.repository.EmployerCheckPricingRepository;
import com.org.bgv.bgvpackage.repository.PackageCheckCategoryAllowedRuleTypeRepository;
import com.org.bgv.bgvpackage.repository.PlatformCheckPricingRepository;
import com.org.bgv.company.dto.AllowedAddOnRuleDTO;
import com.org.bgv.company.dto.AssignCasePreviewResponseDTO;
import com.org.bgv.company.dto.CategoryPreviewDTO;
import com.org.bgv.company.dto.DocumentPreviewDTO;
import com.org.bgv.company.dto.PriceSummaryDTO;
import com.org.bgv.company.dto.PricingInfo;
import com.org.bgv.company.dto.SelectedRuleDTO;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.EmployerPackage;
import com.org.bgv.entity.PackageCheckCategoryRuleType;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.PricingType;
import com.org.bgv.enums.RuleGroup;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.EmployerPackageRepository;
import com.org.bgv.repository.PackageCheckCategoryRuleTypeRepository;
import com.org.bgv.repository.RuleTypesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignCaseService {
	
	    private final EmployerPackageRepository employerPackageRepository;
	    private final PackageCheckCategoryRuleTypeRepository packageRuleRepository;
	    private final PackageCheckCategoryAllowedRuleTypeRepository allowedRuleRepository;
	    private final DocumentTypeRepository documentTypeRepository;
	    private final RuleTypesRepository ruleTypesRepository;
	    private final PlatformCheckPricingRepository platformCheckPricingRepository;
	    private final EmployerCheckPricingRepository employerCheckPricingRepository;

	    
	    public AssignCasePreviewResponseDTO buildPreview(
	            Long employerPackageId,
	            Long companyId
	    ) {

	        EmployerPackage employerPackage =
	                employerPackageRepository.findById(employerPackageId)
	                        .orElseThrow(() -> new RuntimeException("Employer Package not found"));

	        // 1️⃣ Fetch base included rules
	        List<PackageCheckCategoryRuleType> baseRules =
	                packageRuleRepository.findByBgvPackagePackageId(employerPackage.getBgvPackage().getPackageId());

	        // 2️⃣ Fetch allowed add-on rules
	        List<PackageCheckCategoryAllowedRuleType> allowedRules =
	                allowedRuleRepository.findByBgvPackage_PackageId(employerPackage.getBgvPackage().getPackageId());

	        // 3️⃣ Group by category
	        Map<Long, List<PackageCheckCategoryRuleType>> baseRuleMap =
	                baseRules.stream()
	                        .collect(Collectors.groupingBy(PackageCheckCategoryRuleType::getCheckCategoryId));

	        Map<Long, List<PackageCheckCategoryAllowedRuleType>> allowedRuleMap =
	                allowedRules.stream()
	                        .collect(Collectors.groupingBy(r -> r.getCheckCategory().getCategoryId()));

	        List<CategoryPreviewDTO> categoryPreviews = new ArrayList();

	        for (Long categoryId : baseRuleMap.keySet()) {

	            List<PackageCheckCategoryRuleType> baseCategoryRules = baseRuleMap.get(categoryId);
	            List<PackageCheckCategoryAllowedRuleType> allowedCategoryRules =
	                    allowedRuleMap.getOrDefault(categoryId, Collections.emptyList());

	            CategoryPreviewDTO categoryDTO =
	                    buildCategoryPreview(categoryId, baseCategoryRules, allowedCategoryRules);

	            categoryPreviews.add(categoryDTO);
	        }

	        return AssignCasePreviewResponseDTO.builder()
	               // .candidateId(candidateId)
	                .companyId(companyId)
	                .employerPackageId(employerPackageId)
	                .packageName(employerPackage.getBgvPackage().getName())
	                .basePrice(employerPackage.getBasePrice())
	                .addonPrice(0.0)
	                .estimatedTotalPrice(employerPackage.getBasePrice())
	                .finalPriceDynamic(true)
	                .categories(categoryPreviews)
	                .priceSummary(
	                        PriceSummaryDTO.builder()
	                                .basePrice(employerPackage.getBasePrice())
	                                .addonPrice(0.0)
	                                .estimatedTotal(employerPackage.getBasePrice())
	                                .finalPriceCalculatedAfterSubmission(true)
	                                .note("Final price depends on record count.")
	                                .build()
	                )
	                .build();
	    }
	    
	    private CategoryPreviewDTO buildCategoryPreview(
	            Long categoryId,
	            List<PackageCheckCategoryRuleType> baseRules,
	            List<PackageCheckCategoryAllowedRuleType> allowedRules
	    ) {
	    	
	    	Long companyId = SecurityUtils.getCurrentUserCompanyId();

	        CheckCategory category = baseRules.get(0).getBgvPackage()
	                .getPackageCheckCategories()
	                .stream()
	                .filter(c -> c.getCategory().getCategoryId().equals(categoryId))
	                .findFirst()
	                .orElseThrow()
	                .getCategory();

	        // Base rule (usually one)
	        RuleTypes ruleType =
	                ruleTypesRepository.findById(baseRules.get(0).getRuleTypeId())
	                        .orElseThrow();

	        SelectedRuleDTO selectedRuleDTO =
	                SelectedRuleDTO.builder()
	                        .ruleTypeId(ruleType.getRuleTypeId())
	                        .ruleCode(ruleType.getCode())
	                        .ruleLabel(ruleType.getLabel())
	                        .minCount(ruleType.getMinCount())
	                        .maxCount(ruleType.getMaxCount())
	                        .includedInPackage(true)
	                        .addon(false)
	                        .requiresCount(ruleType.getRequiresCount()==null?Boolean.FALSE:ruleType.getRequiresCount())
	                        .build();

	        CategoryPreviewDTO.CategoryPreviewDTOBuilder builder =
	                CategoryPreviewDTO.builder()
	                        .categoryId(category.getCategoryId())
	                        .categoryName(category.getName())
	                        .ruleGroup(ruleType.getRuleGroup()!=null?ruleType.getRuleGroup().name():"")
	                        .includedInPackage(true)
	                        .mandatory(true)
	                        .selectedRule(selectedRuleDTO);
         //  log.info("ruleType.getRuleGroup().name()::::::::::::{}",ruleType.getRuleGroup().name());
           log.info("RuleGroup.DOCUMENT_SELECTION::::::::::::{}",RuleGroup.DOCUMENT_SELECTION);
	        // If DOCUMENT_SELECTION → Load documents
	        if (ruleType.getRuleGroup() == RuleGroup.DOCUMENT_SELECTION) {
	        	
	        	log.info("DOCUMENT_SELECTION:::::::::::::::::::::::::::::::::::::::YES");

	            List<DocumentType> documents =
	                    documentTypeRepository.findByCategoryCategoryId(categoryId);
	            
	            log.info("documents:::::::::::::::::::::::::::{}",documents.size());
	            
	         // Extract allowed rule names
	            List<String> allowedRuleNames =
	                    allowedRules.stream()
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

	            builder.documents(docDTOs);
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
	            return new PricingInfo(
	                    pricing.getPricingType(),
	                    pricing.getUnitPrice(),
	                    pricing.getMinCharge(),
	                    pricing.getMaxCharge()
	            );
	        }

	        // 2️⃣ Fallback to Platform default pricing
	        PlatformCheckPricing platformPricing =
	                platformCheckPricingRepository
	                        .findByCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(
	                                categoryId,
	                                ruleType.getRuleTypeId()
	                        )
	                        .orElse(null);

	        return new PricingInfo(
	        		platformPricing!=null?platformPricing.getPricingType():null,
	        		platformPricing!=null?platformPricing.getUnitPrice():null,
	                null,
	                null
	        );
	    }
	    
	    
	    
}
