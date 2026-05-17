package com.org.bgv.service;

import com.org.bgv.bgvpackage.dto.PackageRuleTypeRequest;
import com.org.bgv.bgvpackage.entity.PackageCheckCategoryAllowedRuleType;
import com.org.bgv.bgvpackage.repository.PackageCheckCategoryAllowedRuleTypeRepository;
import com.org.bgv.common.PackageCategoryDTO;
import com.org.bgv.common.PackageCategoryRequest;
import com.org.bgv.common.PackageDTO;
import com.org.bgv.common.PackageDocumentDTO;
import com.org.bgv.common.PackageDocumentRequest;
import com.org.bgv.common.PackageRequest;
import com.org.bgv.common.PackageRuleTypeDTO;
import com.org.bgv.common.RuleTypesDTO;
import com.org.bgv.company.entity.EmployerPackageCheckCategoryAllowedRuleType;
import com.org.bgv.company.entity.EmployerPackageRule;
import com.org.bgv.company.repository.EmployerPackageCheckCategoryAllowedRuleTypeRepository;
import com.org.bgv.company.repository.EmployerPackageRuleRepository;
import com.org.bgv.constants.EmployerPackageStatus;
import com.org.bgv.dto.*;
import com.org.bgv.entity.*;
import com.org.bgv.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PackageService  {

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

   

    
    @Transactional
    public PackageDTO createPackage(PackageRequest request) {
        log.info("Creating new package with code: {}", request.getCode());
        
        // Check if package code already exists
        if (packageRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Package with code " + request.getCode() + " already exists");
        }
        
        // Create package entity
        BgvPackage bgvPackage = BgvPackage.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .customizable(request.getCustomizable())
                .basePrice(request.getBasePrice())
                .isActive(request.getIsActive())
                .price(request.getPrice())
                .build();
        
        BgvPackage savedPackage = packageRepository.save(bgvPackage);
        log.info("Package created successfully with ID: {}", savedPackage.getPackageId());
        
        // Process categories if provided
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            processPackageCategories(savedPackage, request.getCategories());
        }
        
        return convertToDTO(savedPackage,Boolean.TRUE);
    }

    
    @Transactional(readOnly = true)
    public PackageDTO getPackageById(Long packageId) {
        log.debug("Fetching package by ID: {}", packageId);
        
        BgvPackage bgvPackage = packageRepository.findByIdWithCategories(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + packageId));
        
        return convertToDTO(bgvPackage,Boolean.TRUE);
    }

    
    @Transactional(readOnly = true)
    public List<PackageDTO> getAllPackages() {
        log.debug("Fetching all packages");
        
        return packageRepository.findAll().stream()
        		.map(pkg -> convertToDTO(pkg, Boolean.TRUE)) // categories not required
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PackageDTO> getAllPackages(Long companyId) {
       
        log.debug("Fetching all packages for company: {}", companyId);
        
        // Fetch all packages
        List<BgvPackage> allPackages = packageRepository.findAll();
        
        // Fetch employer packages for this company to check assignment
        List<EmployerPackage> employerPackages = employerPackageRepository.findByCompanyId(companyId);
        
        // Create a set of package IDs that are assigned to this employer with ACTIVE status
        Set<Long> assignedPackageIds = employerPackages.stream()
                .filter(employerPackage -> employerPackage.getStatus() == EmployerPackageStatus.ACTIVE)
                .map(employerPackage -> employerPackage.getBgvPackage().getPackageId())
                .collect(Collectors.toSet());
        
        log.info("getAllPackages - assignedPackageIds: {}", assignedPackageIds);
        
        return allPackages.stream()
                .map(bgvpackage -> {
                    PackageDTO dto = convertToDTO(bgvpackage, Boolean.FALSE);
                    // Check if this package is assigned to the employer
                    boolean isAssigned = assignedPackageIds.contains(bgvpackage.getPackageId());
                    dto.setIsAssigned(isAssigned);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PackageDTO> getActivePackages() {
        log.debug("Fetching active packages");
        
        return packageRepository.findByIsActiveTrue().stream()
                .map(pkg -> convertToDTO(pkg, Boolean.TRUE))
                .collect(Collectors.toList());
    }

   
    @Transactional
    public PackageDTO updatePackage(Long packageId, PackageRequest request) {
        log.info("Updating package with ID: {}", packageId);
        
        // Find existing package
        BgvPackage existingPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + packageId));
        
        // Check if code is being changed and if new code already exists
        if (!existingPackage.getCode().equals(request.getCode()) && 
            packageRepository.existsByCodeAndPackageIdNot(request.getCode(), packageId)) {
            throw new RuntimeException("Package with code " + request.getCode() + " already exists");
        }
        
        // Update package fields
        existingPackage.setName(request.getName());
        existingPackage.setCode(request.getCode());
        existingPackage.setDescription(request.getDescription());
        existingPackage.setCustomizable(request.getCustomizable());
        existingPackage.setBasePrice(request.getBasePrice());
        existingPackage.setIsActive(request.getIsActive());
        existingPackage.setPrice(request.getPrice());
        
        BgvPackage updatedPackage = packageRepository.save(existingPackage);
        
        // Process categories - delete existing and create new
        processPackageUpdate(updatedPackage, request.getCategories());
        
        log.info("Package updated successfully with ID: {}", updatedPackage.getPackageId());
        return convertToDTO(updatedPackage,Boolean.TRUE);
    }

    
    @Transactional
    public void deletePackage(Long packageId) {
        log.info("Deleting package with ID: {}", packageId);
        
        // Check if package exists
        if (!packageRepository.existsById(packageId)) {
            throw new RuntimeException("Package not found with id: " + packageId);
        }
        
        // Delete related entities first (cascade should handle this, but being explicit)
        packageAllowedDocumentRepository.deleteByPackageId(packageId);
        packagecheckcategoryRuleTypeRepository.deleteByBgvPackagePackageId(packageId);
        packageCheckCategoryRepository.deleteByPackageId(packageId);
        
        packageRepository.deleteById(packageId);
        log.info("Package deleted successfully with ID: {}", packageId);
    }

   
    @Transactional
    public PackageDTO togglePackageStatus(Long packageId, Boolean isActive) {
        log.info("Updating package status for ID: {} to {}", packageId, isActive);
        
        BgvPackage bgvPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + packageId));
        
        bgvPackage.setIsActive(isActive);
        BgvPackage updatedPackage = packageRepository.save(bgvPackage);
        
        return convertToDTO(updatedPackage,Boolean.TRUE);
    }

    // ================= PRIVATE HELPER METHODS =================

    private void processPackageCategories(BgvPackage bgvPackage, List<PackageCategoryRequest> categories) {
        for (PackageCategoryRequest categoryRequest : categories) {
            // Validate category exists
            CheckCategory category = checkCategoryRepository.findById(categoryRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryRequest.getCategoryId()));
            
            // Create package-category relationship
            PackageCheckCategory packageCategory = PackageCheckCategory.builder()
                    .bgvPackage(bgvPackage)
                    .category(category)
                    .rulesData(categoryRequest.getRulesData())
                    .build();
            
            PackageCheckCategory savedPackageCategory = packageCheckCategoryRepository.save(packageCategory);
            
            List<RuleTypesDTO> ruleTypes =categoryRequest.getRuleTypes();
            // Process rule types
            if (ruleTypes != null && !ruleTypes.isEmpty()) {
                
                processRuleTypes(bgvPackage, category.getCategoryId(), ruleTypes);
            }
          
            if (categoryRequest.getAllowedRules() != null && !categoryRequest.getAllowedRules().isEmpty()) {
            	processAllowedRuleTypes(bgvPackage, category, categoryRequest.getAllowedRules());
            }
        }
    }
    
    @Transactional
    private void processAllowedRuleTypes(
            BgvPackage bgvPackage,
            CheckCategory category,
            List<PackageRuleTypeRequest> ruleRequests) {

        Long packageId = bgvPackage.getPackageId();
        Long categoryId = category.getCategoryId();

        // 1️⃣ Delete existing
        packageCheckCategoryAllowedRuleTypeRepository
                .deleteByBgvPackage_PackageIdAndCheckCategory_CategoryId(
                        packageId, categoryId
                );

        packageCheckCategoryAllowedRuleTypeRepository.flush();

        if (ruleRequests == null || ruleRequests.isEmpty()) {
            return;
        }

        // 2️⃣ Keep only enabled rules
        Set<Long> uniqueRuleTypeIds = ruleRequests.stream()
               // .filter(r -> Boolean.TRUE.equals(r.getEnabled()))
                .map(PackageRuleTypeRequest::getRuleTypeId)
                .collect(Collectors.toSet());

        if (uniqueRuleTypeIds.isEmpty()) {
            return;
        }

        // 3️⃣ Fetch all RuleTypes in one query (avoid N+1)
        List<RuleTypes> ruleTypes =
                ruleTypesRepository.findAllById(uniqueRuleTypeIds);

        for (RuleTypes ruleType : ruleTypes) {

            PackageCheckCategoryAllowedRuleType allowedRule =
                    PackageCheckCategoryAllowedRuleType.builder()
                            .bgvPackage(bgvPackage)
                            .checkCategory(category)
                            .ruleType(ruleType)
                            .required(false)
                            .build();

            packageCheckCategoryAllowedRuleTypeRepository.save(allowedRule);
        }
    }

   
    private void processRuleTypes(
            BgvPackage bgvPackage,
            Long categoryId,
            List<RuleTypesDTO> ruleTypes) {

        // 1. Fetch existing
        List<PackageCheckCategoryRuleType> existingList =
                packagecheckcategoryRuleTypeRepository
                        .findByBgvPackageAndCheckCategoryId(bgvPackage, categoryId);

        Map<Long, PackageCheckCategoryRuleType> existingMap =
                existingList.stream()
                        .collect(Collectors.toMap(
                                PackageCheckCategoryRuleType::getRuleTypeId,
                                e -> e
                        ));

        // 2. New IDs
        Set<Long> newRuleTypeIds = ruleTypes.stream()
                .map(RuleTypesDTO::getRuleTypeId)
                .collect(Collectors.toSet());

        // =========================
        // DELETE removed ones
        // =========================
        List<PackageCheckCategoryRuleType> toDelete = existingList.stream()
                .filter(e -> !newRuleTypeIds.contains(e.getRuleTypeId()))
                .toList();

        if (!toDelete.isEmpty()) {
            packagecheckcategoryRuleTypeRepository.deleteAll(toDelete);
        }

        // =========================
        // ADD or UPDATE
        // =========================
        List<PackageCheckCategoryRuleType> toSave = new ArrayList<>();

        for (RuleTypesDTO dto : ruleTypes) {

            PackageCheckCategoryRuleType existing = existingMap.get(dto.getRuleTypeId());

            if (existing != null) {
                // 🔁 UPDATE
                existing.setSelectedCount(dto.getSelectedCount());
              //  existing.setRequired(dto.getRequired());
               // existing.setPriorityOrder(dto.getPriorityOrder());

                toSave.add(existing);

            } else {
                // ➕ INSERT

                if (!ruleTypesRepository.existsById(dto.getRuleTypeId())) {
                    throw new RuntimeException(
                            "Rule type not found with id: " + dto.getRuleTypeId()
                    );
                }

                PackageCheckCategoryRuleType entity =
                        PackageCheckCategoryRuleType.builder()
                                .bgvPackage(bgvPackage)
                                .checkCategoryId(categoryId)
                                .ruleTypeId(dto.getRuleTypeId())
                                .selectedCount(dto.getSelectedCount())
                              //  .required(dto.getRequired())
                               // .priorityOrder(dto.getPriorityOrder())
                                .build();

                toSave.add(entity);
            }
        }

        // 🔥 Batch save (important)
        if (!toSave.isEmpty()) {
            packagecheckcategoryRuleTypeRepository.saveAll(toSave);
        }
    }
