package com.org.bgv.service;

import com.org.bgv.common.CandidateCaseDocumentResponse;
import com.org.bgv.common.CandidateCaseRequest;
import com.org.bgv.common.CandidateCaseResponse;
import com.org.bgv.common.CandidateCaseStatisticsResponse;
import com.org.bgv.common.CategoryInfo;
import com.org.bgv.common.DocumentTypeInfo;
import com.org.bgv.common.DocumentUploadCaseRequest;
import com.org.bgv.common.EmployerPackageInfo;
import com.org.bgv.common.VerificationStatisticsResponse;
import com.org.bgv.common.VerificationUpdateRequest;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateCaseService {
    
    private final CandidateCaseRepository candidateCaseRepository;
    private final CandidateCaseDocumentRepository candidateCaseDocumentRepository;
    private final EmployerPackageRepository employerPackageRepository;
    private final EmployerPackageDocumentRepository employerPackageDocumentRepository;
    
    @Transactional
    public CandidateCaseResponse createCandidateCase(CandidateCaseRequest request) {
        log.info("Creating candidate case for candidate: {}, employer package: {}", 
                request.getCandidateId(), request.getEmployerPackageId());
        
        // Validate employer package exists and is active
        EmployerPackage employerPackage = employerPackageRepository.findById(request.getEmployerPackageId())
                .orElseThrow(() -> new RuntimeException("Employer package not found"));
        
        if (employerPackage.getStatus() != EmployerPackageStatus.ACTIVE) {
            throw new RuntimeException("Employer package is not active");
        }
        
        // Check if candidate already has a case with this package
        if (candidateCaseRepository.findByCandidateIdAndEmployerPackageId(
                request.getCandidateId(), request.getEmployerPackageId()).isPresent()) {
            throw new RuntimeException("Candidate already has a case with this package");
        }
        
        // Get employer package documents
        List<EmployerPackageDocument> employerDocuments = employerPackageDocumentRepository
                .findByEmployerPackageId(request.getEmployerPackageId());
        
        // Calculate pricing based on selected addons
        PricingResult pricing = calculateCandidatePricing(employerDocuments, request.getSelectedAddonDocumentIds());
        
        // Create candidate case
        CandidateCase candidateCase = CandidateCase.builder()
                .candidateId(request.getCandidateId())
                .companyId(request.getCompanyId())
                .employerPackage(employerPackage)
                .basePrice(pricing.getBasePrice())
                .addonPrice(pricing.getAddonPrice())
                .totalPrice(pricing.getTotalPrice())
                .status(CaseStatus.ASSIGNED)
                .build();
        
        CandidateCase savedCase = candidateCaseRepository.save(candidateCase);
        
        // Create candidate case documents
        List<CandidateCaseDocument> caseDocuments = createCandidateCaseDocuments(
                savedCase, employerDocuments, request.getSelectedAddonDocumentIds());
        
        savedCase.setCaseDocuments(caseDocuments);
        
        log.info("Created candidate case with id: {}", savedCase.getCaseId());
        return mapToCandidateCaseResponse(savedCase);
    }
    
    public CandidateCaseResponse getCandidateCase(Long caseId) {
        CandidateCase candidateCase = candidateCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Candidate case not found with id: " + caseId));
        return mapToCandidateCaseResponse(candidateCase);
    }
    
    public List<CandidateCaseResponse> getCandidateCasesByCandidate(Long candidateId) {
        List<CandidateCase> cases = candidateCaseRepository.findByCandidateId(candidateId);
        return cases.stream()
                .map(this::mapToCandidateCaseResponse)
                .collect(Collectors.toList());
    }
    
    public List<CandidateCaseResponse> getCandidateCasesByCompany(Long companyId) {
        List<CandidateCase> cases = candidateCaseRepository.findByCompanyId(companyId);
        return cases.stream()
                .map(this::mapToCandidateCaseResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CandidateCaseResponse updateCaseStatus(Long caseId, CaseStatus status) {
        CandidateCase candidateCase = candidateCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Candidate case not found with id: " + caseId));
        
        candidateCase.setStatus(status);
        CandidateCase updatedCase = candidateCaseRepository.save(candidateCase);
        
        log.info("Updated candidate case status to: {} for case id: {}", status, caseId);
        return mapToCandidateCaseResponse(updatedCase);
    }
    
    @Transactional
    public CandidateCaseDocumentResponse uploadDocument(DocumentUploadCaseRequest request) {
        CandidateCaseDocument caseDocument = candidateCaseDocumentRepository.findById(request.getCaseDocumentId())
                .orElseThrow(() -> new RuntimeException("Case document not found"));
        
        caseDocument.setDocumentUrl(request.getDocumentUrl());
        caseDocument.setVerificationStatus(VerificationStatus.UPLOADED);
        caseDocument.setUploadedAt(java.time.LocalDateTime.now());
        
        CandidateCaseDocument updatedDocument = candidateCaseDocumentRepository.save(caseDocument);
        
        log.info("Document uploaded for case document id: {}", request.getCaseDocumentId());
        return mapToCandidateCaseDocumentResponse(updatedDocument);
    }
    
    @Transactional
    public CandidateCaseDocumentResponse updateVerificationStatus(VerificationUpdateRequest request) {
        CandidateCaseDocument caseDocument = candidateCaseDocumentRepository.findById(request.getCaseDocumentId())
                .orElseThrow(() -> new RuntimeException("Case document not found"));
        
        caseDocument.setVerificationStatus(request.getStatus());
        caseDocument.setVerificationNotes(request.getVerificationNotes());
        caseDocument.setVerifiedAt(java.time.LocalDateTime.now());
        
        CandidateCaseDocument updatedDocument = candidateCaseDocumentRepository.save(caseDocument);
        
        // Update case status if all documents are verified
        updateOverallCaseStatus(caseDocument.getCandidateCase().getCaseId());
        
        log.info("Updated verification status to: {} for case document id: {}", 
                request.getStatus(), request.getCaseDocumentId());
        return mapToCandidateCaseDocumentResponse(updatedDocument);
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
    
    private List<CandidateCaseDocument> createCandidateCaseDocuments(CandidateCase candidateCase,
                                                                    List<EmployerPackageDocument> employerDocuments,
                                                                    List<Long> selectedAddonDocumentIds) {
        return employerDocuments.stream()
                .map(empDoc -> {
                    boolean isAddOn = !empDoc.getIncludedInBase();
                    boolean isSelected = empDoc.getIncludedInBase() || 
                                       selectedAddonDocumentIds.contains(empDoc.getDocumentType().getDocTypeId());
                    
                    if (!isSelected) {
                        return null; // Skip documents not selected by candidate
                    }
                    
                    return CandidateCaseDocument.builder()
                            .candidateCase(candidateCase)
                            .checkCategory(empDoc.getCheckCategory())
                            .documentType(empDoc.getDocumentType())
                            .isAddOn(isAddOn)
                            .required(empDoc.getIncludedInBase()) // Required if included in base
                            .documentPrice(isAddOn ? empDoc.getAddonPrice() : 0.0)
                            .verificationStatus(VerificationStatus.PENDING)
                            .build();
                })
                .filter(doc -> doc != null)
                .collect(Collectors.toList());
    }
    

    // NEW METHOD: Get case documents by case ID
    public List<CandidateCaseDocumentResponse> getCaseDocuments(Long caseId) {
        List<CandidateCaseDocument> documents = candidateCaseDocumentRepository.findByCandidateCaseCaseId(caseId);
        return documents.stream()
                .map(this::mapToCandidateCaseDocumentResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get case documents by status
    public List<CandidateCaseDocumentResponse> getCaseDocumentsByStatus(Long caseId, VerificationStatus status) {
        List<CandidateCaseDocument> documents = candidateCaseDocumentRepository
                .findByCandidateCaseCaseIdAndVerificationStatus(caseId, status);
        return documents.stream()
                .map(this::mapToCandidateCaseDocumentResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get pending documents by candidate
    public List<CandidateCaseDocumentResponse> getPendingDocumentsByCandidate(Long candidateId) {
        List<CandidateCaseDocument> documents = candidateCaseDocumentRepository
                .findPendingDocumentsByCandidate(candidateId);
        return documents.stream()
                .map(this::mapToCandidateCaseDocumentResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get case document by ID
    public CandidateCaseDocumentResponse getCaseDocument(Long documentId) {
        CandidateCaseDocument document = candidateCaseDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Case document not found with id: " + documentId));
        return mapToCandidateCaseDocumentResponse(document);
    }
    
    // NEW METHOD: Bulk update verification status
    @Transactional
    public List<CandidateCaseDocumentResponse> bulkUpdateVerificationStatus(List<VerificationUpdateRequest> requests) {
        return requests.stream()
                .map(this::updateVerificationStatus)
                .collect(Collectors.toList());
    }
    
    
    public VerificationStatisticsResponse getVerificationStatistics(Long caseId) {
        List<CandidateCaseDocument> documents = candidateCaseDocumentRepository.findByCandidateCaseCaseId(caseId);
        
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
        CandidateCase candidateCase = candidateCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Candidate case not found"));
        
        List<CandidateCaseDocument> documents = candidateCaseDocumentRepository
                .findByCandidateCaseCaseId(caseId);
        
        long totalDocuments = documents.size();
        long verifiedDocuments = documents.stream()
                .filter(doc -> doc.getVerificationStatus() == VerificationStatus.VERIFIED ||
                              doc.getVerificationStatus() == VerificationStatus.COMPLETED)
                .count();
        
        if (verifiedDocuments == totalDocuments) {
            candidateCase.setStatus(CaseStatus.COMPLETED);
            candidateCaseRepository.save(candidateCase);
        } else if (verifiedDocuments > 0) {
            candidateCase.setStatus(CaseStatus.UNDER_REVIEW);
            candidateCaseRepository.save(candidateCase);
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
    
    private CandidateCaseResponse mapToCandidateCaseResponse(CandidateCase candidateCase) {
        return CandidateCaseResponse.builder()
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
                        .map(this::mapToCandidateCaseDocumentResponse)
                        .collect(Collectors.toList()))
                .build();
    }
    
    private CandidateCaseDocumentResponse mapToCandidateCaseDocumentResponse(CandidateCaseDocument document) {
        return CandidateCaseDocumentResponse.builder()
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
    public List<CandidateCaseResponse> getCandidateCasesByCompanyAndStatus(Long companyId, CaseStatus status) {
        List<CandidateCase> cases = candidateCaseRepository.findByCompanyIdAndStatus(companyId, status);
        return cases.stream()
                .map(this::mapToCandidateCaseResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get candidate cases by employer package
    public List<CandidateCaseResponse> getCandidateCasesByEmployerPackage(Long employerPackageId) {
        List<CandidateCase> cases = candidateCaseRepository.findByEmployerPackageId(employerPackageId);
        return cases.stream()
                .map(this::mapToCandidateCaseResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get pending candidate cases for a company (ASSIGNED status)
    public List<CandidateCaseResponse> getPendingCandidateCases(Long companyId) {
        List<CandidateCase> cases = candidateCaseRepository.findByCompanyIdAndStatus(
                companyId, CaseStatus.ASSIGNED);
        return cases.stream()
                .map(this::mapToCandidateCaseResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get completed candidate cases for a company
    public List<CandidateCaseResponse> getCompletedCandidateCases(Long companyId) {
        List<CandidateCase> cases = candidateCaseRepository.findByCompanyIdAndStatus(
                companyId, CaseStatus.COMPLETED);
        return cases.stream()
                .map(this::mapToCandidateCaseResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get in-progress candidate cases for a company
    public List<CandidateCaseResponse> getInProgressCandidateCases(Long companyId) {
        List<CaseStatus> inProgressStatuses = List.of(CaseStatus.IN_PROGRESS, CaseStatus.UNDER_REVIEW);
        List<CandidateCase> cases = candidateCaseRepository.findByCompanyIdAndStatusIn(companyId, inProgressStatuses);
        return cases.stream()
                .map(this::mapToCandidateCaseResponse)
                .collect(Collectors.toList());
    }
    
    // NEW METHOD: Get candidate cases statistics for a company
    public CandidateCaseStatisticsResponse getCandidateCaseStatistics(Long companyId) {
        List<CandidateCase> allCases = candidateCaseRepository.findByCompanyId(companyId);
        
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