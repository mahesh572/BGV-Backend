package com.org.bgv.service;

import com.org.bgv.common.VerificationCaseDocumentResponse;
import com.org.bgv.common.VerificationCaseResponse;
import com.org.bgv.common.CandidateCaseStatisticsResponse;
import com.org.bgv.common.CaseDocumentSelection;
import com.org.bgv.common.CategoryCase;
import com.org.bgv.common.CategoryInfo;
import com.org.bgv.common.DocumentTypeInfo;
import com.org.bgv.common.DocumentUploadCaseRequest;
import com.org.bgv.common.EmployerPackageInfo;
import com.org.bgv.common.VerificationCaseRequest;
import com.org.bgv.common.VerificationStatisticsResponse;
import com.org.bgv.common.VerificationUpdateRequest;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.constants.EmployerPackageStatus;
import com.org.bgv.constants.VerificationStatus;
import com.org.bgv.dto.*;
import com.org.bgv.entity.*;
import com.org.bgv.repository.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if (verificationCaseRepository.findByCandidateIdAndEmployerPackageId(
                request.getCandidateId(), request.getEmployerPackageId()).isPresent()) {
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
                .status(CaseStatus.CREATED)
                .build();
        
        VerificationCase savedCase = verificationCaseRepository.save(verificationCase);
        
        // Create verification case checks based on categories with selected documents
        List<VerificationCaseCheck> caseChecks = createVerificationCaseChecks(
                savedCase, request.getCategories());
        
        // Create candidate case documents based on selected documents
        List<VerificationCaseDocument> caseDocuments = createCandidateCaseDocuments(
                savedCase, employerDocuments, selectedDocumentIds, request.getCategories());
        
        savedCase.setCaseChecks(caseChecks);
        savedCase.setCaseDocuments(caseDocuments);
        
        // Update verification case with checks and documents
        verificationCaseRepository.save(savedCase);
        
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
                        .status(CaseCheckStatus.PENDING)
                        .build();
                
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
    
    @Transactional
    public VerificationCaseDocumentResponse uploadDocument(DocumentUploadCaseRequest request) {
    	VerificationCaseDocument caseDocument = verificationCaseDocumentRepository.findById(request.getCaseDocumentId())
                .orElseThrow(() -> new RuntimeException("Case document not found"));
        
        caseDocument.setDocumentUrl(request.getDocumentUrl());
        caseDocument.setVerificationStatus(VerificationStatus.UPLOADED);
        caseDocument.setUploadedAt(java.time.LocalDateTime.now());
        
        VerificationCaseDocument updatedDocument = verificationCaseDocumentRepository.save(caseDocument);
        
        log.info("Document uploaded for case document id: {}", request.getCaseDocumentId());
        return mapToVerificationCaseDocumentResponse(updatedDocument);
    }
    
    @Transactional
    public VerificationCaseDocumentResponse updateVerificationStatus(VerificationUpdateRequest request) {
    	VerificationCaseDocument caseDocument = verificationCaseDocumentRepository.findById(request.getCaseDocumentId())
                .orElseThrow(() -> new RuntimeException("Case document not found"));
        
        caseDocument.setVerificationStatus(request.getStatus());
        caseDocument.setVerificationNotes(request.getVerificationNotes());
        caseDocument.setVerifiedAt(java.time.LocalDateTime.now());
        
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
            List<CategoryCase> categories) {
    	
    	// categories - selected by an employer while raising verification case
        
        List<VerificationCaseDocument> caseDocuments = new ArrayList<>();
        
        // Create a map of document selections by category for quick lookup
        Map<Long, List<CaseDocumentSelection>> categoryDocumentSelections = new HashMap();
        
        if (categories != null) {
            for (CategoryCase category : categories) {
                categoryDocumentSelections.put(category.getCategoryId(), category.getDocuments());
            }
        }
        
        for (EmployerPackageDocument employerDoc : employerDocuments) {
            Long documentId = employerDoc.getDocumentType().getDocTypeId();
            boolean isSelected = selectedDocumentIds.contains(documentId);
            
            // If categories are provided, use them to determine selection
            if (!categoryDocumentSelections.isEmpty()) {
                List<CaseDocumentSelection> categoryDocs = categoryDocumentSelections.get(
                        employerDoc.getCheckCategory().getCategoryId());
                
                if (categoryDocs != null) {
                    isSelected = categoryDocs.stream()
                            .anyMatch(doc -> doc.getDocumentId().equals(documentId) && 
                                    doc.getSelected() != null && doc.getSelected());
                }
            }
            
            // Determine if this is an addon (not included in base package)
            boolean isAddOn = !employerDoc.getIncludedInBase() && isSelected;
            
            // Determine if document is required
          //  boolean required = employerDoc.getRequired() != null ? employerDoc.getRequired() : false;
            
            // Calculate document price
          //  Double documentPrice = calculateDocumentPrice(employerDoc, isAddOn);
            
            // Create case document
            VerificationCaseDocument caseDocument = VerificationCaseDocument.builder()
                    .verificationCase(verificationCase)
                    .checkCategory(employerDoc.getCheckCategory())
                    .documentType(employerDoc.getDocumentType())
                    .isAddOn(isAddOn)
                    .required(true)
                   // .documentPrice(documentPrice)
                    .verificationStatus(VerificationStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            caseDocuments.add(caseDocument);
        }
        
        // Save all case documents
        return verificationCaseDocumentRepository.saveAll(caseDocuments);
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
    
    
    public VerificationStatisticsResponse getVerificationStatistics(Long caseId) {
        List<VerificationCaseDocument> documents = verificationCaseDocumentRepository.findByVerificationCaseCaseId(caseId);
        
        long totalDocuments = documents.size();
        long pendingDocuments = documents.stream()
                .filter(doc -> doc.getVerificationStatus() == VerificationStatus.PENDING)
                .count();
        long uploadedDocuments = documents.stream()
                .filter(doc -> doc.getVerificationStatus() == VerificationStatus.UPLOADED)
                .count();
        long underReviewDocuments = documents.stream()
                .filter(doc -> doc.getVerificationStatus() == VerificationStatus.UNDER_REVIEW)
                .count();
        long verifiedDocuments = documents.stream()
                .filter(doc -> doc.getVerificationStatus() == VerificationStatus.VERIFIED ||
                              doc.getVerificationStatus() == VerificationStatus.COMPLETED)
                .count();
        long rejectedDocuments = documents.stream()
                .filter(doc -> doc.getVerificationStatus() == VerificationStatus.REJECTED)
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
                .underReviewDocuments(underReviewDocuments)
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
                .filter(doc -> doc.getVerificationStatus() == VerificationStatus.VERIFIED ||
                              doc.getVerificationStatus() == VerificationStatus.COMPLETED)
                .count();
        
        if (verifiedDocuments == totalDocuments) {
            candidateCase.setStatus(CaseStatus.COMPLETED);
            verificationCaseRepository.save(candidateCase);
        } else if (verifiedDocuments > 0) {
            candidateCase.setStatus(CaseStatus.UNDER_REVIEW);
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
                .documentUrl(document.getDocumentUrl())
                .uploadedAt(document.getUploadedAt())
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
                companyId, CaseStatus.ASSIGNED);
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
        List<CaseStatus> inProgressStatuses = List.of(CaseStatus.IN_PROGRESS, CaseStatus.UNDER_REVIEW);
        List<VerificationCase> cases = verificationCaseRepository.findByCompanyIdAndStatusIn(companyId, inProgressStatuses);
        return cases.stream()
                .map(this::mapToVerificationCaseResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get candidate cases statistics for a company
    public CandidateCaseStatisticsResponse getCandidateCaseStatistics(Long companyId) {
        List<VerificationCase> allCases = verificationCaseRepository.findByCompanyId(companyId);
        
        long totalCases = allCases.size();
        long assignedCases = allCases.stream().filter(c -> c.getStatus() == CaseStatus.ASSIGNED).count();
        long inProgressCases = allCases.stream().filter(c -> c.getStatus() == CaseStatus.IN_PROGRESS).count();
        long underReviewCases = allCases.stream().filter(c -> c.getStatus() == CaseStatus.UNDER_REVIEW).count();
        long completedCases = allCases.stream().filter(c -> c.getStatus() == CaseStatus.COMPLETED).count();
        long cancelledCases = allCases.stream().filter(c -> c.getStatus() == CaseStatus.CANCELLED).count();
        
        double completionRate = totalCases > 0 ? (double) completedCases / totalCases * 100 : 0;
        
        return CandidateCaseStatisticsResponse.builder()
                .totalCases(totalCases)
                .assignedCases(assignedCases)
                .inProgressCases(inProgressCases)
                .underReviewCases(underReviewCases)
                .completedCases(completedCases)
                .cancelledCases(cancelledCases)
                .completionRate(completionRate)
                .build();
    }
    
}