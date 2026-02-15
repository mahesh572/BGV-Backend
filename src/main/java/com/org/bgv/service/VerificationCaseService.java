package com.org.bgv.service;

import com.org.bgv.common.VerificationCaseDocumentResponse;
import com.org.bgv.common.VerificationCaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.org.bgv.candidate.dto.CaseStatisticsDTO;
import com.org.bgv.candidate.dto.SectionNamesDisplayDTO;
import com.org.bgv.candidate.dto.VerificationCaseDTO;
import com.org.bgv.candidate.dto.VerificationCaseFilterDTO;
import com.org.bgv.candidate.dto.VerificationCaseResponseDTO;
import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.entity.CandidateVerification;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.candidate.repository.CandidateVerificationRepository;
import com.org.bgv.common.CandidateCaseStatisticsResponse;
import com.org.bgv.common.CaseDocumentSelection;
import com.org.bgv.common.CategoryCase;
import com.org.bgv.common.CategoryInfo;
import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.common.DocumentTypeInfo;
import com.org.bgv.common.DocumentUploadCaseRequest;
import com.org.bgv.common.EmployerPackageInfo;
import com.org.bgv.common.VerificationCaseRequest;
import com.org.bgv.common.VerificationStatisticsResponse;
import com.org.bgv.common.VerificationUpdateRequest;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.constants.EmployerPackageStatus;
import com.org.bgv.constants.SectionConstants;
import com.org.bgv.constants.VerificationStatus;
import com.org.bgv.dto.*;
import com.org.bgv.entity.*;
import com.org.bgv.notifications.service.NotificationDispatcher;
import com.org.bgv.repository.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCaseService {
    
    private final VerificationCaseRepository verificationCaseRepository;
    private final VerificationCaseDocumentRepository verificationCaseDocumentRepository;
    private final EmployerPackageRepository employerPackageRepository;
    private final EmployerPackageDocumentRepository employerPackageDocumentRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final VerificationCaseCheckRepository verificationCaseCheckRepository;
    private final DocumentRepository documentRepository;
    private final VerificationCaseDocumentLinkRepository verificationCaseDocumentLinkRepository;
    private final CheckCategoryService checkCategoryService;
    private final CandidateVerificationRepository candidateVerificationRepository;
    private final ObjectMapper objectMapper;
    private final DocumentTypeRepository documentTypeRepository;
    private final CompanyRepository companyRepository;
    private final VendorAssignmentService vendorAssignmentService;
    private final ReferenceNumberGenerator referenceNumberGenerator;
    private final NotificationDispatcher notificationDispatcher;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    
    
    @Transactional
    public VerificationCaseResponse createVerificationCase(VerificationCaseRequest request) {
        log.info("Creating candidate case for candidate: {}, employer package: {}", 
                request.getCandidateId(), request.getEmployerPackageId());
        
        // Validate employer package exists and is active
        EmployerPackage employerPackage = employerPackageRepository.findById(request.getEmployerPackageId())
                .orElseThrow(() -> new RuntimeException("Employer package not found"));
        
        if (employerPackage.getStatus() != EmployerPackageStatus.ACTIVE) {
            throw new RuntimeException("Employer package is not active");
        }
        
        // Check if candidate already has a case with this package
        if (verificationCaseRepository.findByCandidateIdAndEmployerPackageIdAndCompanyId(
                request.getCandidateId(), request.getEmployerPackageId(),request.getCompanyId()).isPresent()) {
        	log.info("####################################################################################################################");
            throw new RuntimeException("Candidate already has a case with this package");
        }
        
        // Extract all selected document IDs from categories
        List<Long> selectedDocumentIds = extractSelectedDocumentIds(request.getCategories());
        
        // Get employer package documents
        List<EmployerPackageDocument> employerDocuments = employerPackageDocumentRepository
                .findByEmployerPackageId(request.getEmployerPackageId());
        
        // Calculate pricing
        PricingResult pricing = calculateCandidatePricing(employerDocuments, selectedDocumentIds);
        
       /*
        // Validate total price matches request
        if (request.getTotalPrice() != null && 
            request.getTotalPrice().compareTo(pricing.getTotalPrice()) != 0) {
            log.warn("Price mismatch: requested={}, calculated={}", 
                    request.getTotalPrice(), pricing.getTotalPrice());
            throw new RuntimeException("Total price does not match calculated price");
        }
        */
        
        // Create verification case
        VerificationCase verificationCase = VerificationCase.builder()
                .candidateId(request.getCandidateId())
                .companyId(request.getCompanyId())
                .employerPackage(employerPackage)
                .basePrice(pricing.getBasePrice())
                .addonPrice(pricing.getAddonPrice())
                .totalPrice(pricing.getTotalPrice())
                .status(CaseStatus.INITIATED)
                .build();
        
        String caseRef =
                referenceNumberGenerator.generateCaseNumber();
        verificationCase.setCaseNumber(caseRef);
        
        VerificationCase savedCase = verificationCaseRepository.saveAndFlush(verificationCase);
        
        // Create verification case checks based on categories with selected documents
         createVerificationCaseChecks(
                savedCase, request.getCategories());
        
        List<VerificationCaseCheck> caseChecks =
                verificationCaseCheckRepository.findByVerificationCase_CaseId(savedCase.getCaseId());

        savedCase.setCaseChecks(caseChecks);
       
        
        // Update verification case with checks and documents
       // savedCase = verificationCaseRepository.save(savedCase);
        
        
        // Create candidate case documents based on selected documents
        List<VerificationCaseDocument> caseDocuments = createCandidateCaseDocuments(
                savedCase, employerDocuments, selectedDocumentIds, request.getCategories());
        savedCase.setCaseDocuments(caseDocuments);
       
        savedCase = verificationCaseRepository.save(savedCase);
        // asssign vendor to Category Check
        
        vendorAssignmentService.assignVendorsToCaseChecks(caseChecks);
        
        createCandidateVerification(request.getCandidateId(), verificationCase, caseChecks, caseDocuments);
        
        //request.getCompanyId()
        
        Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(null);
        Candidate candidate = candidateRepository.findByCompanyIdAndCandidateId(request.getCompanyId(), request.getCandidateId()).orElseThrow(null);
        
        notificationDispatcher.dispatchCandidateBgvInvitation(company, candidate, candidate.getUser());
        
        log.info("Created candidate case with id: {} and {} documents, {} checks", 
                savedCase.getCaseId(), caseDocuments.size(), caseChecks.size());
        return mapToVerificationCaseResponse(savedCase);
    }
 
    // Create verification case checks based on categories with selected documents
    private List<VerificationCaseCheck> createVerificationCaseChecks(
            VerificationCase verificationCase, 
            List<CategoryCase> categories) {
        
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }
        
        List<VerificationCaseCheck> caseChecks = new ArrayList();
        
        
        for (CategoryCase categoryData : categories) {
            // Get the check category
            CheckCategory checkCategory = checkCategoryRepository.findById(categoryData.getCategoryId())
                    .orElseThrow(() -> new RuntimeException(
                            "Check category not found: " + categoryData.getCategoryId()));
            
            // Count selected documents in this category
            long selectedCount = categoryData.getDocuments().stream()
                    .filter(doc -> doc.getSelected() != null && doc.getSelected())
                    .count();
            
            // Only create check if there are selected documents in this category
            if (selectedCount > 0) {
                VerificationCaseCheck caseCheck = VerificationCaseCheck.builder()
                        .verificationCase(verificationCase)
                        .category(checkCategory)
                        .status(CaseCheckStatus.PENDING_CANDIDATE)
                        .build();
                
                String checkRef = referenceNumberGenerator.generateCheckCaseNumber();
                caseCheck.setCheckRef(checkRef);
                caseChecks.add(caseCheck);
            }
        }
        
        // Save all case checks
        return verificationCaseCheckRepository.saveAll(caseChecks);
    }
 // Helper method to extract selected document IDs from categories
    private List<Long> extractSelectedDocumentIds(List<CategoryCase> categories) {
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }
        
        return categories.stream()
                .flatMap(category -> category.getDocuments().stream())
                .filter(doc -> doc.getSelected() != null && doc.getSelected())
                .map(CaseDocumentSelection::getDocumentId)
                .distinct()
                .collect(Collectors.toList());
    }
    
    
    public VerificationCaseResponse getVerificationCase(Long caseId) {
    	VerificationCase candidateCase = verificationCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Candidate case not found with id: " + caseId));
        return mapToVerificationCaseResponse(candidateCase);
    }
    
    public List<VerificationCaseResponse> getVerificationCasesByCandidate(Long candidateId) {
        List<VerificationCase> cases = verificationCaseRepository.findByCandidateId(candidateId);
        return cases.stream()
                .map(this::mapToVerificationCaseResponse)
                .collect(Collectors.toList());
    }
    
    public List<VerificationCaseResponse> getVerificationCasesByCompany(Long companyId) {
        List<VerificationCase> cases = verificationCaseRepository.findByCompanyId(companyId);
        return cases.stream()
                .map(this::mapToVerificationCaseResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public VerificationCaseResponse updateCaseStatus(Long caseId, CaseStatus status) {
    	VerificationCase candidateCase = verificationCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Candidate case not found with id: " + caseId));
        
        candidateCase.setStatus(status);
        VerificationCase updatedCase = verificationCaseRepository.save(candidateCase);
        
        log.info("Updated candidate case status to: {} for case id: {}", status, caseId);
        return mapToVerificationCaseResponse(updatedCase);
    }
    
    public List<VerificationCaseDocument>  getVerificationCaseCaseIdAndCheckCategoryCategoryId(Long caseId,Long categoryId) {
    	
    	List<VerificationCaseDocument>  verificationCaseDocuments = verificationCaseDocumentRepository.findByVerificationCaseCaseIdAndCheckCategoryCategoryId(caseId,categoryId);
       return verificationCaseDocuments;
    
    }
    
    @Transactional
    public VerificationCaseDocumentResponse uploadDocument(DocumentUploadCaseRequest request) {
    	VerificationCaseDocument caseDocument = verificationCaseDocumentRepository.findById(request.getCaseDocumentId())
                .orElseThrow(() -> new RuntimeException("Case document not found"));
        
       // caseDocument.setDocumentUrl(request.getDocumentUrl());
       // caseDocument.setVerificationStatus(VerificationStatus.UPLOADED);
      //  caseDocument.setUploadedAt(java.time.LocalDateTime.now());
        
        VerificationCaseDocument updatedDocument = verificationCaseDocumentRepository.save(caseDocument);
        
        log.info("Document uploaded for case document id: {}", request.getCaseDocumentId());
        return mapToVerificationCaseDocumentResponse(updatedDocument);
    }
    
    @Transactional
    public VerificationCaseDocumentResponse updateVerificationStatus(VerificationUpdateRequest request) {
    	VerificationCaseDocument caseDocument = verificationCaseDocumentRepository.findById(request.getCaseDocumentId())
                .orElseThrow(() -> new RuntimeException("Case document not found"));
        
        caseDocument.setVerificationStatus(request.getStatus());
      //  caseDocument.setVerificationNotes(request.getVerificationNotes());
       // caseDocument.setVerifiedAt(java.time.LocalDateTime.now());
        
        VerificationCaseDocument updatedDocument = verificationCaseDocumentRepository.save(caseDocument);
        
        // Update case status if all documents are verified
        updateOverallCaseStatus(caseDocument.getVerificationCase().getCaseId());
        
        log.info("Updated verification status to: {} for case document id: {}", 
                request.getStatus(), request.getCaseDocumentId());
        return mapToVerificationCaseDocumentResponse(updatedDocument);
    }
    
    private PricingResult calculateCandidatePricing(List<EmployerPackageDocument> employerDocuments, 
                                                   List<Long> selectedAddonDocumentIds) {
        double basePrice = 0.0;
        double addonPrice = 0.0;
        
        for (EmployerPackageDocument empDoc : employerDocuments) {
            if (empDoc.getIncludedInBase()) {
                // Base documents contribute to base price
                basePrice += empDoc.getEmployerPackage().getBasePrice() / 
                           countIncludedDocuments(employerDocuments);
            } else if (selectedAddonDocumentIds.contains(empDoc.getDocumentType().getDocTypeId())) {
                // Selected addon documents
                addonPrice += empDoc.getAddonPrice();
            }
        }
        
        return new PricingResult(basePrice, addonPrice, basePrice + addonPrice);
    }
    
    private long countIncludedDocuments(List<EmployerPackageDocument> employerDocuments) {
        return employerDocuments.stream()
                .filter(EmployerPackageDocument::getIncludedInBase)
                .count();
    }
    
 // Create candidate case documents with enhanced logic for categories
    private List<VerificationCaseDocument> createCandidateCaseDocuments(
            VerificationCase verificationCase,
            List<EmployerPackageDocument> employerDocuments,
            List<Long> selectedDocumentIds,
            List<CategoryCase> categories
    ) {

        log.info("Creating case documents | caseId={} | candidateId={}",
                verificationCase.getCaseId(),
                verificationCase.getCandidateId());

        List<VerificationCaseDocument> caseDocuments = new ArrayList<>();

        // -----------------------------
        // categoryId -> selected docs
        // -----------------------------
        Map<Long, List<CaseDocumentSelection>> categoryDocumentSelections = new HashMap<>();
        if (categories != null) {
            for (CategoryCase category : categories) {
                categoryDocumentSelections.put(category.getCategoryId(), category.getDocuments());
            }
        }

        // -----------------------------
        // categoryId -> caseCheck
        // -----------------------------
        Map<Long, VerificationCaseCheck> categoryCheckMap =
                verificationCase.getCaseChecks()
                        .stream()
                        .collect(Collectors.toMap(
                                cc -> cc.getCategory().getCategoryId(),
                                cc -> cc
                        ));

        log.info("Resolved {} case checks for caseId={}",
                categoryCheckMap.size(),
                verificationCase.getCaseId());

        // -----------------------------
        // Package document IDs
        // -----------------------------
        Set<Long> packageDocumentIds = employerDocuments.stream()
                .map(ed -> ed.getDocumentType().getDocTypeId())
                .collect(Collectors.toSet());

        /* =====================================================
           PHASE 1: PACKAGE DOCUMENTS
           ===================================================== */
        for (EmployerPackageDocument employerDoc : employerDocuments) {

            Long categoryId = employerDoc.getCheckCategory().getCategoryId();
            Long documentId = employerDoc.getDocumentType().getDocTypeId();

            VerificationCaseCheck caseCheck = categoryCheckMap.get(categoryId);
            if (caseCheck == null) {
                log.warn("Skipping document | no caseCheck | caseId={} | categoryId={}",
                        verificationCase.getCaseId(), categoryId);
                continue;
            }

            boolean isSelected = selectedDocumentIds.contains(documentId);

            if (categoryDocumentSelections.containsKey(categoryId)) {
                List<CaseDocumentSelection> selections =
                        categoryDocumentSelections.get(categoryId);

                if (selections != null) {
                    isSelected = selections.stream()
                            .anyMatch(sel ->
                                    sel.getDocumentId().equals(documentId)
                                            && Boolean.TRUE.equals(sel.getSelected()));
                }
            }

            boolean isAddOn = !employerDoc.getIncludedInBase() && isSelected;

            log.debug(
                    "Creating case document | caseId={} | checkId={} | category={} | docType={} | addOn={} | required={}",
                    verificationCase.getCaseId(),
                    caseCheck.getCaseCheckId(),
                    employerDoc.getCheckCategory().getName(),
                    employerDoc.getDocumentType().getName(),
                    isAddOn,
                    isSelected
            );

            VerificationCaseDocument caseDocument =
                    VerificationCaseDocument.builder()
                            .verificationCase(verificationCase)
                            .verificationCaseCheck(caseCheck) // ✅ FIXED
                            .checkCategory(employerDoc.getCheckCategory())
                            .documentType(employerDoc.getDocumentType())
                            .isAddOn(isAddOn)
                            .required(isSelected)
                            .verificationStatus(DocumentStatus.NONE)
                            .createdAt(LocalDateTime.now())
                            .build();

            caseDocuments.add(caseDocument);
        }

        /* =====================================================
           PHASE 2: TRUE ADD-ON DOCUMENTS
           ===================================================== */
        for (Map.Entry<Long, List<CaseDocumentSelection>> entry : categoryDocumentSelections.entrySet()) {

            Long categoryId = entry.getKey();
            VerificationCaseCheck caseCheck = categoryCheckMap.get(categoryId);

            if (caseCheck == null) {
                log.warn("Skipping add-on docs | no caseCheck | caseId={} | categoryId={}",
                        verificationCase.getCaseId(), categoryId);
                continue;
            }

            for (CaseDocumentSelection selection : entry.getValue()) {

                if (!Boolean.TRUE.equals(selection.getSelected())) {
                    continue;
                }

                Long documentId = selection.getDocumentId();

                if (!packageDocumentIds.contains(documentId)) {

                    CheckCategory category = checkCategoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Category not found"));

                    DocumentType documentType = documentTypeRepository.findById(documentId)
                            .orElseThrow(() -> new RuntimeException("Document type not found"));

                    log.debug(
                            "Creating TRUE add-on | caseId={} | checkId={} | category={} | docType={}",
                            verificationCase.getCaseId(),
                            caseCheck.getCaseCheckId(),
                            category.getName(),
                            documentType.getName()
                    );

                    VerificationCaseDocument addOnDocument =
                            VerificationCaseDocument.builder()
                                    .verificationCase(verificationCase)
                                    .verificationCaseCheck(caseCheck) // ✅ FIXED
                                    .checkCategory(category)
                                    .documentType(documentType)
                                    .isAddOn(true)
                                    .required(true)
                                    .verificationStatus(DocumentStatus.NONE)
                                    .createdAt(LocalDateTime.now())
                                    .build();

                    caseDocuments.add(addOnDocument);
                }
            }
        }

        /* =====================================================
           SAVE & LINK DOCUMENTS
           ===================================================== */
        List<VerificationCaseDocument> savedCaseDocs =
                verificationCaseDocumentRepository.saveAll(caseDocuments);

        log.info("Saved {} case documents | caseId={}",
                savedCaseDocs.size(),
                verificationCase.getCaseId());

        for (VerificationCaseDocument caseDoc : savedCaseDocs) {

            List<Document> documents =
                    documentRepository
                            .findByCategory_CategoryIdAndDocTypeId_DocTypeIdAndCandidate_CandidateId(
                                    caseDoc.getCheckCategory().getCategoryId(),
                                    caseDoc.getDocumentType().getDocTypeId(),
                                    verificationCase.getCandidateId()
                            );

            log.debug("Linking {} documents | caseDocumentId={} | checkId={}",
                    documents.size(),
                    caseDoc.getCaseDocumentId(),
                    caseDoc.getVerificationCaseCheck().getCaseCheckId());

            for (Document document : documents) {
                verificationCaseDocumentLinkRepository.save(
                        VerificationCaseDocumentLink.builder()
                                .caseDocument(caseDoc)
                                .document(document)
                                .status(DocumentStatus.NONE) // ✅ FIXED (NOT NULL)
                                .linkedAt(LocalDateTime.now())
                                .build()
                );
            }
        }

        return savedCaseDocs;
    }

    

    // NEW METHOD: Get case documents by case ID
    public List<VerificationCaseDocumentResponse> getCaseDocuments(Long caseId) {
        List<VerificationCaseDocument> documents = verificationCaseDocumentRepository.findByVerificationCaseCaseId(caseId);
        return documents.stream()
                .map(this::mapToVerificationCaseDocumentResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get case documents by status
    public List<VerificationCaseDocumentResponse> getCaseDocumentsByStatus(Long caseId, VerificationStatus status) {
        List<VerificationCaseDocument> documents = verificationCaseDocumentRepository
                .findByVerificationCaseCaseIdAndVerificationStatus(caseId, status);
        return documents.stream()
                .map(this::mapToVerificationCaseDocumentResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get pending documents by candidate
    public List<VerificationCaseDocumentResponse> getPendingDocumentsByCandidate(Long candidateId) {
        List<VerificationCaseDocument> documents = verificationCaseDocumentRepository
                .findPendingDocumentsByCandidate(candidateId);
        return documents.stream()
                .map(this::mapToVerificationCaseDocumentResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get case document by ID
    public VerificationCaseDocumentResponse getCaseDocument(Long documentId) {
    	VerificationCaseDocument document = verificationCaseDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Case document not found with id: " + documentId));
        return mapToVerificationCaseDocumentResponse(document);
    }
    
    // NEW METHOD: Bulk update verification status
    @Transactional
    public List<VerificationCaseDocumentResponse> bulkUpdateVerificationStatus(List<VerificationUpdateRequest> requests) {
        return requests.stream()
                .map(this::updateVerificationStatus)
                .collect(Collectors.toList());
    }
   
    @Transactional
    public Optional<VerificationCase> getVerificationCaseByCompanyIdAndCandidateIdAndStatus(Long companyId,Long candidateId,CaseStatus status) {
    	return verificationCaseRepository.findFirstByCompanyIdAndCandidateIdAndStatusOrderByCreatedAtDesc(companyId, candidateId, status);
    }
    
    public VerificationCaseCheck getVerificationCaseChackByCategory(Long companyId,Long caseId,String categoryCheck) {
    	log.info("VerificationCaseService:::::::getVerificationCaseChackByCategory::companyId:caseId:::::categoryCheck:::{}{}{}",companyId,caseId,categoryCheck);
    	CheckCategoryResponse checkCategory = checkCategoryService.getCheckCategoryByName(categoryCheck).orElseThrow(()->new RuntimeException());
    	log.info("getVerificationCaseChackByCategory::::::::checkCategory::::::{}",checkCategory);
    	return verificationCaseCheckRepository.findByVerificationCase_CaseIdAndCategory_CategoryId(caseId, checkCategory.getCategoryId()).orElseThrow(()->new RuntimeException());
    }
    
    
    
    public VerificationStatisticsResponse getVerificationStatistics(Long caseId) {
        List<VerificationCaseDocument> documents = verificationCaseDocumentRepository.findByVerificationCaseCaseId(caseId);
        
        long totalDocuments = documents.size();
        long pendingDocuments = documents.stream()
                .filter(doc -> doc.getVerificationStatus() == DocumentStatus.PENDING)
                .count();
        long uploadedDocuments = documents.stream()
                .filter(doc -> doc.getVerificationStatus() == DocumentStatus.UPLOADED)
                .count();
      /*
        long underReviewDocuments = documents.stream()
                .filter(doc -> doc.getVerificationStatus() == DocumentStatus.UNDER_REVIEW)
                .count();
                */
        long verifiedDocuments = documents.stream()
                .filter(doc -> doc.getVerificationStatus() == DocumentStatus.VERIFIED )
                .count();
        long rejectedDocuments = documents.stream()
                .filter(doc -> doc.getVerificationStatus() == DocumentStatus.REJECTED)
                .count();
        
        double completionPercentage = totalDocuments > 0 ? (double) verifiedDocuments / totalDocuments * 100 : 0;
        
        String overallStatus = "INCOMPLETE";
        if (verifiedDocuments == totalDocuments && totalDocuments > 0) {
            overallStatus = "COMPLETED";
        } else if (uploadedDocuments + verifiedDocuments > 0) {
            overallStatus = "IN_PROGRESS";
        }
        
        return VerificationStatisticsResponse.builder()
                .totalDocuments(totalDocuments)
                .pendingDocuments(pendingDocuments)
                .uploadedDocuments(uploadedDocuments)
                .verifiedDocuments(verifiedDocuments)
                .rejectedDocuments(rejectedDocuments)
              //  .underReviewDocuments(underReviewDocuments)
                .completionPercentage(completionPercentage)
                .overallStatus(overallStatus)
                .build();
    }
    private void validateCaseStatusTransition(CaseStatus currentStatus, CaseStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case COMPLETED:
                throw new RuntimeException("Cannot change status of completed case");
            case CANCELLED:
                throw new RuntimeException("Cannot change status of cancelled case");
            default:
                // Other transitions are allowed
                break;
        }
    }
    
    private void updateOverallCaseStatus(Long caseId) {
    	VerificationCase candidateCase = verificationCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Candidate case not found"));
        
        List<VerificationCaseDocument> documents = verificationCaseDocumentRepository
                .findByVerificationCaseCaseId(caseId);
        
        long totalDocuments = documents.size();
        long verifiedDocuments = documents.stream()
                .filter(doc -> doc.getVerificationStatus() == DocumentStatus.VERIFIED )
                .count();
        
        if (verifiedDocuments == totalDocuments) {
            candidateCase.setStatus(CaseStatus.COMPLETED);
            verificationCaseRepository.save(candidateCase);
        } else if (verifiedDocuments > 0) {
            candidateCase.setStatus(CaseStatus.IN_PROGRESS);
            verificationCaseRepository.save(candidateCase);
        }
    }
    
    // Helper classes
    @Data
    @AllArgsConstructor
    private static class PricingResult {
        private Double basePrice;
        private Double addonPrice;
        private Double totalPrice;
    }
    
    private VerificationCaseResponse mapToVerificationCaseResponse(VerificationCase candidateCase) {
        return VerificationCaseResponse.builder()
                .caseId(candidateCase.getCaseId())
                .candidateId(candidateCase.getCandidateId())
                .companyId(candidateCase.getCompanyId())
                .employerPackage(mapToEmployerPackageInfo(candidateCase.getEmployerPackage()))
                .basePrice(candidateCase.getBasePrice())
                .addonPrice(candidateCase.getAddonPrice())
                .totalPrice(candidateCase.getTotalPrice())
                .status(candidateCase.getStatus().name())
                .createdAt(candidateCase.getCreatedAt())
                .documents(candidateCase.getCaseDocuments().stream()
                        .map(this::mapToVerificationCaseDocumentResponse)
                        .collect(Collectors.toList()))
                .build();
    }
    
    private VerificationCaseDocumentResponse mapToVerificationCaseDocumentResponse(VerificationCaseDocument document) {
        return VerificationCaseDocumentResponse.builder()
                .caseDocumentId(document.getCaseDocumentId())
                .checkCategory(mapToCategoryInfo(document.getCheckCategory()))
                .documentType(mapToDocumentTypeInfo(document.getDocumentType()))
                .isAddOn(document.getIsAddOn())
                .required(document.getRequired())
                .documentPrice(document.getDocumentPrice())
                .verificationStatus(document.getVerificationStatus().name())
                .build();
    }
    
    private EmployerPackageInfo mapToEmployerPackageInfo(EmployerPackage employerPackage) {
        return EmployerPackageInfo.builder()
                .id(employerPackage.getId())
                .companyId(employerPackage.getCompanyId())
                .status(employerPackage.getStatus().name())
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
    
 // NEW METHOD: Get candidate cases by company and status
    public List<VerificationCaseResponse> getVerificationCasesByCompanyAndStatus(Long companyId, CaseStatus status) {
        List<VerificationCase> cases = verificationCaseRepository.findByCompanyIdAndStatus(companyId, status);
        return cases.stream()
                .map(this::mapToVerificationCaseResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get candidate cases by employer package
    public List<VerificationCaseResponse> getVerificationCasesByEmployerPackage(Long employerPackageId) {
        List<VerificationCase> cases = verificationCaseRepository.findByEmployerPackageId(employerPackageId);
        return cases.stream()
                .map(this::mapToVerificationCaseResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get pending candidate cases for a company (ASSIGNED status)
    public List<VerificationCaseResponse> getPendingCandidateCases(Long companyId) {
        List<VerificationCase> cases = verificationCaseRepository.findByCompanyIdAndStatus(
                companyId, CaseStatus.IN_PROGRESS);
        return cases.stream()
                .map(this::mapToVerificationCaseResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get completed candidate cases for a company
    public List<VerificationCaseResponse> getCompletedCandidateCases(Long companyId) {
        List<VerificationCase> cases = verificationCaseRepository.findByCompanyIdAndStatus(
                companyId, CaseStatus.COMPLETED);
        return cases.stream()
                .map(this::mapToVerificationCaseResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get in-progress candidate cases for a company
    public List<VerificationCaseResponse> getInProgressCandidateCases(Long companyId) {
        List<CaseStatus> inProgressStatuses = List.of(CaseStatus.IN_PROGRESS);
        List<VerificationCase> cases = verificationCaseRepository.findByCompanyIdAndStatusIn(companyId, inProgressStatuses);
        return cases.stream()
                .map(this::mapToVerificationCaseResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get candidate cases statistics for a company
    public CandidateCaseStatisticsResponse getCandidateCaseStatistics(Long companyId) {
        List<VerificationCase> allCases = verificationCaseRepository.findByCompanyId(companyId);
        
        long totalCases = allCases.size();
      //  long assignedCases = allCases.stream().filter(c -> c.getStatus() == CaseStatus.ASSIGNED).count();
        long inProgressCases = allCases.stream().filter(c -> c.getStatus() == CaseStatus.IN_PROGRESS).count();
      //  long underReviewCases = allCases.stream().filter(c -> c.getStatus() == CaseStatus.UNDER_REVIEW).count();
        long completedCases = allCases.stream().filter(c -> c.getStatus() == CaseStatus.COMPLETED).count();
        long cancelledCases = allCases.stream().filter(c -> c.getStatus() == CaseStatus.CANCELLED).count();
        
        double completionRate = totalCases > 0 ? (double) completedCases / totalCases * 100 : 0;
        
        return CandidateCaseStatisticsResponse.builder()
                .totalCases(totalCases)
             //   .assignedCases(assignedCases)
                .inProgressCases(inProgressCases)
            //    .underReviewCases(underReviewCases)
                .completedCases(completedCases)
                .cancelledCases(cancelledCases)
                .completionRate(completionRate)
                .build();
    }
    
    public CandidateVerification createCandidateVerification(
            Long candidateId,
            VerificationCase verificationCase,
            List<VerificationCaseCheck> caseChecks,
            List<VerificationCaseDocument> caseDocuments) {

        log.info("Creating CandidateVerification for candidateId={}", candidateId);

        if (candidateVerificationRepository.existsByCandidateId(candidateId)) {
            log.info("CandidateVerification already exists for candidateId={}", candidateId);
            return null;
        }

        try {
            ObjectNode requirementsNode = objectMapper.createObjectNode();
            ObjectNode statusNode = objectMapper.createObjectNode();

            // =====================================================
            // 1️⃣ BASIC DETAILS — ALWAYS MANDATORY
            // =====================================================
            
            /*
            addSection(
                    requirementsNode,
                    statusNode,
                    SectionConstants.BASIC_DETAILS,
                    "Personal information verification"
            );
            */
            addSection(
                    requirementsNode,
                    statusNode,
                    SectionConstants.DOCUMENTS,
                    "Documents"
            );
            
            
            

            // =====================================================
            // 2️⃣ DYNAMIC SECTIONS FROM CASE CHECKS
            // =====================================================
            for (VerificationCaseCheck check : caseChecks) {

                CheckCategory category = check.getCategory();
                if (category == null) {
                    continue;
                }

                SectionConstants section =
                        SectionConstants.fromNameOrValue(category.getName());

                /*
                // Avoid duplicate BASIC_DETAILS
                if (section == SectionConstants.BASIC_DETAILS) {
                    continue;
                }
               */
                String description =
                        category.getDescription() != null
                                ? category.getDescription()
                                : section.getValue() + " verification";

                addSection(
                        requirementsNode,
                        statusNode,
                        section,
                        description
                );
            }

            LocalDateTime now = LocalDateTime.now();

            CandidateVerification verification = CandidateVerification.builder()
                    .candidateId(candidateId)
                    .verificationCase(verificationCase)
                    .startDate(now)
                    .dueDate(now.plusDays(30))
                    .status(VerificationStatus.PENDING)
                    .progressPercentage(0)
                    .instructions("Please complete all required sections.")
                    .supportEmail("support@bgv.com")
                    .sectionRequirements(requirementsNode.toString())
                    .sectionStatus(statusNode.toString())
                    .createdBy("system")
                    .updatedBy("system")
                    .build();

            candidateVerificationRepository.save(verification);

            log.info("CandidateVerification created successfully for candidateId={}", candidateId);
            return verification;

        } catch (Exception e) {
            log.error("Failed to create CandidateVerification", e);
            throw new RuntimeException("Failed to create CandidateVerification", e);
        }
    }

    private void addSection(
            ObjectNode requirementsNode,
            ObjectNode statusNode,
            SectionConstants section,
            String description) {

        String key = section.getValue(); // IDENTITY, EDUCATION, etc.

        ObjectNode req = requirementsNode.putObject(key);
        req.put("required", true);
        req.put("order", section.getDisplayOrder());
        req.put("label", section.getValue());
        req.put("description", description);

        ObjectNode status = statusNode.putObject(key);
        status.put("status", "pending");
        status.put("progress", 0);
    }

   
    public List<SectionNamesDisplayDTO> getSectionsForDocumentVerificationCase(
            Long candidateId,
            Long caseId
    ) {
        log.info("getSectionsForDocumentVerificationCase ::: candidateId={}", candidateId);

        VerificationCaseDTO verificationCaseDTO =
                getCandidateVerificationCase(candidateId, caseId);

        return verificationCaseCheckRepository
                .findByVerificationCase_CaseId(verificationCaseDTO.getCaseId())
                .stream()
                .map(check -> SectionNamesDisplayDTO.builder()
                        .sectionName(check.getCategory().getName())
                        .categoryId(check.getCategory().getCategoryId())
                        .checkId(check.getCaseCheckId())
                        .build()
                )
                .toList();
    }

    public VerificationCase getActiveVerificationCase(Long companyId, Long candidateId) {

        return verificationCaseRepository
                .findByCompanyIdAndCandidateIdAndCompletedAtIsNull(companyId, candidateId)
                .orElseThrow(() ->
                        new RuntimeException("Active verification case not found"));
    }
    
    /**
     * Get all verification cases for a candidate with filtering and pagination
     */
    
    public VerificationCaseResponseDTO getCandidateVerificationCases(VerificationCaseFilterDTO filterDTO) {
        log.info("Fetching verification cases for candidateId: {}", filterDTO.getCandidateId());
        
        // Create pageable object for pagination
        Pageable pageable = createPageable(filterDTO);
        
        // Fetch verification cases with filtering
        Page<VerificationCase> casesPage = verificationCaseRepository.findByCandidateIdWithFilters(
            filterDTO.getCandidateId(),
            filterDTO.getStatus(),
            filterDTO.getSearchTerm(),
            pageable
        );
        
        // Convert to DTOs
        List<VerificationCaseDTO> caseDTOs = casesPage.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        // Get statistics
        CaseStatisticsDTO statistics = getCaseStatistics(filterDTO.getCandidateId());
        
        // Build response
        return VerificationCaseResponseDTO.builder()
            .cases(caseDTOs)
            .statistics(statistics)
            .totalCount((int) casesPage.getTotalElements())
            .page(casesPage.getNumber())
            .pageSize(casesPage.getSize())
            .totalPages(casesPage.getTotalPages())
            .build();
    }

    /**
     * Get a specific verification case by ID for a candidate
     */
    public VerificationCaseDTO getCandidateVerificationCase(Long candidateId, Long caseId) {
        log.info("Fetching verification case {} for candidateId: {}", caseId, candidateId);
        
        VerificationCase verificationCase = verificationCaseRepository
            .findByCaseIdAndCandidateId(caseId, candidateId)
            .orElseThrow(() -> new RuntimeException(
                String.format("Verification case %d not found for candidate %d", caseId, candidateId)
            ));
        
        return convertToDTO(verificationCase);
    }
    
    /**
     * Get case statistics for a candidate
     */
    public CaseStatisticsDTO getCaseStatistics(Long candidateId) {
        log.info("Fetching case statistics for candidateId: {}", candidateId);
        
        return CaseStatisticsDTO.builder()
            .totalCases(verificationCaseRepository.countByCandidateId(candidateId))
            .completedCases(verificationCaseRepository.countByCandidateIdAndStatus(
                candidateId, com.org.bgv.constants.CaseStatus.COMPLETED))
            .inProgressCases(verificationCaseRepository.countByCandidateIdAndStatus(
                candidateId, com.org.bgv.constants.CaseStatus.IN_PROGRESS))
          //  .pendingCases(verificationCaseRepository.countByCandidateIdAndStatus(
          //      candidateId, com.org.bgv.constants.CaseStatus.PENDING))
            .rejectedCases(verificationCaseRepository.countByCandidateIdAndStatus(
                candidateId, com.org.bgv.constants.CaseStatus.CANCELLED))
            .build();
    }
    
    /**
     * Convert VerificationCase entity to DTO
     */
    private VerificationCaseDTO convertToDTO(VerificationCase verificationCase) {
        // Get company details
        Company company = companyRepository.findById(verificationCase.getCompanyId())
            .orElse(null);
        
        // Get document count
        Integer documentsCount = verificationCaseDocumentRepository.countByVerificationCase(verificationCase);
        
        // Get check statistics
        List<VerificationCaseCheck> checks = verificationCaseCheckRepository.findByVerificationCase(verificationCase);
        Integer totalChecks = checks.size();
        Integer checksCompleted = (int) checks.stream()
            .filter(check -> check.getStatus() == CaseCheckStatus.COMPLETED)
            .count();
        /*
        // Get package details from EmployerPackage
        String packageName = verificationCase.getEmployerPackage() != null 
            ? verificationCase.getEmployerPackage().getPackageName() 
            : "Standard Verification";
        
        String verificationType = verificationCase.getEmployerPackage() != null 
            ? verificationCase.getEmployerPackage().getVerificationType() 
            : "Background Check";
        */
        return VerificationCaseDTO.builder()
            .caseId(verificationCase.getCaseId())
            .candidateId(verificationCase.getCandidateId())
            .companyId(verificationCase.getCompanyId())
            .companyName(company != null ? company.getCompanyName() : "Unknown Company")
            .companyLogo(company != null ? company.getAdminProfilePicturePath() : null)
            .status(verificationCase.getStatus())
            .createdAt(verificationCase.getCreatedAt())
            .updatedAt(verificationCase.getUpdatedAt())
            .completedAt(verificationCase.getCompletedAt())
           // .verificationType(verificationType)
           // .packageName(packageName)
            .documentsCount(documentsCount)
            .checksCompleted(checksCompleted)
            .totalChecks(totalChecks)
           // .vendorId(verificationCase.getVendorId())
          //  .vendorName(getVendorName(verificationCase.getVendorId()))
            .build();
    }
    
    /**
     * Helper method to get vendor name (you might need to implement this)
     */
    private String getVendorName(Long vendorId) {
        if (vendorId == null) return null;
        // Implement logic to fetch vendor name from vendor service/repository
        return "VerifyPro Inc."; // Placeholder
    }
    
    /**
     * Create Pageable object from filter DTO
     */
    private Pageable createPageable(VerificationCaseFilterDTO filterDTO) {
        // Set default values
        int page = filterDTO.getPage() != null ? filterDTO.getPage() : 0;
        int size = filterDTO.getPageSize() != null ? filterDTO.getPageSize() : 10;
        String sortBy = filterDTO.getSortBy() != null ? filterDTO.getSortBy() : "createdAt";
        String direction = filterDTO.getSortDirection() != null ? filterDTO.getSortDirection() : "desc";
        
        Sort sort = direction.equalsIgnoreCase("asc") 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        
        return PageRequest.of(page, size, sort);
    }
    
    
    
    
   
    
}