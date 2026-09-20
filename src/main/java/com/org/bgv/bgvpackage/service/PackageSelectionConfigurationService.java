package com.org.bgv.bgvpackage.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.bgvpackage.entity.PackageCheckCategoryAllowedRuleType;
import com.org.bgv.bgvpackage.repository.PackageCheckCategoryAllowedRuleTypeRepository;
import com.org.bgv.company.dto.AllowedAddOnRuleDTO;
import com.org.bgv.company.dto.AssignCasePreviewResponseDTO;
import com.org.bgv.company.dto.CategoryPreviewDTO;
import com.org.bgv.company.dto.DocumentPreviewDTO;
import com.org.bgv.company.dto.PriceSummaryDTO;
import com.org.bgv.company.dto.PricingInfo;
import com.org.bgv.company.dto.SelectedRuleDTO;
import com.org.bgv.company.entity.EmployerPackageCheckCategoryAllowedRuleType;
import com.org.bgv.company.entity.EmployerPackageRule;
import com.org.bgv.company.entity.EmployerPackageSelectedRule;
import com.org.bgv.company.repository.EmployerPackageAllowedDocumentRepository;
import com.org.bgv.company.repository.EmployerPackageCheckCategoryAllowedRuleTypeRepository;
import com.org.bgv.company.repository.EmployerPackageCheckCategoryRepository;
import com.org.bgv.company.repository.EmployerPackageRuleRepository;
import com.org.bgv.company.repository.EmployerPackageSelectedRuleRepository;
import com.org.bgv.company.service.PackagePricingService;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.entity.BgvPackage;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.PackageCheckCategoryRuleType;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.PricingType;
import com.org.bgv.enums.RuleGroup;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.repository.BgvPackageRepository;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.EmployerPackageDocumentRepository;
import com.org.bgv.repository.EmployerPackageRepository;
import com.org.bgv.repository.PackageCheckCategoryAllowedDocumentRepository;
import com.org.bgv.repository.PackageCheckCategoryRepository;
import com.org.bgv.repository.PackageCheckCategoryRuleTypeRepository;
import com.org.bgv.repository.RuleTypesRepository;
import com.org.bgv.service.PackageService;
import com.org.bgv.service.util.CompanyServiceUtil;
import com.org.bgv.service.util.UserServiceUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PackageSelectionConfigurationService {
	
	 private final BgvPackageRepository packageRepository;
	    private final PackageCheckCategoryRepository packageCheckCategoryRepository;
	    private final PackageCheckCategoryRuleTypeRepository packagecheckcategoryRuleTypeRepository;
	    private final PackageCheckCategoryAllowedDocumentRepository packageAllowedDocumentRepository;
	    private final CheckCategoryRepository checkCategoryRepository;
	    private final RuleTypesRepository ruleTypesRepository;
	    private final DocumentTypeRepository documentTypeRepository;
	    private final EmployerPackageRepository employerPackageRepository;
	    private final PackageCheckCategoryAllowedRuleTypeRepository packageCheckCategoryAllowedRuleTypeRepository;
	    private final EmployerPackageDocumentRepository employerPackageDocumentRepository;
	    private final EmployerPackageRuleRepository employerPackageRuleRepository;
	    private final EmployerPackageCheckCategoryAllowedRuleTypeRepository employerPackageCheckCategoryAllowedRuleTypeRepository;
	    private final EmployerPackageCheckCategoryRepository employerPackageCheckCategoryRepository;
	    private final EmployerPackageAllowedDocumentRepository employerPackageAllowedDocumentRepository;
	    private final UserServiceUtil userServiceUtil;
	    private final PackagePricingService pricingService;
	    private final EmployerPackageSelectedRuleRepository employerPackageSelectedRuleRepository;
	    private final CompanyServiceUtil companyServiceUtil;
	    
	    
	    public AssignCasePreviewResponseDTO getPackageConfigurationSelectionforSelfRegistered(Long packageId) {
	    	
	    	Company company = companyServiceUtil.getDefaultCompany();
	 	   
	 	   BgvPackage bgvPackage =   packageRepository.findById(packageId).orElseThrow(() -> new BusinessException("Package not found"));
	 	   
	 	   
	 	   List<PackageCheckCategoryRuleType> baseRules = packagecheckcategoryRuleTypeRepository.findByBgvPackagePackageId(packageId);
	 	   List<PackageCheckCategoryAllowedRuleType> allowedRules = packageCheckCategoryAllowedRuleTypeRepository.findByBgvPackage_PackageId(packageId);
	 	   
	 	   
	 	   Map<Long, List<PackageCheckCategoryRuleType>> baseRuleMap =
	                baseRules.stream()
	                        .collect(Collectors.groupingBy(PackageCheckCategoryRuleType::getCheckCategoryId));

	 	   Map<Long, List<PackageCheckCategoryAllowedRuleType>> allowedRuleMap =
	 			    allowedRules.stream()
	 			        .collect(Collectors.groupingBy(
	 			            rule -> rule.getCheckCategory().getCategoryId()
	 			        ));
	 	   
	 	   List<CategoryPreviewDTO> categoryPreviews = new ArrayList();
	        
	        Set<Long> allCategoryIds = new HashSet();
	        allCategoryIds.addAll(baseRuleMap.keySet());
	        allCategoryIds.addAll(allowedRuleMap.keySet());

	        for (Long categoryId : allCategoryIds) {

	            List<PackageCheckCategoryRuleType> baseCategoryRules = baseRuleMap.get(categoryId);
	            List<PackageCheckCategoryAllowedRuleType> allowedCategoryRules = allowedRuleMap.getOrDefault(categoryId, Collections.emptyList());

	            CategoryPreviewDTO categoryDTO =
	                    buildCategoryPreview(categoryId, baseCategoryRules, allowedCategoryRules);

	            categoryPreviews.add(categoryDTO);
	        }
	        
	        return AssignCasePreviewResponseDTO.builder()
		               // .candidateId(candidateId)
		                .companyId(company.getId())
		               // .employerPackageId(employerPackageId)
		                .packageName(bgvPackage.getName())
		                .basePrice(bgvPackage.getBasePrice())
		                .addonPrice(BigDecimal.ZERO)
		                .estimatedTotalPrice(bgvPackage.getBasePrice())
		                .finalPriceDynamic(true)
		                .categories(categoryPreviews)
		                .priceSummary(
		                        PriceSummaryDTO.builder()
		                                .basePrice(bgvPackage.getBasePrice())
		                                .addonPrice(BigDecimal.ZERO)
		                                .estimatedTotal(bgvPackage.getBasePrice())
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
	    	
	    	 Company defaultCompany = companyServiceUtil.getDefaultCompany();

	         boolean isEmployerCompany =
	                 defaultCompany != null
	                         && companyId != null
	                         && !Objects.equals(defaultCompany.getId(), companyId);
	    	
	    	PackageCheckCategoryRuleType packageCheckCategoryRuleType = baseRules.get(0);

	    	CheckCategory category = checkCategoryRepository.findById(categoryId)
	    	        .orElseThrow(() -> new RuntimeException("Category not found"));

	        // Base rule (usually one)
	        RuleTypes ruleType =
	                ruleTypesRepository.findById(packageCheckCategoryRuleType.getRuleTypeId())
	                        .orElseThrow();
	        
	        PricingInfo selectedpricingInfo = pricingService.resolvePricing(
                    companyId,
                    categoryId,
                    ruleType,
                    isEmployerCompany
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
	        
	      //  List<SelectedRuleDTO> selectedRuleDTO = mapFromSelectedRules(packageCheckCategoryRuleType.getBgvPackage().getPackageId(), categoryId);

	        CategoryPreviewDTO.CategoryPreviewDTOBuilder builder =
	                CategoryPreviewDTO.builder()
	                        .categoryId(category.getCategoryId())
	                        .categoryName(category.getName())
	                        .ruleGroup(ruleType.getRuleGroup()!=null?ruleType.getRuleGroup().name():"")
	                        .includedInPackage(true)
	                        .mandatory(true)
	                      //  .selectedRule(selectedRuleDTO)
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

	        /*
	        Set<Long> selectedRuleIds = selectedRuleDTO.stream()
	                .map(SelectedRuleDTO::getRuleTypeId)
	                .collect(Collectors.toSet());
*/
	        
	        // Add-ons
	        List<AllowedAddOnRuleDTO> addOnDTOs =
	                allowedRules.stream()
	                // 🔥 KEY FIX → remove already selected rules
	              //  .filter(rule -> !selectedRuleIds.contains(rule.getRuleType().getRuleTypeId()) )
	                .map(rule -> {

	                            RuleTypes addOnRuleType = rule.getRuleType();

	                            PricingInfo pricingInfo = pricingService.resolvePricing(
	                                    companyId,
	                                    categoryId,
	                                    addOnRuleType,
	                                    isEmployerCompany
	                            );
	                            PricingType pricingType = pricingInfo != null ? pricingInfo.getPricingType() : null;
	                           
	                         //   boolean alreadySelected = selectedRuleIds.contains(addOnRuleType.getRuleTypeId());
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
	                                   // .disabled(alreadySelected)
	                                    .requiresCount(addOnRuleType.getRequiresCount()==null?Boolean.FALSE:addOnRuleType.getRequiresCount())
	                                    .build();
	                        })
	                        .collect(Collectors.toList());

	        builder.allowedAddOnRules(addOnDTOs);

	        return builder.build();
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