/*
    private void processAllowedDocuments(BgvPackage bgvPackage, CheckCategory category, List<PackageDocumentRequest> documentRequests) {
        for (PackageDocumentRequest docRequest : documentRequests) {
            // Validate document type exists
            DocumentType documentType = documentTypeRepository.findById(docRequest.getDocumentTypeId())
                    .orElseThrow(() -> new RuntimeException("Document type not found with id: " + docRequest.getDocumentTypeId()));
            
            PackageCheckCategoryAllowedDocument allowedDoc = PackageCheckCategoryAllowedDocument.builder()
                    .bgvPackage(bgvPackage)
                    .checkCategory(category)
                    .documentType(documentType)
                    .required(docRequest.getRequired() != null ? docRequest.getRequired() : false)
                    .priorityOrder(docRequest.getPriorityOrder())
                    .build();
            
            packageAllowedDocumentRepository.save(allowedDoc);
        }
    }
*/
    private void processPackageUpdate(BgvPackage bgvPackage, List<PackageCategoryRequest> categories) {
        // Delete existing relationships
        packageAllowedDocumentRepository.deleteByPackageId(bgvPackage.getPackageId());
        packagecheckcategoryRuleTypeRepository.deleteByBgvPackagePackageId(bgvPackage.getPackageId());
        packageCheckCategoryRepository.deleteByPackageId(bgvPackage.getPackageId());
        
        // Create new relationships if categories provided
        if (categories != null && !categories.isEmpty()) {
            processPackageCategories(bgvPackage, categories);
        }
    }

    private PackageDTO convertToDTO(BgvPackage bgvPackage, Boolean isCategoriesRequired) {

        List<PackageCategoryDTO> categoryDTOs = null;

        // Only load categories if required (1 = true)
        if (isCategoriesRequired != null && isCategoriesRequired == Boolean.TRUE) {
            List<PackageCheckCategory> packageCategories =
                    packageCheckCategoryRepository.findByBgvPackage_PackageId(bgvPackage.getPackageId());

            categoryDTOs = packageCategories.stream()
                    .map(this::convertToCategoryDTO)
                    .collect(Collectors.toList());
        }

        return PackageDTO.builder()
                .packageId(bgvPackage.getPackageId())
                .name(bgvPackage.getName())
                .code(bgvPackage.getCode())
                .description(bgvPackage.getDescription())
                .customizable(bgvPackage.getCustomizable())
                .basePrice(bgvPackage.getBasePrice())
                .isActive(bgvPackage.getIsActive())
                .price(bgvPackage.getPrice())
                .categories(categoryDTOs)   // will be null if not required
                .build();
    }

    private PackageCategoryDTO convertToCategoryDTO(PackageCheckCategory packageCategory) {
        CheckCategory category = packageCategory.getCategory();
        
        // Fetch rule types for this package-category combination
        List<PackageCheckCategoryRuleType> packageRuleTypes = packagecheckcategoryRuleTypeRepository
                .findByBgvPackagePackageIdAndCheckCategoryId(packageCategory.getBgvPackage().getPackageId(), category.getCategoryId());
        
        List<PackageRuleTypeDTO> ruleTypeDTOs = packageRuleTypes.stream()
                .map(this::convertToRuleTypeDTO)
                .collect(Collectors.toList());
        
        // Fetch allowed documents for this package-category combination
        List<PackageCheckCategoryAllowedDocument> allowedDocuments = packageAllowedDocumentRepository
                .findByBgvPackagePackageIdAndCheckCategoryCategoryId(packageCategory.getBgvPackage().getPackageId(), category.getCategoryId());
        
        List<PackageDocumentDTO> documentDTOs = allowedDocuments.stream()
                .map(this::convertToDocumentDTO)
                .collect(Collectors.toList());
        
        // Fetch allowed rule types for this package-category combination
        List<PackageCheckCategoryAllowedRuleType> allowedRuleTypes =
                packageCheckCategoryAllowedRuleTypeRepository
                        .findByBgvPackage_PackageIdAndCheckCategory_CategoryId(
                        		packageCategory.getBgvPackage().getPackageId(),
                        		category.getCategoryId()
                        );
        
        List<PackageRuleTypeDTO> allowedRuleDTOs = allowedRuleTypes.stream()
                .map(this::convertToAllowedRuleTypeDTO)
                .collect(Collectors.toList());
        
        return PackageCategoryDTO.builder()
                .id(packageCategory.getId())
                .categoryId(category.getCategoryId())
                .categoryName(category.getName())
                .categoryCode(category.getCode())
                .rulesData(packageCategory.getRulesData())
                .ruleTypes(ruleTypeDTOs)
                .allowedDocuments(documentDTOs)
                .allowedRules(allowedRuleDTOs) 
                .build();
    }
    
    
    private PackageRuleTypeDTO convertToAllowedRuleTypeDTO(
            PackageCheckCategoryAllowedRuleType allowedRule) {

        RuleTypes ruleType = allowedRule.getRuleType();

        return PackageRuleTypeDTO.builder()
                .id(allowedRule.getId())
                .ruleTypeId(ruleType.getRuleTypeId())
                .ruleName(ruleType.getName())
                .ruleCode(ruleType.getCode())
                .minCount(ruleType.getMinCount() != null ? ruleType.getMinCount() : 0)
                .maxCount(ruleType.getMaxCount() != null ? ruleType.getMaxCount() : 0)
                .required(Boolean.TRUE.equals(allowedRule.getRequired()))
                .priorityOrder(allowedRule.getPriorityOrder())
                .selected(true) // since this is allowed in package
                .build();
    }


    private PackageRuleTypeDTO convertToRuleTypeDTO(PackageCheckCategoryRuleType packageRuleType) {
        // You might want to fetch the actual rule type entity for more details
    	// RuleTypes ruleTypes = ruleTypesRepository.findById(packageRuleType.getRuleTypeId()).orElseThrow(()->new RuntimeException("rule type not found:"+packageRuleType.getRuleTypeId()));
    	Optional<RuleTypes> ruleTypesOpt = ruleTypesRepository.findById(packageRuleType.getRuleTypeId());

    	if (ruleTypesOpt.isEmpty()) {
    	    log.error("Invalid ruleTypeId found: {}", packageRuleType.getRuleTypeId());
    	    return null; // or skip
    	}

    	RuleTypes ruleTypes = ruleTypesOpt.get();
        return PackageRuleTypeDTO.builder()
                .id(packageRuleType.getId())
                .ruleTypeId(packageRuleType.getRuleTypeId())
                .ruleCode(ruleTypes.getCode())
                .ruleName(ruleTypes.getName())
                .minCount(ruleTypes.getMinCount())
                .maxCount(ruleTypes.getMaxCount())
                .selectedCount(packageRuleType.getSelectedCount())
                .build();
    }

    private PackageDocumentDTO convertToDocumentDTO(PackageCheckCategoryAllowedDocument allowedDocument) {
        DocumentType documentType = allowedDocument.getDocumentType();
        return PackageDocumentDTO.builder()
                .id(allowedDocument.getId())
                .documentTypeId(documentType.getDocTypeId())
                .documentName(documentType.getName())
                .documentCode(documentType.getCode())
                .required(allowedDocument.getRequired())
                .priorityOrder(allowedDocument.getPriorityOrder())
                .build();
    }
    
    
    @Transactional
    public void assignPackageToCompany(Long companyId, Long packageId) {

        log.info("Assigning package {} to company {}", packageId, companyId);

        // 🔹 1. Validate package
        BgvPackage bgvPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("BGV Package not found: " + packageId));

        // 🔹 2. Prevent duplicate active package
        boolean alreadyExists = employerPackageRepository
                .existsByCompanyIdAndBgvPackage_PackageIdAndStatus(
                        companyId,
                        packageId,
                        EmployerPackageStatus.ACTIVE
                );

        if (alreadyExists) {
            throw new RuntimeException("Active package already assigned to this company");
        }

        // 🔹 3. Create EmployerPackage (NO pricing logic here)
        EmployerPackage employerPackage = EmployerPackage.builder()
                .companyId(companyId)
                .bgvPackage(bgvPackage)
                .basePrice(bgvPackage.getBasePrice()) // optional (can keep as reference)
                .status(EmployerPackageStatus.ACTIVE)
                .build();

        EmployerPackage savedPackage = employerPackageRepository.save(employerPackage);

        // 🔹 4. Fetch package rules (structure only)
        List<PackageCheckCategoryRuleType> rules =
                packagecheckcategoryRuleTypeRepository
                        .findByBgvPackagePackageId(packageId);

        if (rules == null || rules.isEmpty()) {
            log.warn("No rules found for package {}", packageId);
            return;
        }

        // 🔹 5. Copy rules to EmployerPackageRule (NO pricing)
        List<EmployerPackageRule> employerRules = rules.stream()
                .map(r -> EmployerPackageRule.builder()
                        .employerPackage(savedPackage)
                        .checkCategoryId(r.getCheckCategoryId())
                        .ruleTypeId(r.getRuleTypeId())
                        .includedInBase(true)
                        .requiresCount(r.getRequiresCount())
                        .selectedCount(r.getSelectedCount())

                        // ❌ DO NOT COPY PRICING HERE
                        // Pricing will come from EmployerCheckPricing later

                        .build()
                )
                .collect(Collectors.toList());

        employerPackageRuleRepository.saveAll(employerRules);
        
        
        List<PackageCheckCategoryAllowedRuleType> adminRules = packageCheckCategoryAllowedRuleTypeRepository.findByBgvPackage_PackageId(packageId);
        
        // List<PackageCheckCategoryAllowedRuleType> adminRules
        
        copyAllowedRulesToEmployer(savedPackage, adminRules);

        log.info("Package assigned successfully with {} rules", employerRules.size());
    }
    
    public void unassignPackageFromCompany(Long companyId, Long packageId) {
        log.info("Unassigning package from company: {}, package: {}", companyId, packageId);
        
        // Validate BGV package exists
        BgvPackage bgvPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("BGV Package not found with id: " + packageId));
        
        // Check if employer has an active package of this type
        Optional<EmployerPackage> activePackageOpt = employerPackageRepository.findActiveByCompanyAndPackage(companyId, packageId);
        
        if (activePackageOpt.isPresent()) {
            EmployerPackage employerPackage = activePackageOpt.get();
            
            // Update status to INACTIVE or DELETED instead of saving the same entity
            employerPackage.setStatus(EmployerPackageStatus.INACTIVE);
            employerPackage.setUpdatedAt(LocalDateTime.now());
            
            EmployerPackage savedPackage = employerPackageRepository.save(employerPackage);
            log.info("Successfully unassigned package from company. EmployerPackage ID: {}", savedPackage.getId());
        } else {
            throw new RuntimeException("Employer has no active package with packageId: " + packageId);
        }
    }
    
    
    public void copyAllowedRulesToEmployer(
            EmployerPackage employerPackage,
            List<PackageCheckCategoryAllowedRuleType> adminRules) {

        List<EmployerPackageCheckCategoryAllowedRuleType> list = new ArrayList<>();

        for (PackageCheckCategoryAllowedRuleType admin : adminRules) {

            RuleTypes ruleType = admin.getRuleType();

           // int count = extractCount(ruleType); // ANY_1 → 1

            EmployerPackageCheckCategoryAllowedRuleType e = 
                    EmployerPackageCheckCategoryAllowedRuleType.builder()
                            .employerPackage(employerPackage)
                            .checkCategoryId(admin.getCheckCategory().getCategoryId())
                            .ruleTypeId(ruleType.getRuleTypeId())
                            .required(admin.getRequired())
                            .includedInBase(admin.getRequired()) // or business logic
                           // .requiresCount(count > 0)
                            .minCount(ruleType.getMinCount())
                            .maxCount(ruleType.getMaxCount())
                           // .defaultSelectedCount(count)
                            .priorityOrder(admin.getPriorityOrder())
                            .build();

            list.add(e);
        }

        employerPackageCheckCategoryAllowedRuleTypeRepository.saveAll(list);
    }
    
}