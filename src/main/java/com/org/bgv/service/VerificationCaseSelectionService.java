package com.org.bgv.service;

import com.org.bgv.common.VerificationCaseDocumentResponse;
import com.org.bgv.common.VerificationCaseResponse;
import com.org.bgv.common.VerificationCheckDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.org.bgv.candidate.dto.CaseStatisticsDTO;
import com.org.bgv.candidate.dto.SectionNamesDisplayDTO;
import com.org.bgv.candidate.dto.VerificationCaseDTO;
import com.org.bgv.candidate.dto.VerificationCaseFilterDTO;
import com.org.bgv.candidate.dto.VerificationCaseResponseDTO;
import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.entity.CandidatePackageRule;
import com.org.bgv.candidate.entity.CandidatePackageRuleDocument;
import com.org.bgv.candidate.entity.CandidateVerification;
import com.org.bgv.candidate.entity.EducationHistory;
import com.org.bgv.candidate.entity.IdentityProof;
import com.org.bgv.candidate.entity.WorkExperience;
import com.org.bgv.candidate.repository.AddressRepository;
import com.org.bgv.candidate.repository.CandidatePackageRuleDocumentRepository;
import com.org.bgv.candidate.repository.CandidatePackageRuleRepository;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.candidate.repository.CandidateVerificationRepository;
import com.org.bgv.candidate.repository.EducationHistoryRepository;
import com.org.bgv.candidate.repository.IdentityProofRepository;
import com.org.bgv.candidate.repository.WorkExperienceRepository;
import com.org.bgv.common.ActivityTimelineDTO;
import com.org.bgv.common.CandidateCaseStatisticsResponse;
import com.org.bgv.common.CaseDocumentSelection;
import com.org.bgv.common.CategoryCase;
import com.org.bgv.common.CategoryInfo;
import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.common.ColumnMetadata;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.common.DocumentTypeInfo;
import com.org.bgv.common.DocumentUploadCaseRequest;
import com.org.bgv.common.EmployerPackageInfo;
import com.org.bgv.common.FilterMetadata;
import com.org.bgv.common.FilterRequest;
import com.org.bgv.common.Option;
import com.org.bgv.common.PaginationMetadata;
import com.org.bgv.common.PaginationRequest;
import com.org.bgv.common.PaginationResponse;
import com.org.bgv.common.SortingRequest;
import com.org.bgv.common.VPackageDTO;
import com.org.bgv.common.VerificationCaseRequest;
import com.org.bgv.common.VerificationStatisticsResponse;
import com.org.bgv.common.VerificationUpdateRequest;
import com.org.bgv.company.dto.CandidateSummary;
import com.org.bgv.company.dto.CaseSearchRequest;
import com.org.bgv.company.dto.CompanyVerificationCaseDTO;
import com.org.bgv.company.dto.PricingDTO;
import com.org.bgv.company.dto.VerificationCaseDetailsDTO;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.constants.EmployerPackageStatus;
import com.org.bgv.constants.SectionConstants;
import com.org.bgv.constants.VerificationStatus;
import com.org.bgv.dto.*;
import com.org.bgv.entity.*;
import com.org.bgv.enums.RuleGroup;
import com.org.bgv.global.service.RuleExecutionStrategy;
import com.org.bgv.global.service.RuleExecutionStrategyFactory;
import com.org.bgv.notifications.service.NotificationDispatcher;
import com.org.bgv.repository.*;
import com.org.bgv.vendor.repository.VerificationActionEvidenceRepository;
import com.org.bgv.vendor.repository.VerificationActionRepository;
import com.org.bgv.wallet.service.PaymentService;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCaseSelectionService {
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
	private final CandidatePackageRuleRepository candidatePackageRuleRepository;
	private final CandidatePackageRuleDocumentRepository candidatePackageRuleDocumentRepository;
	private final RuleTypesRepository ruleTypesRepository;
	private final PaymentService paymentService;
	private final IconService iconService;
	private final VerificationActionRepository verificationActionRepository;
	private final IdentityProofRepository identityProofRepository;
	private final EducationHistoryRepository educationHistoryRepository;
	private final WorkExperienceRepository workExperienceRepository;
	private final VerificationActionEvidenceRepository verificationActionEvidenceRepository;
	private final AddressRepository addressRepository;
	private final VerificationCaseSelectionRepository verificationCaseSelectionRepository;
	private final RuleExecutionStrategyFactory strategyFactory;

	@Transactional
	public void populateSelections(Long caseId) {
	    
	    VerificationCase verificationCase = verificationCaseRepository.findById(caseId)
	            .orElseThrow(() -> new RuntimeException("Case not found"));

	    List<CandidatePackageRule> rules = candidatePackageRuleRepository.findByVerificationCase_CaseId(caseId);
	    
	    // Separate base package rules and add-on rules
	    List<CandidatePackageRule> baseRules = rules.stream()
	            .filter(rule -> Boolean.TRUE.equals(rule.getIncludedInPackage()))
	            .collect(Collectors.toList());
	    
	    List<CandidatePackageRule> addonRules = rules.stream()
	            .filter(rule -> Boolean.FALSE.equals(rule.getIncludedInPackage()) || rule.getIncludedInPackage() == null)
	            .collect(Collectors.toList());

	    // First process all base rules
	    for (CandidatePackageRule rule : baseRules) {
	        processRule(verificationCase, rule);
	    }
	    
	    // Then process add-on rules (these should enhance/override base selections)
	    for (CandidatePackageRule rule : addonRules) {
	        processRule(verificationCase, rule);
	    }
	}

	private void processRule(VerificationCase verificationCase, CandidatePackageRule rule) {
	    Long categoryId = rule.getCheckCategoryId();
	    CheckCategory checkCategory = checkCategoryRepository.findByCategoryId(categoryId);

	    // =========================
	    // 1. DOCUMENT TYPE RULES
	    // =========================
	    if (checkCategory != null && checkCategory.getName().equalsIgnoreCase(CheckCategoryEnum.IDENTITY.getName())) {
	        processDocumentTypeRules(verificationCase, rule);
	    }

	    // =========================
	    // 2. EDUCATION RULES
	    // =========================
	    else if (checkCategory != null && checkCategory.getName().equalsIgnoreCase(CheckCategoryEnum.EDUCATION.getName())) {
	        processEducationRules(verificationCase, rule);
	    }

	    // =========================
	    // 3. WORK RULES
	    // =========================
	    else if (checkCategory != null && checkCategory.getName().equalsIgnoreCase(CheckCategoryEnum.WORK.getName())) {
	        processWorkRules(verificationCase, rule);
	    }
	    
	    // =========================
	    // 4. ADDRESS RULES
	    // =========================
	    else if (checkCategory != null && checkCategory.getName().equalsIgnoreCase(CheckCategoryEnum.ADDRESS.getName())) {
	        processAddressRules(verificationCase, rule);
	    }
	}

	private void processDocumentTypeRules(VerificationCase verificationCase, CandidatePackageRule rule) {
	    RuleTypes ruleType = ruleTypesRepository.findById(rule.getRuleTypeId()).orElse(null);
	    if (ruleType == null) return;
	    
	    boolean isAddon = rule.getIncludedInPackage() != null && !rule.getIncludedInPackage();
	    String ruleCode = ruleType.getCode();
	    
	    // Case 1: Specific document type rule (e.g., AADHAR, PAN, VOTER, PASSPORT)
	    if (!"ANY_1".equalsIgnoreCase(ruleCode) && !"ANY_2".equalsIgnoreCase(ruleCode)) {
	        Long documentTypeId = ruleType.getDocumentTypeId();
	        
	        if (documentTypeId != null) {
	            // Find all identity proofs of this type for this case
	            List<IdentityProof> identityProofs = identityProofRepository
	                    .findByVerificationCaseCaseIdAndDocTypeId(verificationCase.getCaseId(), documentTypeId);
	            
	            for (IdentityProof identityProof : identityProofs) {
	                processIdentityProofSelection(verificationCase, identityProof, rule, isAddon);
	            }
	        } else {
	            // Fallback: Check CandidatePackageRuleDocument
	            List<CandidatePackageRuleDocument> docRules = candidatePackageRuleDocumentRepository.findByRule(rule);
	            for (CandidatePackageRuleDocument docRule : docRules) {
	                List<IdentityProof> identityProofs = identityProofRepository
	                        .findByVerificationCaseCaseIdAndDocTypeId(verificationCase.getCaseId(), docRule.getDocumentTypeId());
	                
	                for (IdentityProof identityProof : identityProofs) {
	                    processIdentityProofSelection(verificationCase, identityProof, rule, isAddon);
	                }
	            }
	        }
	    } 
	    // Case 2: ANY_1 or ANY_2 rule
	    else {
	        List<CandidatePackageRuleDocument> selectedDocTypes = candidatePackageRuleDocumentRepository.findByRule(rule);
	        
	        int maxBaseSelections = ruleType.getMaxCount() != null ? ruleType.getMaxCount() : 1;
	        int currentSelectionCount = 0;
	        
	        for (CandidatePackageRuleDocument docRule : selectedDocTypes) {
	            Long documentTypeId = docRule.getDocumentTypeId();
	            boolean shouldBeAddon = isAddon || (currentSelectionCount >= maxBaseSelections);
	            
	            // Find identity proofs of this document type
	            List<IdentityProof> identityProofs = identityProofRepository
	                    .findByVerificationCaseCaseIdAndDocTypeId(verificationCase.getCaseId(), documentTypeId);
	            
	            for (IdentityProof identityProof : identityProofs) {
	                processIdentityProofSelection(verificationCase, identityProof, rule, shouldBeAddon);
	            }
	            
	            // Only increment count if we actually processed this document type and it's base
	            if (!shouldBeAddon && !identityProofs.isEmpty()) {
	                currentSelectionCount++;
	            }
	        }
	    }
	}

	private void processIdentityProofSelection(VerificationCase verificationCase, IdentityProof identityProof,
	                                           CandidatePackageRule rule, boolean isAddon) {
	    // Use identityProof.getId() as referenceId (consistent with education/work)
	    VerificationCaseSelection existingSelection = verificationCaseSelectionRepository
	            .findByVerificationCaseAndTypeAndReferenceId(verificationCase, CheckCategoryEnum.IDENTITY, identityProof.getId())
	            .orElse(null);
	    
	    if (existingSelection != null) {
	        // Selection already exists
	        if (!isAddon && existingSelection.getIncludedInBase() == null) {
	            // Base rule updating - ensure it's marked as base
	            existingSelection.setIncludedInBase(true);
	            existingSelection.setUnitPrice(BigDecimal.ZERO);
	            verificationCaseSelectionRepository.save(existingSelection);
	        }
	        // If add-on rule finds existing selection, we DO NOT modify (preserve base pricing)
	        return;
	    }
	    
	    // Create new selection with identityProof ID as reference
	    VerificationCaseSelection selection = createSelection(verificationCase, CheckCategoryEnum.IDENTITY, identityProof.getId());
	    
	    if (isAddon) {
	        selection.setIncludedInBase(false);
	        selection.setUnitPrice(rule.getUnitPrice() != null ? rule.getUnitPrice() : BigDecimal.ZERO);
	    } else {
	        selection.setIncludedInBase(true);
	        selection.setUnitPrice(BigDecimal.ZERO);
	    }
	    verificationCaseSelectionRepository.save(selection);
	    
	    // Link documents to this selection
	    CheckCategory identityCategory = checkCategoryRepository.findByName(CheckCategoryEnum.IDENTITY.getName()).orElseGet(null);
	    
	    List<Document> docs = documentRepository.findByCategory_CategoryIdAndObjectId(
	            identityCategory.getCategoryId(), identityProof.getId());
	    
	    for (Document doc : docs) {
	        if (doc.getSelection() == null) {
	            doc.setSelection(selection);
	            documentRepository.save(doc);
	        }
	    }
	    
	    updateSelectionStatus(selection);
	}



	private void processEducationRules(VerificationCase verificationCase, CandidatePackageRule rule) {
	    RuleTypes ruleType = ruleTypesRepository.findById(rule.getRuleTypeId()).orElse(null);
	    if (ruleType == null) return;

	    CheckCategory checkCategory = checkCategoryRepository.findByCategoryId(rule.getCheckCategoryId());
	    
	    int maxCount = rule.getSelectedCount() != null ? rule.getSelectedCount() : 1;
	    List<EducationHistory> selectedEducation = new ArrayList<>();
	    boolean isAddon = rule.getIncludedInPackage() != null && !rule.getIncludedInPackage();

	    // HIGHEST_ONLY LOGIC
	    if ("HIGHEST_EDUCATION".equalsIgnoreCase(ruleType.getCode())) {
	        selectedEducation.addAll(selectLatestEducation(verificationCase.getCaseId(), 1));
	    }
	    // ALL EDUCATION
	    else if ("ALL".equalsIgnoreCase(ruleType.getCode())) {
	        selectedEducation.addAll(educationHistoryRepository.findByVerificationCaseCaseId(verificationCase.getCaseId()));
	    }
	    // COUNT BASED (e.g. last N records)
	    else if ("LAST_N".equalsIgnoreCase(ruleType.getCode())) {
	        selectedEducation.addAll(selectLatestEducation(verificationCase.getCaseId(), maxCount));
	    }

	    for (EducationHistory edu : selectedEducation) {
	        VerificationCaseSelection existingSelection = verificationCaseSelectionRepository
	                .findByVerificationCaseAndTypeAndReferenceId(verificationCase, CheckCategoryEnum.EDUCATION, edu.getId())
	                .orElse(null);
	        
	        // Only create if selection doesn't exist
	        if (existingSelection == null) {
	            VerificationCaseSelection selection = createSelection(verificationCase, CheckCategoryEnum.EDUCATION, edu.getId());
	            
	            // Set pricing info from the rule
	            if (isAddon) {
	                selection.setIncludedInBase(false);
	                selection.setUnitPrice(rule.getUnitPrice() != null ? rule.getUnitPrice() : BigDecimal.ZERO);
	            } else {
	                selection.setIncludedInBase(true);
	                selection.setUnitPrice(BigDecimal.ZERO);
	            }
	            
	            verificationCaseSelectionRepository.save(selection);
	            
	            edu.setVerificationCase(verificationCase);
	            educationHistoryRepository.save(edu);
	            
	            attachDocumentsToSelection(selection, edu.getId(), checkCategory.getCategoryId());
	            updateSelectionStatus(selection);
	        }
	        // REMOVED the else-if block that was converting base to add-on!
	        // If selection already exists, we DO NOT modify it - it's already handled by base rule
	    }
	    
	    // Special handling: For ALL add-on, ensure ALL education records are selected
	    // but only those NOT already selected by base rules
	    if ("ALL".equalsIgnoreCase(ruleType.getCode()) && isAddon) {
	        List<EducationHistory> allEducation = educationHistoryRepository.findByVerificationCaseCaseId(verificationCase.getCaseId());
	        for (EducationHistory edu : allEducation) {
	            boolean alreadySelected = verificationCaseSelectionRepository
	                    .findByVerificationCaseAndTypeAndReferenceId(verificationCase, CheckCategoryEnum.EDUCATION, edu.getId())
	                    .isPresent();
	            
	            if (!alreadySelected) {
	                // This education record was NOT selected by base rule
	                VerificationCaseSelection selection = createSelection(verificationCase, CheckCategoryEnum.EDUCATION, edu.getId());
	                selection.setIncludedInBase(false);
	                selection.setUnitPrice(rule.getUnitPrice() != null ? rule.getUnitPrice() : BigDecimal.ZERO);
	                verificationCaseSelectionRepository.save(selection);
	                
	                edu.setVerificationCase(verificationCase);
	                educationHistoryRepository.save(edu);
	                
	                attachDocumentsToSelection(selection, edu.getId(), checkCategory.getCategoryId());
	                updateSelectionStatus(selection);
	            }
	        }
	    }
	}

	private void processWorkRules(VerificationCase verificationCase, CandidatePackageRule rule) {
	    RuleTypes ruleType = ruleTypesRepository.findById(rule.getRuleTypeId()).orElse(null);
	    if (ruleType == null) return;

	    CheckCategory checkCategory = checkCategoryRepository.findByCategoryId(rule.getCheckCategoryId());
	    
	    List<WorkExperience> selectedWork = new ArrayList<>();
	    String ruleCode = ruleType.getCode();
	    int maxCount = rule.getSelectedCount() != null ? rule.getSelectedCount() : 2;
	    boolean isAddon = rule.getIncludedInPackage() != null && !rule.getIncludedInPackage();

	    // LAST N COMPANIES
	    if ("LAST_N".equalsIgnoreCase(ruleCode)) {
	        selectedWork.addAll(selectLatestWork(verificationCase.getCaseId(), maxCount));
	    }
	    // ALL COMPANIES
	    else if ("ALL".equalsIgnoreCase(ruleCode)) {
	        selectedWork.addAll(workExperienceRepository.findByVerificationCaseCaseId(verificationCase.getCaseId()));
	    }

	    for (WorkExperience work : selectedWork) {
	        VerificationCaseSelection existingSelection = verificationCaseSelectionRepository
	                .findByVerificationCaseAndTypeAndReferenceId(verificationCase, CheckCategoryEnum.WORK, work.getExperienceId())
	                .orElse(null);
	        
	        if (existingSelection == null) {
	            // Create new selection only if it doesn't exist
	            VerificationCaseSelection selection = createSelection(verificationCase, CheckCategoryEnum.WORK, work.getExperienceId());
	            
	            if (isAddon) {
	                selection.setIncludedInBase(false);
	                selection.setUnitPrice(rule.getUnitPrice() != null ? rule.getUnitPrice() : BigDecimal.ZERO);
	            } else {
	                selection.setIncludedInBase(true);
	                selection.setUnitPrice(BigDecimal.ZERO);
	            }
	            
	            verificationCaseSelectionRepository.save(selection);
	            
	            work.setVerificationCase(verificationCase);
	            workExperienceRepository.save(work);
	            
	            attachDocumentsToSelection(selection, work.getExperienceId(), checkCategory.getCategoryId());
	            updateSelectionStatus(selection);
	        }
	        // REMOVED the else-if block that was converting base to add-on!
	        // If selection already exists, we DO NOT modify it - it's already handled by base rule
	    }
	    
	    // Special handling: For ALL add-on, ensure ALL experiences are selected
	    // but only those NOT already selected by base rules
	    if ("ALL".equalsIgnoreCase(ruleCode) && isAddon) {
	        List<WorkExperience> allWork = workExperienceRepository.findByVerificationCaseCaseId(verificationCase.getCaseId());
	        for (WorkExperience work : allWork) {
	            boolean alreadySelected = verificationCaseSelectionRepository
	                    .findByVerificationCaseAndTypeAndReferenceId(verificationCase, CheckCategoryEnum.WORK, work.getExperienceId())
	                    .isPresent();
	            
	            if (!alreadySelected) {
	                // This work experience was NOT selected by base rule (e.g., candidate has 3+ experiences
	                // but base rule only selected last 2)
	                VerificationCaseSelection selection = createSelection(verificationCase, CheckCategoryEnum.WORK, work.getExperienceId());
	                selection.setIncludedInBase(false);
	                selection.setUnitPrice(rule.getUnitPrice() != null ? rule.getUnitPrice() : BigDecimal.ZERO);
	                verificationCaseSelectionRepository.save(selection);
	                
	                work.setVerificationCase(verificationCase);
	                workExperienceRepository.save(work);
	                
	                attachDocumentsToSelection(selection, work.getExperienceId(), checkCategory.getCategoryId());
	                updateSelectionStatus(selection);
	            }
	        }
	    }
	}

	private void processAddressRules(VerificationCase verificationCase, CandidatePackageRule rule) {
	    RuleTypes ruleType = ruleTypesRepository.findById(rule.getRuleTypeId()).orElse(null);
	    if (ruleType == null) return;

	    CheckCategory checkCategory = checkCategoryRepository.findByCategoryId(rule.getCheckCategoryId());
	    
	    List<Address> selectedAddresses = new ArrayList<>();
	    String ruleCode = ruleType.getCode();
	    int value = rule.getSelectedCount() != null ? rule.getSelectedCount() : 1;
	    boolean isAddon = rule.getIncludedInPackage() != null && !rule.getIncludedInPackage();

	    // LAST N ADDRESSES
	    if ("LAST_N".equalsIgnoreCase(ruleCode)) {
	        selectedAddresses.addAll(selectLatestAddresses(verificationCase.getCaseId(), value));
	    }
	    // ADDRESS DURATION (X YEARS)
	    else if ("ADDRESS_DURATION".equalsIgnoreCase(ruleCode)) {
	        selectedAddresses.addAll(selectAddressByDuration(verificationCase.getCaseId(), value));
	    }

	    for (Address addr : selectedAddresses) {
	        VerificationCaseSelection existingSelection = verificationCaseSelectionRepository
	                .findByVerificationCaseAndTypeAndReferenceId(verificationCase, CheckCategoryEnum.ADDRESS, addr.getId())
	                .orElse(null);
	        
	        if (existingSelection == null) {
	            VerificationCaseSelection selection = createSelection(verificationCase, CheckCategoryEnum.ADDRESS, addr.getId());
	            
	            if (isAddon) {
	                selection.setIncludedInBase(false);
	                selection.setUnitPrice(rule.getUnitPrice() != null ? rule.getUnitPrice() : BigDecimal.ZERO);
	            } else {
	                selection.setIncludedInBase(true);
	                selection.setUnitPrice(BigDecimal.ZERO);
	            }
	            
	            verificationCaseSelectionRepository.save(selection);
	            
	            addr.setVerificationCase(verificationCase);
	            addressRepository.save(addr);
	            
	            attachDocumentsToSelection(selection, addr.getId(), checkCategory.getCategoryId());
	            updateSelectionStatus(selection);
	        }
	        // REMOVED the else-if block that was converting base to add-on!
	    }
	}

	// Helper methods remain the same as before
	private VerificationCaseSelection createSelection(VerificationCase caseObj, CheckCategoryEnum type, Long referenceId) {
	    Optional<VerificationCaseSelection> existing = verificationCaseSelectionRepository
	            .findByVerificationCaseAndTypeAndReferenceId(caseObj, type, referenceId);

	    if (existing.isPresent()) {
	        return existing.get();
	    }

	    VerificationCaseSelection selection = VerificationCaseSelection.builder()
	            .verificationCase(caseObj)
	            .type(type)
	            .referenceId(referenceId)
	            .status(CaseCheckStatus.PENDING.name())
	            .build();

	    return verificationCaseSelectionRepository.save(selection);
	}

	private void attachDocumentsToSelection(VerificationCaseSelection selection, Long objectId, Long checkCategoryId) {
	    List<Document> docs = documentRepository.findByCategory_CategoryIdAndObjectId(checkCategoryId, objectId);

	    for (Document doc : docs) {
	        if (doc.getSelection() == null) {
	            doc.setSelection(selection);
	            documentRepository.save(doc);
	        }
	    }
	}

	private void updateSelectionStatus(VerificationCaseSelection selection) {
	    List<Document> docs = documentRepository.findBySelection(selection);

	    if (docs.isEmpty()) {
	        selection.setStatus(CaseCheckStatus.PENDING.name());
	    } else {
	        boolean allVerified = docs.stream().allMatch(Document::isVerified);

	        if (allVerified) {
	            selection.setStatus(CaseCheckStatus.VERIFIED.name());
	        } else {
	            selection.setStatus(CaseCheckStatus.IN_PROGRESS.name());
	        }
	    }

	    verificationCaseSelectionRepository.save(selection);
	}

	private List<EducationHistory> selectLatestEducation(Long caseId, int limit) {
	    return educationHistoryRepository.findByVerificationCaseCaseId(caseId)
	            .stream()
	            .sorted(Comparator.<EducationHistory, LocalDate>comparing(e -> {
	                if (e.getToDate() != null) {
	                    return e.getToDate();
	                } else if (e.getYearOfPassing() != null) {
	                    return LocalDate.of(e.getYearOfPassing(), 12, 31);
	                } else {
	                    return LocalDate.MIN;
	                }
	            }).reversed())
	            .limit(limit)
	            .toList();
	}

	private List<WorkExperience> selectLatestWork(Long caseId, int limit) {
	    return workExperienceRepository.findByVerificationCaseCaseId(caseId)
	            .stream()
	            .sorted(Comparator.<WorkExperience, LocalDate>comparing(w -> {
	                if (w.getEnd_date() != null) {
	                    return w.getEnd_date();
	                } else {
	                    return LocalDate.now();
	                }
	            }).reversed())
	            .limit(limit)
	            .toList();
	}

	private List<Address> selectLatestAddresses(Long caseId, int limit) {
	    return addressRepository.findByVerificationCaseCaseId(caseId)
	            .stream()
	            .sorted(Comparator.<Address, LocalDate>comparing(a -> {
	                if (a.getToDate() != null) {
	                    return a.getToDate();
	                } else {
	                    return LocalDate.now();
	                }
	            }).reversed())
	            .limit(limit)
	            .toList();
	}

	private List<Address> selectAddressByDuration(Long caseId, int years) {
	    List<Address> addresses = addressRepository
	            .findByVerificationCaseCaseId(caseId)
	            .stream()
	            .sorted(Comparator.<Address, LocalDate>comparing(a -> {
	                if (a.getFromDate() != null) {
	                    return a.getFromDate();
	                } else {
	                    return LocalDate.MIN;
	                }
	            }).reversed())
	            .toList();

	    List<Address> result = new ArrayList<>();
	    LocalDate cutoffDate = LocalDate.now().minusYears(years);

	    for (Address addr : addresses) {
	        result.add(addr);
	        LocalDate toDate = addr.getToDate() != null ? addr.getToDate() : LocalDate.now();

	        if (toDate.isBefore(cutoffDate)) {
	            break;
	        }
	    }

	    return result;
	}
}