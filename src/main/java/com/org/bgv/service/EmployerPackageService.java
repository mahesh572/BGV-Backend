package com.org.bgv.service;

import com.org.bgv.common.CategoryInfo;
import com.org.bgv.common.DocumentTypeInfo;
import com.org.bgv.common.EmployerPackageDocumentRequest;
import com.org.bgv.common.EmployerPackageDocumentResponse;
import com.org.bgv.common.EmployerPackageRequest;
import com.org.bgv.common.EmployerPackageResponse;
import com.org.bgv.common.PackageCategoryDTO;
import com.org.bgv.common.PackageCategoryRequest;
import com.org.bgv.common.PackageDTO;
import com.org.bgv.common.PackageDocumentDTO;
import com.org.bgv.common.PackageDocumentRequest;
import com.org.bgv.common.PackageInfo;
import com.org.bgv.common.PackageRequest;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.constants.EmployerPackageStatus;
import com.org.bgv.constants.SelectionType;
import com.org.bgv.dto.*;
import com.org.bgv.entity.*;
import com.org.bgv.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployerPackageService {
    
    private final EmployerPackageRepository employerPackageRepository;
    private final EmployerPackageDocumentRepository employerPackageDocumentRepository;
    private final BgvPackageRepository bgvPackageRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final CandidateCaseRepository candidateCaseRepository;
    private final PackageService packageService;
    
    @Transactional
    public EmployerPackageResponse createEmployerPackage(EmployerPackageRequest request) {
        log.info("Creating employer package for employer: {}, package: {}", 
                request.getCompanyId(), request.getPackageId());
        
        // Validate BGV package exists
        BgvPackage bgvPackage = bgvPackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new RuntimeException("BGV Package not found with id: " + request.getPackageId()));
        
        // Check if employer already has an active package of this type
        if (employerPackageRepository.existsByCompanyIdAndBgvPackage_PackageIdAndStatus(
                request.getCompanyId(), request.getPackageId(), EmployerPackageStatus.ACTIVE)) {
            throw new RuntimeException("Employer already has an active package of this type");
        }
        
        // Calculate total price
        Double addonPrice = calculateAddonPrice(request.getDocuments());
        Double totalPrice = request.getBasePrice() + addonPrice;
        
        // Create employer package
        EmployerPackage employerPackage = EmployerPackage.builder()
                .companyId(request.getCompanyId())
                .bgvPackage(bgvPackage)
                .basePrice(request.getBasePrice())
                .addonPrice(addonPrice)
                .totalPrice(totalPrice)
                .status(EmployerPackageStatus.ACTIVE)
                .build();
        
        EmployerPackage savedPackage = employerPackageRepository.save(employerPackage);
        
        // Create employer package documents
        List<EmployerPackageDocument> documents = createEmployerPackageDocuments(
                savedPackage, request.getDocuments());
        
        savedPackage.setSelectedDocuments(documents);
        
        log.info("Created employer package with id: {}", savedPackage.getId());
        return mapToEmployerPackageResponse(savedPackage);
    }
    

    
    
    
    
    public PackageDTO getEmployerPackage(Long packageId) {
        // Get base package details
        PackageDTO packageDTO = packageService.getPackageById(packageId);
        
        Long companyId = SecurityUtils.getCurrentUserCompanyId();
        Optional<EmployerPackage> employerPackageOpt = employerPackageRepository
                .findActiveByCompanyAndPackage(companyId, packageId);
        
        if (employerPackageOpt.isPresent()) {
            EmployerPackage employerPackage = employerPackageOpt.get();
            
            // Get all documents selected by employer for this package
            List<EmployerPackageDocument> employerSelectedDocuments = employerPackageDocumentRepository
                    .findByEmployerPackageId(employerPackage.getId());
            
            log.info("employerPackage::::::::employerPackage.getId():::::{}",employerPackage.getId());
            log.info("employerPackage::::::::employerPackage.size():::::{}",employerSelectedDocuments.size());
            
            // Create a map of documentTypeId -> EmployerPackageDocument for quick lookup
            Map<Long, EmployerPackageDocument> employerDocumentMap = employerSelectedDocuments.stream()
                    .filter(epd -> epd.getDocumentType() != null)
                    .collect(Collectors.toMap(
                        epd -> epd.getDocumentType().getDocTypeId(),
                        epd -> epd,
                        (existing, replacement) -> existing // handle duplicates if any
                    ));
            
            // Update PackageDocumentDTOs to mark selected ones as true
            updateDocumentSelections(packageDTO, employerDocumentMap);
            
            // Set additional employer-specific information
          //  packageDTO.setEmployerPackageId(employerPackage.getId());
          //  packageDTO.setCustomized(true);
            packageDTO.setBasePrice(employerPackage.getBasePrice());
        } else {
            // If no employer package exists, mark as not customized
          //  packageDTO.setCustomized(false);
        }
        
        return packageDTO;
    }
    

