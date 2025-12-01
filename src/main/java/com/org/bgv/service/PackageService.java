package com.org.bgv.service;

import com.org.bgv.common.PackageCategoryDTO;
import com.org.bgv.common.PackageCategoryRequest;
import com.org.bgv.common.PackageDTO;
import com.org.bgv.common.PackageDocumentDTO;
import com.org.bgv.common.PackageDocumentRequest;
import com.org.bgv.common.PackageRequest;
import com.org.bgv.common.PackageRuleTypeDTO;
import com.org.bgv.common.RuleTypesDTO;
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
                // Extract rule type IDs from DTOs
                List<Long> ruleTypeIds = ruleTypes.stream()
                    .map(RuleTypesDTO::getRuleTypeId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                
                processRuleTypes(bgvPackage, category.getCategoryId(), ruleTypeIds);
            }
           /*
            if (categoryRequest.getRuleTypeIds() != null && !categoryRequest.getRuleTypeIds().isEmpty()) {
                processRuleTypes(bgvPackage, category.getCategoryId(), categoryRequest.getRuleTypeIds());
            }
            */
            // Process allowed documents
            if (categoryRequest.getAllowedDocuments() != null && !categoryRequest.getAllowedDocuments().isEmpty()) {
                processAllowedDocuments(bgvPackage, category, categoryRequest.getAllowedDocuments());
            }
        }
    }

    private void processRuleTypes1(BgvPackage bgvPackage, Long categoryId, List<Long> ruleTypeIds) {
        for (Long ruleTypeId : ruleTypeIds) {
            // Validate rule type exists
            if (!ruleTypesRepository.existsById(ruleTypeId)) {
                throw new RuntimeException("Rule type not found with id: " + ruleTypeId);
            }
            
            PackageCheckCategoryRuleType packageRuleType = PackageCheckCategoryRuleType.builder()
                    .bgvPackage(bgvPackage)
                    .checkCategoryId(categoryId)
                    .ruleTypeId(ruleTypeId)
                    .build();
            
            packagecheckcategoryRuleTypeRepository.save(packageRuleType);
        }
    }
    private void processRuleTypes(BgvPackage bgvPackage, Long categoryId, List<Long> ruleTypeIds) {
        // Get existing rule types for this package and category
        List<PackageCheckCategoryRuleType> existingRuleTypes = packagecheckcategoryRuleTypeRepository
                .findByBgvPackageAndCheckCategoryId(bgvPackage, categoryId);
        
        Set<Long> existingRuleTypeIds = existingRuleTypes.stream()
                .map(PackageCheckCategoryRuleType::getRuleTypeId)
                .collect(Collectors.toSet());
        
        Set<Long> newRuleTypeIds = new HashSet();
        
        // Delete rule types that are no longer needed
        for (Long existingRuleTypeId : existingRuleTypeIds) {
            if (!newRuleTypeIds.contains(existingRuleTypeId)) {
            	packagecheckcategoryRuleTypeRepository.deleteByBgvPackageAndCheckCategoryIdAndRuleTypeId(
                    bgvPackage, categoryId, existingRuleTypeId
                );
            }
        }
        
        // Add new rule types
        for (Long ruleTypeId : ruleTypeIds) {
            if (!existingRuleTypeIds.contains(ruleTypeId)) {
                // Validate rule type exists
                if (!ruleTypesRepository.existsById(ruleTypeId)) {
                    throw new RuntimeException("Rule type not found with id: " + ruleTypeId);
                }
                
                PackageCheckCategoryRuleType packageRuleType = PackageCheckCategoryRuleType.builder()
                        .bgvPackage(bgvPackage)
                        .checkCategoryId(categoryId)
                        .ruleTypeId(ruleTypeId)
                        .build();
                
                packagecheckcategoryRuleTypeRepository.save(packageRuleType);
            }
        }
    }

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
        
        return PackageCategoryDTO.builder()
                .id(packageCategory.getId())
                .categoryId(category.getCategoryId())
                .categoryName(category.getName())
                .categoryCode(category.getCode())
                .rulesData(packageCategory.getRulesData())
                .ruleTypes(ruleTypeDTOs)
                .allowedDocuments(documentDTOs)
                .build();
    }

    private PackageRuleTypeDTO convertToRuleTypeDTO(PackageCheckCategoryRuleType packageRuleType) {
        // You might want to fetch the actual rule type entity for more details
        return PackageRuleTypeDTO.builder()
                .id(packageRuleType.getId())
                .ruleTypeId(packageRuleType.getRuleTypeId())
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
    
    
    public void assignPackageToCompany(Long companyId,Long packageId) {
    	
    	log.info("Creating employer package for employer: {}, package: {}", 
    			companyId, packageId);
        
        // Validate BGV package exists
        BgvPackage bgvPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("BGV Package not found with id: " + packageId));
        
        // Check if employer already has an active package of this type
        if (employerPackageRepository.existsByCompanyIdAndBgvPackage_PackageIdAndStatus(
        		companyId, packageId, EmployerPackageStatus.ACTIVE)) {
            throw new RuntimeException("Employer already has an active package of this type");
        }
        
		
		
		EmployerPackage employerPackage = EmployerPackage.builder()
                .companyId(companyId)
                .bgvPackage(bgvPackage)
                .basePrice(bgvPackage.getBasePrice())
               // .addonPrice(addonPrice)
               // .totalPrice(totalPrice)
                .status(EmployerPackageStatus.ACTIVE)
                .build();
        
        EmployerPackage savedPackage = employerPackageRepository.save(employerPackage);
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
    
}