private void updateDocumentSelections(PackageDTO packageDTO, 
                                     Map<Long, EmployerPackageDocument> employerDocumentMap) {
    if (packageDTO.getCategories() == null) {
        return;
    }
    
    for (PackageCategoryDTO category : packageDTO.getCategories()) {
        if (category.getAllowedDocuments() != null) {
            for (PackageDocumentDTO documentDTO : category.getAllowedDocuments()) {
                // Check if this document is in employer's selection
                EmployerPackageDocument employerDoc = employerDocumentMap.get(documentDTO.getDocumentTypeId());
                
                if (employerDoc != null) {
                    // Determine if selected based on selection type
                	documentDTO.setSelected(Boolean.TRUE);
                    
                    // Optional: You can also set additional fields if needed
                    // documentDTO.setAddonPrice(employerDoc.getAddonPrice());
                    // documentDTO.setIncludedInBase(employerDoc.getIncludedInBase());
                } else {
                    // Not selected by employer
                    documentDTO.setSelected(false);
                }
                
                
            }
        }
    }
}
    
    public List<EmployerPackageResponse> getEmployerPackages(Long employerId) {
        List<EmployerPackage> packages = employerPackageRepository.findByCompanyId(employerId);
        return packages.stream()
                .map(this::mapToEmployerPackageResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public EmployerPackageResponse updateEmployerPackageStatus(Long id, EmployerPackageStatus status) {
        EmployerPackage employerPackage = employerPackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer package not found with id: " + id));
        
        employerPackage.setStatus(status);
        EmployerPackage updatedPackage = employerPackageRepository.save(employerPackage);
        
        log.info("Updated employer package status to: {} for id: {}", status, id);
        return mapToEmployerPackageResponse(updatedPackage);
    }
    
    @Transactional
    public void deleteEmployerPackage(Long id) {
        EmployerPackage employerPackage = employerPackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer package not found with id: " + id));
        
        // Delete associated documents first
        employerPackageDocumentRepository.deleteByEmployerPackageId(id);
        
        // Then delete the package
        employerPackageRepository.delete(employerPackage);
        
        log.info("Deleted employer package with id: {}", id);
    }
    
    private Double calculateAddonPrice(List<EmployerPackageDocumentRequest> documents) {
        return documents.stream()
                .filter(doc -> !doc.getIncludedInBase())
                .mapToDouble(EmployerPackageDocumentRequest::getAddonPrice)
                .sum();
    }
    
    private List<EmployerPackageDocument> createEmployerPackageDocuments(
            EmployerPackage employerPackage, List<EmployerPackageDocumentRequest> documentRequests) {
        
        return documentRequests.stream()
                .map(docRequest -> {
                    CheckCategory category = checkCategoryRepository.findById(docRequest.getCheckCategoryId())
                            .orElseThrow(() -> new RuntimeException("Check category not found"));
                    
                    DocumentType documentType = documentTypeRepository.findById(docRequest.getDocumentTypeId())
                            .orElseThrow(() -> new RuntimeException("Document type not found"));
                    
                    return EmployerPackageDocument.builder()
                            .employerPackage(employerPackage)
                            .checkCategory(category)
                            .documentType(documentType)
                            .addonPrice(docRequest.getAddonPrice())
                            .includedInBase(docRequest.getIncludedInBase())
                            .selectionType(docRequest.getSelectionType())
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private EmployerPackageResponse mapToEmployerPackageResponse(EmployerPackage employerPackage) {
        return EmployerPackageResponse.builder()
                .id(employerPackage.getId())
                .companyId(employerPackage.getCompanyId())
              //  .package_id(mapToPackageInfo(employerPackage.getBgvPackage()))
                .bgvPackage(mapToPackageInfo(employerPackage.getBgvPackage()))
                .basePrice(employerPackage.getBasePrice())
                .addonPrice(employerPackage.getAddonPrice())
                .totalPrice(employerPackage.getTotalPrice())
                .status(employerPackage.getStatus().name())
                .createdAt(employerPackage.getCreatedAt())
                .documents(employerPackage.getSelectedDocuments().stream()
                        .map(this::mapToEmployerPackageDocumentResponse)
                        .collect(Collectors.toList()))
                .build();
    }
    
    private EmployerPackageDocumentResponse mapToEmployerPackageDocumentResponse(EmployerPackageDocument document) {
        return EmployerPackageDocumentResponse.builder()
                .id(document.getId())
                .checkCategory(mapToCategoryInfo(document.getCheckCategory()))
                .documentType(mapToDocumentTypeInfo(document.getDocumentType()))
                .addonPrice(document.getAddonPrice())
                .includedInBase(document.getIncludedInBase())
                .selectionType(document.getSelectionType().name())
                .build();
    }
    
    private PackageInfo mapToPackageInfo(BgvPackage bgvPackage) {
        return PackageInfo.builder()
                .packageId(bgvPackage.getPackageId())
                .name(bgvPackage.getName())
                .code(bgvPackage.getCode())
                .description(bgvPackage.getDescription())
                .build();
    }
    
    private CategoryInfo mapToCategoryInfo(CheckCategory category) {
        return CategoryInfo.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .code(category.getCode())
                .build();
    }
    
    private DocumentTypeInfo mapToDocumentTypeInfo(DocumentType documentType) {
        return DocumentTypeInfo.builder()
                .docTypeId(documentType.getDocTypeId())
                .name(documentType.getName())
                .code(documentType.getCode())
                .price(documentType.getPrice())
                .build();
    }
    
    
    /*
    public EmployerPackageResponse getEmployerPackage(Long id) {
        EmployerPackage employerPackage = employerPackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer package not found with id: " + id));
        return mapToEmployerPackageResponse(employerPackage);
    }
    */
    
    // NEW METHOD: Get employer packages by company
    public List<EmployerPackageResponse> getEmployerPackagesByCompany(Long companyId) {
        List<EmployerPackage> packages = employerPackageRepository.findByCompanyId(companyId);
        return packages.stream()
                .map(this::mapToEmployerPackageResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get specific employer package for a company
    public EmployerPackageResponse getEmployerPackageByCompany(Long id, Long companyId) {
        EmployerPackage employerPackage = employerPackageRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new RuntimeException("Employer package not found with id: " + id + " for company: " + companyId));
        return mapToEmployerPackageResponse(employerPackage);
    }
    
    // NEW METHOD: Activate employer package
    @Transactional
    public EmployerPackageResponse activateEmployerPackage(Long id, Long companyId) {
        EmployerPackage employerPackage = employerPackageRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new RuntimeException("Employer package not found"));
        
        if (employerPackage.getStatus() != EmployerPackageStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT packages can be activated");
        }
        
        // Validate that package has at least one document
        if (employerPackage.getSelectedDocuments().isEmpty()) {
            throw new RuntimeException("Cannot activate package without any documents");
        }
        
        employerPackage.setStatus(EmployerPackageStatus.ACTIVE);
        employerPackage.setValidFrom(LocalDateTime.now());
        // Set default validity of 1 year if not specified
        if (employerPackage.getValidUntil() == null) {
            employerPackage.setValidUntil(LocalDateTime.now().plusYears(1));
        }
        
        EmployerPackage updatedPackage = employerPackageRepository.save(employerPackage);
        log.info("Activated employer package with id: {}", id);
        return mapToEmployerPackageResponse(updatedPackage);
    }
    
    // NEW METHOD: Suspend employer package
    @Transactional
    public EmployerPackageResponse suspendEmployerPackage(Long id, Long companyId) {
        EmployerPackage employerPackage = employerPackageRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new RuntimeException("Employer package not found"));
        
        if (employerPackage.getStatus() != EmployerPackageStatus.ACTIVE) {
            throw new RuntimeException("Only ACTIVE packages can be suspended");
        }
        
        employerPackage.setStatus(EmployerPackageStatus.SUSPENDED);
        EmployerPackage updatedPackage = employerPackageRepository.save(employerPackage);
        
        log.info("Suspended employer package with id: {}", id);
        return mapToEmployerPackageResponse(updatedPackage);
    }
    
    // NEW METHOD: Renew employer package
    @Transactional
    public EmployerPackageResponse renewEmployerPackage(Long id, Long companyId, LocalDateTime newValidUntil) {
        EmployerPackage employerPackage = employerPackageRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new RuntimeException("Employer package not found"));
        
        if (employerPackage.getStatus() == EmployerPackageStatus.DELETED) {
            throw new RuntimeException("Cannot renew DELETED package");
        }
        
        if (newValidUntil.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Valid until date must be in the future");
        }
        
        employerPackage.setValidUntil(newValidUntil);
        employerPackage.setStatus(EmployerPackageStatus.ACTIVE);
        
        EmployerPackage updatedPackage = employerPackageRepository.save(employerPackage);
        log.info("Renewed employer package with id: {} until {}", id, newValidUntil);
        return mapToEmployerPackageResponse(updatedPackage);
    }
    
    // NEW METHOD: Update employer package status
    @Transactional
    public EmployerPackageResponse updateEmployerPackageStatus(Long id, Long companyId, EmployerPackageStatus status) {
        EmployerPackage employerPackage = employerPackageRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new RuntimeException("Employer package not found"));
        
        // Validate status transition
        validateStatusTransition(employerPackage.getStatus(), status);
        
        employerPackage.setStatus(status);
        EmployerPackage updatedPackage = employerPackageRepository.save(employerPackage);
        
        log.info("Updated employer package status to: {} for id: {}", status, id);
        return mapToEmployerPackageResponse(updatedPackage);
    }
    
    // NEW METHOD: Get active employer packages for a company
    public List<EmployerPackageResponse> getActiveEmployerPackages(Long companyId) {
        List<EmployerPackage> packages = employerPackageRepository
                .findByCompanyIdAndStatus(companyId, EmployerPackageStatus.ACTIVE);
        return packages.stream()
                .map(this::mapToEmployerPackageResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Delete employer package
    @Transactional
    public void deleteEmployerPackage(Long id, Long companyId) {
        EmployerPackage employerPackage = employerPackageRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new RuntimeException("Employer package not found"));
        
        // Check if there are any active candidate cases using this package
        Long activeCaseCount = candidateCaseRepository.countByEmployerPackageAndStatusNot(
                employerPackage, CaseStatus.COMPLETED);
        
        if (activeCaseCount > 0) {
            throw new RuntimeException("Cannot delete package with active candidate cases");
        }
        
        // Soft delete - set status to DELETED
      //  employerPackage.setStatus(EmployerPackageStatus.DELETED);
        employerPackage.setStatus(EmployerPackageStatus.INACTIVE);
        employerPackageRepository.save(employerPackage);
        
        log.info("Soft deleted employer package with id: {}", id);
    }
    
    // NEW METHOD: Get employer packages by status for a company
    public List<EmployerPackageResponse> getEmployerPackagesByStatus(Long companyId, EmployerPackageStatus status) {
        List<EmployerPackage> packages = employerPackageRepository
                .findByCompanyIdAndStatus(companyId, status);
        return packages.stream()
                .map(this::mapToEmployerPackageResponse)
                .collect(Collectors.toList());
    }
 // Helper method to validate status transitions
    private void validateStatusTransition(EmployerPackageStatus currentStatus, EmployerPackageStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case DRAFT:
                if (newStatus != EmployerPackageStatus.ACTIVE && newStatus != EmployerPackageStatus.DELETED) {
                    throw new RuntimeException("DRAFT package can only be activated or deleted");
                }
                break;
            case ACTIVE:
                if (newStatus == EmployerPackageStatus.DRAFT) {
                    throw new RuntimeException("ACTIVE package cannot go back to DRAFT");
                }
                break;
            case DELETED:
                throw new RuntimeException("DELETED package cannot change status");
            default:
                // Other transitions are allowed
                break;
        }
    }
    
    public void getCompanyPackageDetails(Long companyId,Long packageId) {
    	
    //	get  the package assigned to company from EmployerPackage
    	
    	// get the actual asssigned chck categories from package_check_category based on packageid
    	// get allowed documents for particular categories check from package_checkcategory_allowed_document
    	// get rules from package_checkcategory_ruletype
    	
    	
    	
    }
    
    public void updateEmployerPackageDocuments(Long packageId, PackageRequest request) {
        log.info("Updating employer package documents with ID: {}", packageId);
        
        Long companyId  = SecurityUtils.getCurrentUserCompanyId();
        
        Optional<EmployerPackage>  employerPackageList = employerPackageRepository.findActiveByCompanyAndPackage(companyId, packageId);
        
        EmployerPackage existingEmployerPackage = employerPackageList.get();
        
        /*
        EmployerPackage existingEmployerPackage = employerPackageRepository.findById(employerPackageId)
                .orElseThrow(() -> new RuntimeException("Employer package not found with id: " + employerPackageId));
        */
        
        // Update employer package fields if needed
        existingEmployerPackage.setBasePrice(request.getBasePrice());
        existingEmployerPackage.setTotalPrice(request.getPrice());
        existingEmployerPackage.setUpdatedAt(LocalDateTime.now());
        
        EmployerPackage updatedEmployerPackage = employerPackageRepository.save(existingEmployerPackage);
        
        // Process categories and documents - update or create new
        processEmployerPackageDocumentsUpdate(updatedEmployerPackage, request.getCategories());
        
        log.info("Employer package documents updated successfully with ID: {}", updatedEmployerPackage.getId());
    }

    private void processEmployerPackageDocumentsUpdate(EmployerPackage employerPackage, List<PackageCategoryRequest> categories) {
        if (categories == null || categories.isEmpty()) {
            log.info("No categories provided for update");
            return;
        }
        
        // Get existing employer package documents
        List<EmployerPackageDocument> existingDocuments = employerPackageDocumentRepository
                .findByEmployerPackageId(employerPackage.getId());
        
        // Create a map for quick lookup of existing documents
        Map<String, EmployerPackageDocument> existingDocumentsMap = new HashMap();
        for (EmployerPackageDocument doc : existingDocuments) {
            String key = generateDocumentKey(doc.getCheckCategory().getCategoryId(), 
                                           doc.getDocumentType().getDocTypeId());
            existingDocumentsMap.put(key, doc);
        }
        
        List<EmployerPackageDocument> documentsToSave = new ArrayList();
        
        for (PackageCategoryRequest category : categories) {
            CheckCategory checkCategory = checkCategoryRepository.findById(category.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Check category not found with id: " + category.getCategoryId()));
            
            // Process allowed documents for this category
            if (category.getAllowedDocuments() != null) {
                for (PackageDocumentRequest documentRequest : category.getAllowedDocuments()) {
                    // Only process selected documents
                    if (Boolean.TRUE.equals(documentRequest.getSelected())) {
                        DocumentType documentType = documentTypeRepository.findById(documentRequest.getDocumentTypeId())
                                .orElseThrow(() -> new RuntimeException("Document type not found with id: " + documentRequest.getDocumentTypeId()));
                        
                        String documentKey = generateDocumentKey(category.getCategoryId(), documentRequest.getDocumentTypeId());
                        
                        EmployerPackageDocument employerPackageDocument;
                        
                        if (existingDocumentsMap.containsKey(documentKey)) {
                            // Update existing document
                            employerPackageDocument = existingDocumentsMap.get(documentKey);
                            employerPackageDocument.setUpdatedAt(LocalDateTime.now());
                        } else {
                            // Create new document
                            employerPackageDocument = EmployerPackageDocument.builder()
                                    .employerPackage(employerPackage)
                                    .checkCategory(checkCategory)
                                    .documentType(documentType)
                                    .addonPrice(0.0) // Set appropriate addon price
                                    .includedInBase(true) // Set based on your business logic
                                    .selectionType(SelectionType.INCLUDED) // Set based on your business logic
                                    .createdAt(LocalDateTime.now())
                                    .build();
                        }
                        
                        documentsToSave.add(employerPackageDocument);
                    }
                }
            }
        }
        
        // Save all documents
        if (!documentsToSave.isEmpty()) {
            employerPackageDocumentRepository.saveAll(documentsToSave);
            log.info("Saved {} employer package documents", documentsToSave.size());
        }
        
        // Remove documents that are no longer selected (optional - based on your requirements)
        removeUnselectedDocuments(existingDocumentsMap, categories);
    }

    private String generateDocumentKey(Long categoryId, Long documentTypeId) {
        return categoryId + "_" + documentTypeId;
    }

    private void removeUnselectedDocuments(Map<String, EmployerPackageDocument> existingDocumentsMap, 
                                         List<PackageCategoryRequest> categories) {
        // Create a set of currently selected document keys
        Set<String> selectedDocumentKeys = new HashSet();
        
        for (PackageCategoryRequest category : categories) {
            if (category.getAllowedDocuments() != null) {
                for (PackageDocumentRequest documentRequest : category.getAllowedDocuments()) {
                    if (Boolean.TRUE.equals(documentRequest.getSelected())) {
                        String key = generateDocumentKey(category.getCategoryId(), documentRequest.getDocumentTypeId());
                        selectedDocumentKeys.add(key);
                    }
                }
            }
        }
        
        // Find documents to remove (existing but not in current selection)
        List<EmployerPackageDocument> documentsToRemove = new ArrayList<>();
        for (Map.Entry<String, EmployerPackageDocument> entry : existingDocumentsMap.entrySet()) {
            if (!selectedDocumentKeys.contains(entry.getKey())) {
                documentsToRemove.add(entry.getValue());
            }
        }
        
        // Remove unselected documents
        if (!documentsToRemove.isEmpty()) {
            employerPackageDocumentRepository.deleteAll(documentsToRemove);
            log.info("Removed {} unselected employer package documents", documentsToRemove.size());
        }
    }
    
    
}