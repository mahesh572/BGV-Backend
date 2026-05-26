package com.org.bgv.service;

import com.org.bgv.common.VerificationCaseDocumentResponse;
import com.org.bgv.common.VerificationCaseResponse;
import com.org.bgv.common.VerificationCheckDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.org.bgv.bgvpackage.entity.EmployerCheckPricing;
import com.org.bgv.bgvpackage.repository.EmployerCheckPricingRepository;
import com.org.bgv.candidate.dto.CaseStatisticsDTO;
import com.org.bgv.candidate.dto.SectionNamesDisplayDTO;
import com.org.bgv.candidate.dto.VerificationCaseDTO;
import com.org.bgv.candidate.dto.VerificationCaseFilterDTO;
import com.org.bgv.candidate.dto.VerificationCaseResponseDTO;
import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.entity.CandidatePackageRule;
import com.org.bgv.candidate.entity.CandidatePackageRuleDocument;
import com.org.bgv.candidate.entity.CandidateVerification;
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
import com.org.bgv.common.SelectedRuleRequest;
import com.org.bgv.common.SortingRequest;
import com.org.bgv.common.VPackageDTO;
import com.org.bgv.common.VerificationCaseRequest;
import com.org.bgv.common.VerificationStatisticsResponse;
import com.org.bgv.common.VerificationUpdateRequest;
import com.org.bgv.company.dto.CandidateSummary;
import com.org.bgv.company.dto.CaseSearchRequest;
import com.org.bgv.company.dto.CompanyVerificationCaseDTO;
import com.org.bgv.company.dto.PricingDTO;
import com.org.bgv.company.dto.PricingInfo;
import com.org.bgv.company.dto.VerificationCaseDetailsDTO;
import com.org.bgv.company.entity.EmployerDocumentPricing;
import com.org.bgv.company.entity.EmployerPackageRule;
import com.org.bgv.company.entity.EmployerPackageSelectedRule;
import com.org.bgv.company.repository.EmployerDocumentPricingRepository;
import com.org.bgv.company.repository.EmployerPackageRuleRepository;
import com.org.bgv.company.repository.EmployerPackageSelectedRuleRepository;
import com.org.bgv.company.service.PackagePricingService;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.constants.EmployerPackageStatus;
import com.org.bgv.constants.SectionConstants;
import com.org.bgv.constants.VerificationStatus;
import com.org.bgv.dto.*;
import com.org.bgv.entity.*;
import com.org.bgv.enums.RuleGroup;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	private final EmployerCheckPricingRepository employerCheckPricingRepository;
	private final EmployerPackageRuleRepository employerPackageRuleRepository;
	private final EmployerDocumentPricingRepository employerDocumentPricingRepository;
	private final PackagePricingService pricingService;
	private final EmployerPackageSelectedRuleRepository employerPackageSelectedRuleRepository;
	private final VerificationCaseSelectionRepository verificationCaseSelectionRepository;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

	@Transactional
	public VerificationCaseResponse createVerificationCase(VerificationCaseRequest request) {
		log.info("Creating candidate case for candidate: {}, employer package: {}", request.getCandidateId(),
				request.getEmployerPackageId());

		// Validate employer package exists and is active
		EmployerPackage employerPackage = employerPackageRepository.findById(request.getEmployerPackageId())
				.orElseThrow(() -> new RuntimeException("Employer package not found"));

		if (employerPackage.getStatus() != EmployerPackageStatus.ACTIVE) {
			throw new RuntimeException("Employer package is not active");
		}

		// Check if candidate already has a case with this package
		if (verificationCaseRepository.findByCandidateIdAndEmployerPackageIdAndCompanyId(request.getCandidateId(),
				request.getEmployerPackageId(), request.getCompanyId()).isPresent()) {
			log.info(
					"####################################################################################################################");
			throw new RuntimeException("Candidate already has a case with this package");
		}

		
		// Create verification case
		VerificationCase verificationCase = VerificationCase.builder().candidateId(request.getCandidateId())
				.companyId(request.getCompanyId())
				.employerPackage(employerPackage)
				//  .basePrice(pricing.getBasePrice())
				//  .addonPrice(pricing.getAddonPrice())
				.totalPrice(request.getTotalPrice())
				.status(CaseStatus.INITIATED).build();

		String caseRef = referenceNumberGenerator.generateCaseNumber();
		verificationCase.setCaseNumber(caseRef);

		VerificationCase savedCase = verificationCaseRepository.saveAndFlush(verificationCase);

		saveCandidatePackageRules(request, savedCase);

		// Create verification case checks based on categories - 

		createVerificationCaseChecks(savedCase, request.getCategories());

		List<VerificationCaseCheck> caseChecks = verificationCaseCheckRepository
				.findByVerificationCase_CaseId(savedCase.getCaseId());

		savedCase.setCaseChecks(caseChecks);

		
		savedCase = verificationCaseRepository.save(savedCase);

		// Asssigning vendor to Category Check START

		vendorAssignmentService.assignVendorsToCaseChecks(caseChecks);

		// Asssigning vendor to Category Check  END

		// to Track Candidate Upload checks START
		createCandidateVerification(request.getCandidateId(), verificationCase, caseChecks);

		// to Track Candidate Upload checks END

		Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(null);
		Candidate candidate = candidateRepository
				.findByCompanyIdAndCandidateId(request.getCompanyId(), request.getCandidateId()).orElseThrow(null);

		// debit an amount

		// paymentService.makePaymentFromWallet(SecurityUtils.getCurrentUserId(), company.getId(), request.getTotalPrice(),"");

		notificationDispatcher.dispatchCandidateBgvInvitation(company, candidate, candidate.getUser());

		log.info("Created candidate case with id: {} and {} checks", savedCase.getCaseId(), caseChecks.size());
		return mapToVerificationCaseResponse(savedCase);
	}
	
	@Transactional
	public void saveCandidatePackageRules(VerificationCaseRequest request, VerificationCase savedCase) {

	    log.info("🔵 START saveCandidatePackageRules | caseId={} | employerPackageId={} | companyId={}",
	            savedCase.getCaseId(),
	            request.getEmployerPackageId(),
	            request.getCompanyId());

	    EmployerPackage employerPackage = employerPackageRepository.findById(request.getEmployerPackageId())
	            .orElseThrow(() -> new RuntimeException("Package not found"));

	    // 🔹 Snapshot base price
	    savedCase.setBasePrice(employerPackage.getBasePrice());
	    verificationCaseRepository.save(savedCase);

	    List<CandidatePackageRule> rulesToSave = new ArrayList<>();

	    // =========================
	    // 🔹 FETCH CONFIG
	    // =========================
	    List<EmployerPackageRule> packageRules =
	            employerPackageRuleRepository.findByEmployerPackage_Id(employerPackage.getId());

	    Map<Long, List<EmployerPackageRule>> packageRuleByCategory =
	            packageRules.stream()
	                    .collect(Collectors.groupingBy(EmployerPackageRule::getCheckCategoryId));

	    List<EmployerPackageSelectedRule> allSelectedRules =
	            employerPackageSelectedRuleRepository.findByEmployerPackageId(employerPackage.getId());
	    
	    log.info("allSelectedRules::::::::::::::::::::{}",allSelectedRules.size());

	    // 🔥 parent → children mapping
	    Map<Long, List<EmployerPackageSelectedRule>> selectedRuleByPackageRule =
	            allSelectedRules.stream()
	                    .collect(Collectors.groupingBy(r -> r.getPackageRule().getId()));
	    
	    log.info("selectedRuleByPackageRule:::::::::::::::::::::::{}",selectedRuleByPackageRule);

	    // Collect rule types
	    Set<Long> ruleTypeIds = new HashSet<>();
	    packageRules.forEach(r -> ruleTypeIds.add(r.getRuleTypeId()));
	    allSelectedRules.forEach(r -> ruleTypeIds.add(r.getRuleType().getRuleTypeId()));
	    request.getCategories().forEach(c ->
	            c.getSelectedRules().forEach(r -> ruleTypeIds.add(r.getRuleTypeId()))
	    );

	    Map<Long, RuleTypes> ruleTypeMap =
	            ruleTypesRepository.findAllById(ruleTypeIds)
	                    .stream()
	                    .collect(Collectors.toMap(RuleTypes::getRuleTypeId, r -> r));

	    // =========================
	    // 🔹 PROCESS
	    // =========================
	    for (CategoryCase category : request.getCategories()) {

	        Long categoryId = category.getCategoryId();

	        List<EmployerPackageRule> baseRules =
	                packageRuleByCategory.getOrDefault(categoryId, Collections.emptyList());

	        Set<Long> processed = new HashSet<>();

	        // =========================
	        // 🔹 BASE RULES
	        // =========================
	        for (EmployerPackageRule pkgRule : baseRules) {

	            if (!Boolean.TRUE.equals(pkgRule.getIncludedInBase())) continue;

	            RuleTypes ruleType = ruleTypeMap.get(pkgRule.getRuleTypeId());
	            if (ruleType == null) continue;

	            processed.add(pkgRule.getRuleTypeId());

	            int parentCount = safeCount(pkgRule.getSelectedCount());

	            // 🔹 1. PARENT RULE
	            CandidatePackageRule parentRule = CandidatePackageRule.builder()
	                    .employerPackageId(employerPackage)
	                    .companyId(request.getCompanyId())
	                    .candidateId(request.getCandidateId())
	                    .verificationCase(savedCase)
	                    .checkCategoryId(categoryId)
	                    .ruleTypeId(pkgRule.getRuleTypeId())
	                    .selectedCount(parentCount)
	                    .required(true)
	                    .includedInPackage(true)
	                    .addon(false)
	                    .unitPrice(BigDecimal.ZERO)
	                    .totalPrice(BigDecimal.ZERO)
	                    .build();

	            rulesToSave.add(parentRule);

	            log.info("✅ Parent rule saved: {} count={}", pkgRule.getRuleTypeId(), parentCount);

	            // =========================
	            // 🔹 RULE GROUP (ANY_X / LAST_N)
	            // =========================
	            
	            log.info("ruleType.getRuleGroup():::::::::::::::::::::::::{}",ruleType.getRuleGroup());
	            if (ruleType.getRuleGroup() == RuleGroup.RULE) {

	                List<EmployerPackageSelectedRule> selectedRules =
	                        selectedRuleByPackageRule.getOrDefault(pkgRule.getId(), Collections.emptyList());
	                
	                log.info("pkgRule.getId():::::::::::::::{}",pkgRule.getId());
	                
	                log.info("RuleGroup.RULE:::::::::::::::{}",selectedRules.size());

	                for (EmployerPackageSelectedRule selected : selectedRules) {

	                    int count = Boolean.TRUE.equals(ruleType.getRequiresCount())
	                            ? safeCount(selected.getSelectedCount())
	                            : 1;

	                    CandidatePackageRule childRule = CandidatePackageRule.builder()
	                            .employerPackageId(employerPackage)
	                            .companyId(request.getCompanyId())
	                            .candidateId(request.getCandidateId())
	                            .verificationCase(savedCase)
	                            .checkCategoryId(categoryId)

	                            // 🔥 actual doc rule
	                            .ruleTypeId(selected.getRuleType().getRuleTypeId())

	                            .selectedCount(count)
	                            .required(true)
	                            .includedInPackage(true)
	                            .addon(false)

	                            .unitPrice(BigDecimal.ZERO)
	                            .totalPrice(BigDecimal.ZERO)
	                            .build();

	                    rulesToSave.add(childRule);

	                    log.info("   ↳ Child rule saved: {} count={}",
	                            selected.getRuleType().getRuleTypeId(), count);
	                }
	            }

	            // =========================
	            // 🔹 DOCUMENT_SELECTION
	            // =========================
	            else if (ruleType.getRuleGroup() == RuleGroup.DOCUMENT_SELECTION) {

	                Long docTypeId = ruleType.getDocumentTypeId();

	                if (docTypeId != null) {
	                	

	                    CandidatePackageRule docRule = CandidatePackageRule.builder()
	                            .employerPackageId(employerPackage)
	                            .companyId(request.getCompanyId())
	                            .candidateId(request.getCandidateId())
	                            .verificationCase(savedCase)
	                            .checkCategoryId(categoryId)
	                            .ruleTypeId(docTypeId)
	                            .selectedCount(1)
	                            .required(true)
	                            .includedInPackage(true)
	                            .addon(false)
	                            .unitPrice(BigDecimal.ZERO)
	                            .totalPrice(BigDecimal.ZERO)
	                            .build();

	                    rulesToSave.add(docRule);

	                    log.info("   ↳ Document rule saved: {}", docTypeId);
	                }
	            }
	        }

	        // =========================
	        // 🔹 ADDON RULES
	        // =========================
	        for (SelectedRuleRequest req : category.getSelectedRules()) {

	            if (processed.contains(req.getRuleTypeId())) continue;

	            RuleTypes ruleType = ruleTypeMap.get(req.getRuleTypeId());
	            if (ruleType == null) continue;

	            int count = Boolean.TRUE.equals(ruleType.getRequiresCount())
	                    ? safeCount(req.getSelectedCount())
	                    : 1;

	            PricingInfo pricing = pricingService.resolvePricing(
	                    request.getCompanyId(), categoryId, ruleType
	            );

	            BigDecimal unitPrice = pricing != null ? pricing.getUnitPrice() : BigDecimal.ZERO;

	           // BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(count));

	            CandidatePackageRule addonRule = CandidatePackageRule.builder()
	                    .employerPackageId(employerPackage)
	                    .companyId(request.getCompanyId())
	                    .candidateId(request.getCandidateId())
	                    .verificationCase(savedCase)
	                    .checkCategoryId(categoryId)
	                    .ruleTypeId(req.getRuleTypeId())
	                    .selectedCount(count)
	                    .required(false)
	                    .includedInPackage(false)
	                    .addon(true)
	                    .unitPrice(unitPrice)
	                 //   .totalPrice(totalPrice)
	                    .build();

	            rulesToSave.add(addonRule);

	            log.info("➕ Addon rule saved: {} count={} ",
	                    req.getRuleTypeId(), count);
	        }
	    }

	    // =========================
	    // 🔹 SAVE
	    // =========================
	    candidatePackageRuleRepository.saveAll(rulesToSave);

	    log.info("🟢 END saveCandidatePackageRules | totalRulesSaved={}", rulesToSave.size());
	}

	// Create verification case checks based on categories with selected documents
	private List<VerificationCaseCheck> createVerificationCaseChecks(VerificationCase verificationCase,
			List<CategoryCase> categories) {

		if (categories == null || categories.isEmpty()) {
			return List.of();
		}

		List<VerificationCaseCheck> caseChecks = new ArrayList();

		for (CategoryCase categoryData : categories) {
			// Get the check category
			CheckCategory checkCategory = checkCategoryRepository.findById(categoryData.getCategoryId()).orElseThrow(
					() -> new RuntimeException("Check category not found: " + categoryData.getCategoryId()));

			// Count selected documents in this category

			/*
			 * 
			 * long selectedCount = categoryData.getDocuments().stream() .filter(doc ->
			 * doc.getSelected() != null && doc.getSelected()) .count();
			 */
			// Only create check if there are selected documents in this category

			VerificationCaseCheck caseCheck = VerificationCaseCheck.builder().verificationCase(verificationCase)
					.category(checkCategory).status(CaseCheckStatus.PENDING_CANDIDATE).build();

			String checkRef = referenceNumberGenerator.generateCheckCaseNumber();
			caseCheck.setCheckRef(checkRef);
			caseChecks.add(caseCheck);

		}

		// Save all case checks
		return verificationCaseCheckRepository.saveAll(caseChecks);
	}

	// Helper method to extract selected document IDs from categories
	private List<Long> extractSelectedDocumentIds(List<CategoryCase> categories) {
		if (categories == null || categories.isEmpty()) {
			return List.of();
		}

		return categories.stream().flatMap(category -> category.getDocuments().stream())
				.filter(doc -> doc.getSelected() != null && doc.getSelected()).map(CaseDocumentSelection::getDocumentId)
				.distinct().collect(Collectors.toList());
	}

	public VerificationCaseResponse getVerificationCase(Long caseId) {
		VerificationCase candidateCase = verificationCaseRepository.findById(caseId)
				.orElseThrow(() -> new RuntimeException("Candidate case not found with id: " + caseId));
		return mapToVerificationCaseResponse(candidateCase);
	}

	public List<VerificationCaseResponse> getVerificationCasesByCandidate(Long candidateId) {
		List<VerificationCase> cases = verificationCaseRepository.findByCandidateId(candidateId);
		return cases.stream().map(this::mapToVerificationCaseResponse).collect(Collectors.toList());
	}

	public List<VerificationCaseResponse> getVerificationCasesByCompany(Long companyId) {
		List<VerificationCase> cases = verificationCaseRepository.findByCompanyId(companyId);
		return cases.stream().map(this::mapToVerificationCaseResponse).collect(Collectors.toList());
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

	public List<VerificationCaseDocument> getVerificationCaseCaseIdAndCheckCategoryCategoryId(Long caseId,
			Long categoryId) {

		List<VerificationCaseDocument> verificationCaseDocuments = verificationCaseDocumentRepository
				.findByVerificationCaseCaseIdAndCheckCategoryCategoryId(caseId, categoryId);
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

		log.info("Updated verification status to: {} for case document id: {}", request.getStatus(),
				request.getCaseDocumentId());
		return mapToVerificationCaseDocumentResponse(updatedDocument);
	}

	private PricingResult calculateCandidatePricing(List<EmployerPackageDocument> employerDocuments,
			List<Long> selectedAddonDocumentIds) {
		double basePrice = 0.0;
		double addonPrice = 0.0;

		for (EmployerPackageDocument empDoc : employerDocuments) {
			if (empDoc.getIncludedInBase()) {
				// Base documents contribute to base price
				basePrice += empDoc.getEmployerPackage().getBasePrice() / countIncludedDocuments(employerDocuments);
			} else if (selectedAddonDocumentIds.contains(empDoc.getDocumentType().getDocTypeId())) {
				// Selected addon documents
				addonPrice += empDoc.getAddonPrice();
			}
		}

		return new PricingResult(basePrice, addonPrice, basePrice + addonPrice);
	}

	private long countIncludedDocuments(List<EmployerPackageDocument> employerDocuments) {
		return employerDocuments.stream().filter(EmployerPackageDocument::getIncludedInBase).count();
	}

	// Create candidate case documents with enhanced logic for categories when candidate uploads the documents then its calculated...
	private List<VerificationCaseDocument> createCandidateCaseDocuments(VerificationCase verificationCase,
			List<EmployerPackageDocument> employerDocuments, List<Long> selectedDocumentIds,
			List<CategoryCase> categories) {

		log.info("Creating case documents | caseId={} | candidateId={}", verificationCase.getCaseId(),
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
		Map<Long, VerificationCaseCheck> categoryCheckMap = verificationCase.getCaseChecks().stream()
				.collect(Collectors.toMap(cc -> cc.getCategory().getCategoryId(), cc -> cc));

		log.info("Resolved {} case checks for caseId={}", categoryCheckMap.size(), verificationCase.getCaseId());

		// -----------------------------
		// Package document IDs
		// -----------------------------
		Set<Long> packageDocumentIds = employerDocuments.stream().map(ed -> ed.getDocumentType().getDocTypeId())
				.collect(Collectors.toSet());

		/*
		 * ===================================================== PHASE 1: PACKAGE
		 * DOCUMENTS =====================================================
		 */
		for (EmployerPackageDocument employerDoc : employerDocuments) {

			Long categoryId = employerDoc.getCheckCategory().getCategoryId();
			Long documentId = employerDoc.getDocumentType().getDocTypeId();

			VerificationCaseCheck caseCheck = categoryCheckMap.get(categoryId);
			if (caseCheck == null) {
				log.warn("Skipping document | no caseCheck | caseId={} | categoryId={}", verificationCase.getCaseId(),
						categoryId);
				continue;
			}

			boolean isSelected = selectedDocumentIds.contains(documentId);

			if (categoryDocumentSelections.containsKey(categoryId)) {
				List<CaseDocumentSelection> selections = categoryDocumentSelections.get(categoryId);

				if (selections != null) {
					isSelected = selections.stream().anyMatch(
							sel -> sel.getDocumentId().equals(documentId) && Boolean.TRUE.equals(sel.getSelected()));
				}
			}

			boolean isAddOn = !employerDoc.getIncludedInBase() && isSelected;

			log.debug(
					"Creating case document | caseId={} | checkId={} | category={} | docType={} | addOn={} | required={}",
					verificationCase.getCaseId(), caseCheck.getCaseCheckId(), employerDoc.getCheckCategory().getName(),
					employerDoc.getDocumentType().getName(), isAddOn, isSelected);

			VerificationCaseDocument caseDocument = VerificationCaseDocument.builder()
					.verificationCase(verificationCase).verificationCaseCheck(caseCheck) // ✅ FIXED
					.checkCategory(employerDoc.getCheckCategory()).documentType(employerDoc.getDocumentType())
					.isAddOn(isAddOn).required(isSelected).verificationStatus(DocumentStatus.NONE)
					.createdAt(LocalDateTime.now()).build();

			caseDocuments.add(caseDocument);
		}

		/*
		 * ===================================================== PHASE 2: TRUE ADD-ON
		 * DOCUMENTS =====================================================
		 */
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

					log.debug("Creating TRUE add-on | caseId={} | checkId={} | category={} | docType={}",
							verificationCase.getCaseId(), caseCheck.getCaseCheckId(), category.getName(),
							documentType.getName());

					VerificationCaseDocument addOnDocument = VerificationCaseDocument.builder()
							.verificationCase(verificationCase).verificationCaseCheck(caseCheck) // ✅ FIXED
							.checkCategory(category).documentType(documentType).isAddOn(true).required(true)
							.verificationStatus(DocumentStatus.NONE).createdAt(LocalDateTime.now()).build();

					caseDocuments.add(addOnDocument);
				}
			}
		}

		/*
		 * ===================================================== SAVE & LINK DOCUMENTS
		 * =====================================================
		 */
		List<VerificationCaseDocument> savedCaseDocs = verificationCaseDocumentRepository.saveAll(caseDocuments);

		log.info("Saved {} case documents | caseId={}", savedCaseDocs.size(), verificationCase.getCaseId());

		for (VerificationCaseDocument caseDoc : savedCaseDocs) {

			List<Document> documents = documentRepository
					.findByCategory_CategoryIdAndDocTypeId_DocTypeIdAndCandidate_CandidateId(
							caseDoc.getCheckCategory().getCategoryId(), caseDoc.getDocumentType().getDocTypeId(),
							verificationCase.getCandidateId());

			log.debug("Linking {} documents | caseDocumentId={} | checkId={}", documents.size(),
					caseDoc.getCaseDocumentId(), caseDoc.getVerificationCaseCheck().getCaseCheckId());

			for (Document document : documents) {
				verificationCaseDocumentLinkRepository.save(VerificationCaseDocumentLink.builder().caseDocument(caseDoc)
						.document(document).status(DocumentStatus.NONE) // ✅ FIXED (NOT NULL)
						.linkedAt(LocalDateTime.now()).build());
			}
		}

		return savedCaseDocs;
	}

	// NEW METHOD: Get case documents by case ID
	public List<VerificationCaseDocumentResponse> getCaseDocuments(Long caseId) {
		List<VerificationCaseDocument> documents = verificationCaseDocumentRepository
				.findByVerificationCaseCaseId(caseId);
		return documents.stream().map(this::mapToVerificationCaseDocumentResponse).collect(Collectors.toList());
	}

	// NEW METHOD: Get case documents by status
	public List<VerificationCaseDocumentResponse> getCaseDocumentsByStatus(Long caseId, VerificationStatus status) {
		List<VerificationCaseDocument> documents = verificationCaseDocumentRepository
				.findByVerificationCaseCaseIdAndVerificationStatus(caseId, status);
		return documents.stream().map(this::mapToVerificationCaseDocumentResponse).collect(Collectors.toList());
	}

	// NEW METHOD: Get pending documents by candidate
	public List<VerificationCaseDocumentResponse> getPendingDocumentsByCandidate(Long candidateId) {
		List<VerificationCaseDocument> documents = verificationCaseDocumentRepository
				.findPendingDocumentsByCandidate(candidateId);
		return documents.stream().map(this::mapToVerificationCaseDocumentResponse).collect(Collectors.toList());
	}

	// NEW METHOD: Get case document by ID
	public VerificationCaseDocumentResponse getCaseDocument(Long documentId) {
		VerificationCaseDocument document = verificationCaseDocumentRepository.findById(documentId)
				.orElseThrow(() -> new RuntimeException("Case document not found with id: " + documentId));
		return mapToVerificationCaseDocumentResponse(document);
	}

	// NEW METHOD: Bulk update verification status
	@Transactional
	public List<VerificationCaseDocumentResponse> bulkUpdateVerificationStatus(
			List<VerificationUpdateRequest> requests) {
		return requests.stream().map(this::updateVerificationStatus).collect(Collectors.toList());
	}

	@Transactional
	public Optional<VerificationCase> getVerificationCaseByCompanyIdAndCandidateIdAndStatus(Long companyId,
			Long candidateId, CaseStatus status) {
		return verificationCaseRepository.findFirstByCompanyIdAndCandidateIdAndStatusOrderByCreatedAtDesc(companyId,
				candidateId, status);
	}

	public VerificationCaseCheck getVerificationCaseChackByCategory(Long companyId, Long caseId, String categoryCheck) {
		log.info(
				"VerificationCaseService:::::::getVerificationCaseChackByCategory::companyId:caseId:::::categoryCheck:::{}{}{}",
				companyId, caseId, categoryCheck);
		CheckCategoryResponse checkCategory = checkCategoryService.getCheckCategoryByName(categoryCheck)
				.orElseThrow(() -> new RuntimeException());
		log.info("getVerificationCaseChackByCategory::::::::checkCategory::::::{}", checkCategory);
		return verificationCaseCheckRepository
				.findByVerificationCase_CaseIdAndCategory_CategoryId(caseId, checkCategory.getCategoryId())
				.orElseThrow(() -> new RuntimeException());
	}

	public VerificationStatisticsResponse getVerificationStatistics(Long caseId) {
		List<VerificationCaseDocument> documents = verificationCaseDocumentRepository
				.findByVerificationCaseCaseId(caseId);

		long totalDocuments = documents.size();
		long pendingDocuments = documents.stream().filter(doc -> doc.getVerificationStatus() == DocumentStatus.PENDING)
				.count();
		long uploadedDocuments = documents.stream()
				.filter(doc -> doc.getVerificationStatus() == DocumentStatus.UPLOADED).count();
		/*
		 * long underReviewDocuments = documents.stream() .filter(doc ->
		 * doc.getVerificationStatus() == DocumentStatus.UNDER_REVIEW) .count();
		 */
		long verifiedDocuments = documents.stream()
				.filter(doc -> doc.getVerificationStatus() == DocumentStatus.VERIFIED).count();
		long rejectedDocuments = documents.stream()
				.filter(doc -> doc.getVerificationStatus() == DocumentStatus.REJECTED).count();

		double completionPercentage = totalDocuments > 0 ? (double) verifiedDocuments / totalDocuments * 100 : 0;

		String overallStatus = "INCOMPLETE";
		if (verifiedDocuments == totalDocuments && totalDocuments > 0) {
			overallStatus = "COMPLETED";
		} else if (uploadedDocuments + verifiedDocuments > 0) {
			overallStatus = "IN_PROGRESS";
		}

		return VerificationStatisticsResponse.builder().totalDocuments(totalDocuments)
				.pendingDocuments(pendingDocuments).uploadedDocuments(uploadedDocuments)
				.verifiedDocuments(verifiedDocuments).rejectedDocuments(rejectedDocuments)
				//  .underReviewDocuments(underReviewDocuments)
				.completionPercentage(completionPercentage).overallStatus(overallStatus).build();
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
				.filter(doc -> doc.getVerificationStatus() == DocumentStatus.VERIFIED).count();

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
		return VerificationCaseResponse.builder().caseId(candidateCase.getCaseId())
				.candidateId(candidateCase.getCandidateId()).companyId(candidateCase.getCompanyId())
				.employerPackage(mapToEmployerPackageInfo(candidateCase.getEmployerPackage()))
				.basePrice(candidateCase.getBasePrice()).addonPrice(candidateCase.getAddonPrice())
				.totalPrice(candidateCase.getTotalPrice()).status(candidateCase.getStatus().name())
				.createdAt(candidateCase.getCreatedAt()).documents(candidateCase.getCaseDocuments().stream()
						.map(this::mapToVerificationCaseDocumentResponse).collect(Collectors.toList()))
				.build();
	}

	private VerificationCaseDocumentResponse mapToVerificationCaseDocumentResponse(VerificationCaseDocument document) {
		return VerificationCaseDocumentResponse.builder().caseDocumentId(document.getCaseDocumentId())
				.checkCategory(mapToCategoryInfo(document.getCheckCategory()))
				.documentType(mapToDocumentTypeInfo(document.getDocumentType())).isAddOn(document.getIsAddOn())
				.required(document.getRequired()).documentPrice(document.getDocumentPrice())
				.verificationStatus(document.getVerificationStatus().name()).build();
	}

	private EmployerPackageInfo mapToEmployerPackageInfo(EmployerPackage employerPackage) {
		return EmployerPackageInfo.builder().id(employerPackage.getId()).companyId(employerPackage.getCompanyId())
				.status(employerPackage.getStatus().name()).build();
	}

	private CategoryInfo mapToCategoryInfo(CheckCategory category) {
		return CategoryInfo.builder().categoryId(category.getCategoryId()).name(category.getName())
				.code(category.getCode()).build();
	}

	private DocumentTypeInfo mapToDocumentTypeInfo(DocumentType documentType) {
		return DocumentTypeInfo.builder().docTypeId(documentType.getDocTypeId()).name(documentType.getName())
				.code(documentType.getCode()).price(documentType.getPrice()).build();
	}

	// NEW METHOD: Get candidate cases by company and status
	public List<VerificationCaseResponse> getVerificationCasesByCompanyAndStatus(Long companyId, CaseStatus status) {
		List<VerificationCase> cases = verificationCaseRepository.findByCompanyIdAndStatus(companyId, status);
		return cases.stream().map(this::mapToVerificationCaseResponse).collect(Collectors.toList());
	}

	// NEW METHOD: Get candidate cases by employer package
	public List<VerificationCaseResponse> getVerificationCasesByEmployerPackage(Long employerPackageId) {
		List<VerificationCase> cases = verificationCaseRepository.findByEmployerPackageId(employerPackageId);
		return cases.stream().map(this::mapToVerificationCaseResponse).collect(Collectors.toList());
	}

	// NEW METHOD: Get pending candidate cases for a company (ASSIGNED status)
	public List<VerificationCaseResponse> getPendingCandidateCases(Long companyId) {
		List<VerificationCase> cases = verificationCaseRepository.findByCompanyIdAndStatus(companyId,
				CaseStatus.IN_PROGRESS);
		return cases.stream().map(this::mapToVerificationCaseResponse).collect(Collectors.toList());
	}

	// NEW METHOD: Get completed candidate cases for a company
	public List<VerificationCaseResponse> getCompletedCandidateCases(Long companyId) {
		List<VerificationCase> cases = verificationCaseRepository.findByCompanyIdAndStatus(companyId,
				CaseStatus.COMPLETED);
		return cases.stream().map(this::mapToVerificationCaseResponse).collect(Collectors.toList());
	}

	// NEW METHOD: Get in-progress candidate cases for a company
	public List<VerificationCaseResponse> getInProgressCandidateCases(Long companyId) {
		List<CaseStatus> inProgressStatuses = List.of(CaseStatus.IN_PROGRESS);
		List<VerificationCase> cases = verificationCaseRepository.findByCompanyIdAndStatusIn(companyId,
				inProgressStatuses);
		return cases.stream().map(this::mapToVerificationCaseResponse).collect(Collectors.toList());
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

		return CandidateCaseStatisticsResponse.builder().totalCases(totalCases)
				//   .assignedCases(assignedCases)
				.inProgressCases(inProgressCases)
				//    .underReviewCases(underReviewCases)
				.completedCases(completedCases).cancelledCases(cancelledCases).completionRate(completionRate).build();
	}

	public CandidateVerification createCandidateVerification(Long candidateId, VerificationCase verificationCase,
			List<VerificationCaseCheck> caseChecks) {

		log.info("Creating CandidateVerification for candidateId={}", candidateId);

		if (candidateVerificationRepository.existsByCandidateIdAndVerificationCaseCaseId(candidateId,
				verificationCase.getCaseId())) {
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
			 * addSection( requirementsNode, statusNode, SectionConstants.BASIC_DETAILS,
			 * "Personal information verification" );
			 * 
			 * 
			 * addSection( requirementsNode, statusNode, SectionConstants.DOCUMENTS,
			 * "Documents" );
			 */

			// =====================================================
			// 2️⃣ DYNAMIC SECTIONS FROM CASE CHECKS
			// =====================================================
			for (VerificationCaseCheck check : caseChecks) {

				CheckCategory category = check.getCategory();
				if (category == null) {
					continue;
				}

				SectionConstants section = SectionConstants.fromNameOrValue(category.getName());

				/*
				 * // Avoid duplicate BASIC_DETAILS if (section ==
				 * SectionConstants.BASIC_DETAILS) { continue; }
				 */
				String description = category.getDescription() != null ? category.getDescription()
						: section.getValue() + " verification";

				addSection(requirementsNode, statusNode, section, description);
			}

			LocalDateTime now = LocalDateTime.now();

			CandidateVerification verification = CandidateVerification.builder().candidateId(candidateId)
					.verificationCase(verificationCase).startDate(now).dueDate(now.plusDays(30))
					.status(VerificationStatus.PENDING).progressPercentage(0)
					.instructions("Please complete all required sections.").supportEmail("support@bgv.com")
					.sectionRequirements(requirementsNode.toString()).sectionStatus(statusNode.toString())
					.createdBy("system").updatedBy("system").build();

			candidateVerificationRepository.save(verification);

			log.info("CandidateVerification created successfully for candidateId={}", candidateId);
			return verification;

		} catch (Exception e) {
			log.error("Failed to create CandidateVerification", e);
			throw new RuntimeException("Failed to create CandidateVerification", e);
		}
	}

	private void addSection(ObjectNode requirementsNode, ObjectNode statusNode, SectionConstants section,
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

	public List<SectionNamesDisplayDTO> getSectionsForDocumentVerificationCase(Long candidateId, Long caseId) {
		log.info("getSectionsForDocumentVerificationCase ::: candidateId={}", candidateId);

		VerificationCaseDTO verificationCaseDTO = getCandidateVerificationCase(candidateId, caseId);

		return verificationCaseCheckRepository.findByVerificationCase_CaseId(verificationCaseDTO.getCaseId()).stream()
				.map(check -> SectionNamesDisplayDTO.builder().sectionName(check.getCategory().getName())
						.categoryId(check.getCategory().getCategoryId()).checkId(check.getCaseCheckId()).build())
				.toList();
	}

	public VerificationCase getActiveVerificationCase(Long companyId, Long candidateId) {

		return verificationCaseRepository.findByCompanyIdAndCandidateIdAndCompletedAtIsNull(companyId, candidateId)
				.orElseThrow(() -> new RuntimeException("Active verification case not found"));
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
				filterDTO.getCandidateId(), filterDTO.getStatus(), filterDTO.getSearchTerm(), pageable);

		// Convert to DTOs
		List<VerificationCaseDTO> caseDTOs = casesPage.getContent().stream().map(this::convertToDTO)
				.collect(Collectors.toList());

		// Get statistics
		CaseStatisticsDTO statistics = getCaseStatistics(filterDTO.getCandidateId());

		// Build response
		return VerificationCaseResponseDTO.builder().cases(caseDTOs).statistics(statistics)
				.totalCount((int) casesPage.getTotalElements()).page(casesPage.getNumber())
				.pageSize(casesPage.getSize()).totalPages(casesPage.getTotalPages()).build();
	}

	/**
	 * Get a specific verification case by ID for a candidate
	 */
	public VerificationCaseDTO getCandidateVerificationCase(Long candidateId, Long caseId) {
		log.info("Fetching verification case {} for candidateId: {}", caseId, candidateId);

		VerificationCase verificationCase = verificationCaseRepository.findByCaseIdAndCandidateId(caseId, candidateId)
				.orElseThrow(() -> new RuntimeException(
						String.format("Verification case %d not found for candidate %d", caseId, candidateId)));

		return convertToDTO(verificationCase);
	}

	/**
	 * Get case statistics for a candidate
	 */
	public CaseStatisticsDTO getCaseStatistics(Long candidateId) {
		log.info("Fetching case statistics for candidateId: {}", candidateId);

		return CaseStatisticsDTO.builder().totalCases(verificationCaseRepository.countByCandidateId(candidateId))
				.completedCases(verificationCaseRepository.countByCandidateIdAndStatus(candidateId,
						com.org.bgv.constants.CaseStatus.COMPLETED))
				.inProgressCases(verificationCaseRepository.countByCandidateIdAndStatus(candidateId,
						com.org.bgv.constants.CaseStatus.IN_PROGRESS))
				//  .pendingCases(verificationCaseRepository.countByCandidateIdAndStatus(
				//      candidateId, com.org.bgv.constants.CaseStatus.PENDING))
				.rejectedCases(verificationCaseRepository.countByCandidateIdAndStatus(candidateId,
						com.org.bgv.constants.CaseStatus.CANCELLED))
				.build();
	}

	/**
	 * Convert VerificationCase entity to DTO
	 */
	private VerificationCaseDTO convertToDTO(VerificationCase verificationCase) {
		// Get company details
		Company company = companyRepository.findById(verificationCase.getCompanyId()).orElse(null);

		// Get document count
		Integer documentsCount = verificationCaseDocumentRepository.countByVerificationCase(verificationCase);

		// Get check statistics
		List<VerificationCaseCheck> checks = verificationCaseCheckRepository.findByVerificationCase(verificationCase);
		Integer totalChecks = checks.size();
		Integer checksCompleted = (int) checks.stream().filter(check -> check.getStatus() == CaseCheckStatus.COMPLETED)
				.count();
		/*
		 * // Get package details from EmployerPackage String packageName =
		 * verificationCase.getEmployerPackage() != null ?
		 * verificationCase.getEmployerPackage().getPackageName() :
		 * "Standard Verification";
		 * 
		 * String verificationType = verificationCase.getEmployerPackage() != null ?
		 * verificationCase.getEmployerPackage().getVerificationType() :
		 * "Background Check";
		 */
		return VerificationCaseDTO.builder().caseId(verificationCase.getCaseId())
				.candidateId(verificationCase.getCandidateId()).companyId(verificationCase.getCompanyId())
				.companyName(company != null ? company.getCompanyName() : "Unknown Company")
				.companyLogo(company != null ? company.getAdminProfilePicturePath() : null)
				.status(verificationCase.getStatus()).createdAt(verificationCase.getCreatedAt())
				.updatedAt(verificationCase.getUpdatedAt()).completedAt(verificationCase.getCompletedAt())
				// .verificationType(verificationType)
				// .packageName(packageName)
				.documentsCount(documentsCount).checksCompleted(checksCompleted).totalChecks(totalChecks)
				// .vendorId(verificationCase.getVendorId())
				//  .vendorName(getVendorName(verificationCase.getVendorId()))
				.build();
	}

	/**
	 * Helper method to get vendor name (you might need to implement this)
	 */
	private String getVendorName(Long vendorId) {
		if (vendorId == null)
			return null;
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

		Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

		return PageRequest.of(page, size, sort);
	}

	

	private Specification<VerificationCase> buildSpecification(CaseSearchRequest request) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			// 🔹 Company filter (MANDATORY)
			if (request.getCompanyId() != null) {
				predicates.add(cb.equal(root.get("companyId"), request.getCompanyId()));
			}

			// 🔹 Candidate filter
			if (request.getCandidateId() != null) {
				predicates.add(cb.equal(root.get("candidateId"), request.getCandidateId()));
			}

			// 🔹 Free text search (case number)
			if (StringUtils.hasText(request.getSearch())) {
				String like = "%" + request.getSearch().toLowerCase() + "%";
				predicates.add(cb.like(cb.lower(root.get("caseNumber")), like));
			}

			// 🔹 Filters
			if (request.getFilters() != null) {

				for (FilterRequest filter : request.getFilters()) {

					if (!Boolean.TRUE.equals(filter.getIsSelected()) || filter.getSelectedValue() == null)
						continue;

					switch (filter.getField()) {

					case "status":
						predicates.add(cb.equal(root.get("status"), filter.getSelectedValue()));
						break;

					case "createdDate":
						try {
							LocalDateTime date = LocalDateTime.parse(filter.getSelectedValue().toString());

							predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), date));
						} catch (Exception e) {
							log.warn("Invalid date filter");
						}
						break;
					}
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private Pageable createPageable(PaginationRequest pagination, SortingRequest sorting) {

		if (pagination == null) {
			pagination = PaginationRequest.builder().page(0).size(10).build();
		}

		if (sorting == null) {
			sorting = SortingRequest.builder().sortBy("createdAt").sortDirection("desc").build();
		}

		Sort sort = Sort.by(
				sorting.getSortDirection().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
				sorting.getSortBy());

		return PageRequest.of(pagination.getPage(), pagination.getSize(), sort);
	}

	private PaginationResponse<CompanyVerificationCaseDTO> buildResponse(Page<VerificationCase> page,
			CaseSearchRequest request) {

		List<CompanyVerificationCaseDTO> content = page.getContent().stream().map(this::mapToDTO).toList();

		PaginationMetadata pagination = PaginationMetadata.builder().currentPage(page.getNumber())
				.pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
				.hasNext(page.hasNext()).hasPrevious(page.hasPrevious()).build();
		List<FilterMetadata> filters = getAvailableFilters(request);
		List<ColumnMetadata> columns = getColumnMetadata(request.getCompanyId());

		return PaginationResponse.<CompanyVerificationCaseDTO>builder().content(content).pagination(pagination)
				.columns(columns).filters(filters).build();
	}

	public PaginationResponse<CompanyVerificationCaseDTO> searchCases(CaseSearchRequest request) {

		Pageable pageable = createPageable(request.getPagination(), request.getSorting());

		Specification<VerificationCase> spec = buildSpecification(request);

		Page<VerificationCase> page = verificationCaseRepository.findAll(spec, pageable);

		return buildResponse(page, request);
	}

	private CompanyVerificationCaseDTO mapToDTO(VerificationCase entity) {

		Optional<Candidate> candidateOpt = candidateRepository.findById(entity.getCandidateId());

		String candidateName = candidateOpt.map(c -> c.getFirstName()) // or full name
				.orElse("N/A");

		return CompanyVerificationCaseDTO.builder().caseId(entity.getCaseId()).caseNumber(entity.getCaseNumber())
				.candidateId(entity.getCandidateId()).companyId(entity.getCompanyId())
				.status(entity.getStatus() != null ? entity.getStatus().name() : null)
				.totalPrice(entity.getTotalPrice() != null ? entity.getTotalPrice().doubleValue() : null)
				.createdAt(entity.getCreatedAt()).candidateName(candidateName).build();
	}

	private List<ColumnMetadata> getColumnMetadata(Long companyId) {

		List<ColumnMetadata> columns = new ArrayList<>();

		columns.add(ColumnMetadata.builder().field("caseId").displayName("Case ID").visible(false).build());
		/*
		 * columns.add(ColumnMetadata.builder() .field("caseNumber")
		 * .displayName("Case Number") .visible(true) .build());
		 */

		/*
		 * columns.add(ColumnMetadata.builder() .field("candidateId")
		 * .displayName("Candidate ID") .visible(false) .build());
		 */

		// 👉 You will likely show this in UI (if joined later)
		columns.add(
				ColumnMetadata.builder().field("candidateName").displayName("Candidate Name").visible(true).build());

		columns.add(ColumnMetadata.builder().field("status").displayName("Status").visible(true).build());

		columns.add(ColumnMetadata.builder().field("createdAt").displayName("Created Date").visible(true).build());

		return columns;
	}

	private List<FilterMetadata> getAvailableFilters(CaseSearchRequest searchRequest) {

		List<FilterMetadata> filters = new ArrayList<>();

		// 🔹 Case Status filter
		FilterMetadata statusFilter = FilterMetadata.builder().field("status").displayName("Case Status")
				.type("dropdown")
				.options(Arrays.asList(Option.builder().label("Initiated").value("INITIATED").build(),
						Option.builder().label("In Progress").value("IN_PROGRESS").build(),
						Option.builder().label("Completed").value("COMPLETED").build(),
						Option.builder().label("On Hold").value("ON_HOLD").build(),
						Option.builder().label("Cancelled").value("CANCELLED").build()))
				.build();

		// 🔹 Created Date filter
		FilterMetadata createdDateFilter = FilterMetadata.builder().field("createdAt").displayName("Created Date")
				.type("dateRange").build();

		/*
		 * // 🔹 (Optional) Candidate filter FilterMetadata candidateFilter =
		 * FilterMetadata.builder() .field("candidateId") .displayName("Candidate")
		 * .type("text") // or dropdown later .build();
		 * 
		 */

		/* ---------- Set selected values ---------- */
		if (searchRequest.getFilters() != null) {

			for (FilterRequest filter : searchRequest.getFilters()) {

				if (filter.getIsSelected() != null && filter.getIsSelected()) {

					switch (filter.getField()) {

					case "status":
						statusFilter.setSelectedValue(filter.getSelectedValue());
						break;

					case "createdAt":
						createdDateFilter.setSelectedValue(filter.getSelectedValue());
						break;
					/*
					 * case "candidateId":
					 * candidateFilter.setSelectedValue(filter.getSelectedValue()); break;
					 */
					}
				}
			}
		}

		filters.add(statusFilter);
		filters.add(createdDateFilter);
		// filters.add(candidateFilter);

		return filters;
	}

	public VerificationCaseDetailsDTO getVerificationCaseDetails(Long caseId) {

		VerificationCase verificationCase = verificationCaseRepository.findById(caseId)
				.orElseThrow(() -> new RuntimeException("Verification case not found"));

		// Fetch once
		List<VerificationCaseCheck> caseChecks = verificationCaseCheckRepository.findByVerificationCase_CaseId(caseId);

		CandidateSummary candidateSummary = buildCandidateSummary(verificationCase.getCandidateId());

		List<VerificationCheckDTO> verificationChecks = buildVerificationChecks(caseChecks);

		List<ActivityTimelineDTO> activityTimeline = buildActivityTimeline(verificationCase, caseChecks);

		VPackageDTO vpackageDTO = buildVPackageDTO(verificationCase);

		PricingDTO pricingDTO = PricingDTO.builder()
				.basePrice(Optional.ofNullable(verificationCase.getBasePrice()).orElse(0.0))
				.addonPrice(Optional.ofNullable(verificationCase.getAddonPrice()).orElse(0.0))
				.totalPrice(
						verificationCase.getTotalPrice() != null ? verificationCase.getTotalPrice().doubleValue() : 0.0)
				.build();

		return VerificationCaseDetailsDTO.builder().caseId(verificationCase.getCaseId())
				.caseNumber(verificationCase.getCaseNumber())
				.status(verificationCase.getStatus() != null ? verificationCase.getStatus().name() : null)
				.createdAt(verificationCase.getCreatedAt()).completedAt(verificationCase.getCompletedAt())
				.candidate(candidateSummary).verificationChecks(verificationChecks).activityTimeline(activityTimeline)
				.vpackage(vpackageDTO).pricing(pricingDTO).build();
	}

	private CandidateSummary buildCandidateSummary(Long candidateId) {

		return candidateRepository.findById(candidateId).map((Candidate candidate) -> {

			String firstName = candidate.getFirstName() != null ? candidate.getFirstName() : "";
			String lastName = candidate.getLastName() != null ? candidate.getLastName() : "";

			String fullName = (firstName + " " + lastName).trim();

			return CandidateSummary.builder().candidateId(candidate.getCandidateId()).firstName(firstName)
					.lastName(lastName)
					// .name(fullName.isEmpty() ? "Unknown Candidate" : fullName)
					.email(candidate.getUser() != null ? candidate.getUser().getEmail() : null)
					.phone(candidate.getPhoneNumber()).build();
		}).orElse(CandidateSummary.builder().candidateId(candidateId).firstName("Unknown").lastName("")
				// .name("Unknown Candidate")
				.build());
	}

	private List<VerificationCheckDTO> buildVerificationChecks(List<VerificationCaseCheck> caseChecks) {

		if (caseChecks == null || caseChecks.isEmpty()) {
			return new ArrayList<>();
		}

		return caseChecks.stream().map((VerificationCaseCheck check) -> {

			CheckCategory category = check.getCategory();

			return VerificationCheckDTO.builder().id(check.getCaseCheckId())
					.name(category != null ? category.getName() : "Unknown Check")
					.description(category != null ? category.getDescription() : null).status(check.getStatus().name())
					.icon(iconService.getIconForVerification(category.getName())).build();
		}).collect(Collectors.toList());
	}

	private List<ActivityTimelineDTO> buildActivityTimeline(VerificationCase verificationCase,
			List<VerificationCaseCheck> caseChecks) {

		List<ActivityTimelineDTO> timeline = new ArrayList<>();
		long idSeq = 1L;

		// 1️⃣ Case Created
		timeline.add(ActivityTimelineDTO.builder().id(idSeq++).title("Case Initiated")
				.description("Verification case " + verificationCase.getCaseNumber() + " created")
				.timestamp(formatTimestamp(verificationCase.getCreatedAt())).icon("📁").status("COMPLETED")
				.type("CASE_EVENT").build());

		// 2️⃣ Each Check Progress
		if (caseChecks != null) {
			for (VerificationCaseCheck check : caseChecks) {

				String categoryName = check.getCategory() != null ? check.getCategory().getName() : "Verification";

				// String status = mapCheckStatus(check.getStatus());

				timeline.add(ActivityTimelineDTO.builder().id(idSeq++).title(categoryName + " Check")
						.description(categoryName + " is " + check.getStatus())
						.timestamp(resolveCheckTimestamp(check, verificationCase))
						.icon(iconService.getIconForVerification(categoryName))
						.status(check.getStatus().name())
						.type("CHECK_EVENT").build());
			}
		}

		// 3️⃣ Case Completed
		if (verificationCase.getCompletedAt() != null) {
			timeline.add(ActivityTimelineDTO.builder().id(idSeq++).title("Case Completed")
					.description("All verifications completed")
					.timestamp(formatTimestamp(verificationCase.getCompletedAt())).icon("✅").status("COMPLETED")
					.type("CASE_EVENT").build());
		}

		return timeline;
	}

	private String formatTimestamp(LocalDateTime time) {
		if (time == null)
			return null;
		return time.format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a"));
	}

	private String resolveCheckTimestamp(VerificationCaseCheck check, VerificationCase verificationCase) {

		if (check.getUpdatedAt() != null) {
			return formatTimestamp(check.getUpdatedAt());
		}

		if (check.getCreatedAt() != null) {
			return formatTimestamp(check.getCreatedAt());
		}

		return formatTimestamp(verificationCase.getCreatedAt());
	}

	private VPackageDTO buildVPackageDTO(VerificationCase verificationCase) {

	    if (verificationCase == null || verificationCase.getEmployerPackage() == null) {
	        return null;
	    }

	    EmployerPackage pkg = verificationCase.getEmployerPackage();

	    return VPackageDTO.builder()
	            .id(pkg.getId() != null ? pkg.getId().toString() : null)
	            .name(pkg.getBgvPackage() != null ? pkg.getBgvPackage().getName() : "N/A")

	            // Pricing (use total from case, not package)
	            .price(verificationCase.getTotalPrice() != null
	                    ? verificationCase.getTotalPrice().toString()
	                    : "0.0")

	            // Package status
	            .status(pkg.getStatus() != null
	                    ? pkg.getStatus().name()
	                    : "ACTIVE")

	            // When assigned to case
	            .assignedDate(formatTimestamp(verificationCase.getCreatedAt()))

	            // Timeline (can be dynamic later)
	           // .timeline(resolveTimeline(pkg))

	            .build();
	}
	
	/*
	private String resolveTimeline(EmployerPackage pkg) {

	    // If you have field like pkg.getTATDays()
	    if (pkg.getTatDays() != null) {
	        return pkg.getTatDays() + " days";
	    }

	    // fallback
	    return "30 days";
	}
	*/
	
	
	@Transactional
	public void removeVerificationCase(Long caseId) {
	    log.info("Removing verification case with caseId: {}", caseId);

	    VerificationCase verificationCase = verificationCaseRepository.findById(caseId)
	            .orElseThrow(() -> new RuntimeException("Verification case not found with id: " + caseId));

	    // 1. Delete document links first (no cascade from VerificationCaseDocument → Link)
	    List<VerificationCaseDocument> caseDocuments =
	            verificationCaseDocumentRepository.findByVerificationCaseCaseId(caseId);

	    for (VerificationCaseDocument doc : caseDocuments) {
	        verificationCaseDocumentLinkRepository.deleteAllByCaseDocument(doc);
	    }
	    log.info("Deleted document links for caseId: {}", caseId);

	    // 2. Delete candidate verification (tracks candidate upload progress)
	    if (candidateVerificationRepository
	            .existsByCandidateIdAndVerificationCaseCaseId(
	                    verificationCase.getCandidateId(), caseId)) {
	        candidateVerificationRepository
	                .deleteByVerificationCaseCaseId(caseId);
	        log.info("Deleted candidate verification for caseId: {}", caseId);
	    }

	    // 3. Delete candidate package rule documents tied to this case
	    candidatePackageRuleDocumentRepository.deleteByVerificationCaseCaseId(caseId);
	    log.info("Deleted candidate package rule documents for caseId: {}", caseId);

	    // 4. Delete candidate package rules tied to this case
	    candidatePackageRuleRepository.deleteByVerificationCaseCaseId(caseId);
	    log.info("Deleted candidate package rules for caseId: {}", caseId);

	    // 5. Delete documents (uploaded files) linked to this case
	    documentRepository.deleteByVerificationCaseCaseId(caseId);
	    log.info("Deleted uploaded documents for caseId: {}", caseId);
	    
	    verificationActionEvidenceRepository.deleteByActionVerificationCaseCaseId(caseId);
	    log.info("Deleted verification action evidence for caseId: {}", caseId);
	    
	    verificationActionRepository.deleteByVerificationCaseCaseId(caseId);
	    log.info("Deleted verification actions for caseId: {}", caseId);
	    
	    identityProofRepository.deleteByVerificationCaseCaseId(caseId);
	    log.info("Deleted identity proofs for caseId: {}", caseId);
	    
	    educationHistoryRepository.deleteByVerificationCaseCaseId(caseId);
	    log.info("Deleted education history for caseId: {}", caseId);
	    
	    workExperienceRepository.deleteByVerificationCaseCaseId(caseId);
	    log.info("Deleted work experience for caseId: {}", caseId);
	    
	    addressRepository.deleteByVerificationCase_CaseId(caseId);
	    
	    verificationCaseSelectionRepository.deleteByVerificationCase_CaseId(caseId);

	    // 6. Delete the verification case itself
	    //    (cascades to VerificationCaseDocument + VerificationCaseCheck via CascadeType.ALL)
	    verificationCaseRepository.delete(verificationCase);
	    log.info("Verification case {} removed successfully", caseId);
	}
	
	
	/*
	
	@Transactional
	public void populateCaseDocuments(Long caseId) {

	    // 1. Fetch case
	    VerificationCase verificationCase = verificationCaseRepository.findById(caseId)
	            .orElseThrow(() -> new RuntimeException("Case not found"));

	    // 2. Fetch rule documents
	    List<CandidatePackageRuleDocument> ruleDocs =
	            candidatePackageRuleDocumentRepository.findByVerificationCase_CaseId(caseId);

	    for (CandidatePackageRuleDocument ruleDoc : ruleDocs) {

	        Long docTypeId = ruleDoc.getDocumentTypeId();
	        Long categoryId = ruleDoc.getCategoryId();

	        // 3. Avoid duplicate case document
	        boolean alreadyExists =
	                verificationCaseDocumentRepository
	                        .existsByVerificationCase_CaseIdAndDocumentType_DocTypeId(caseId, docTypeId);

	        if (alreadyExists) continue;
	        
	        DocumentType documentType=documentTypeRepository.findById(docTypeId).orElseThrow(()->new RuntimeException("Document type record not Found"));
	       
	        CheckCategory checkCategory = checkCategoryRepository.findByCategoryId(categoryId);
	        // 4. Create VerificationCaseDocument
	        VerificationCaseDocument caseDoc = VerificationCaseDocument.builder()
	                .verificationCase(verificationCase)
	                .checkCategory(checkCategory)
	                .documentType(documentType) // or fetch if needed
	                .required(ruleDoc.getRequired()) 
	                .isAddOn(ruleDoc.getSelected())
	                .documentPrice(ruleDoc.getPrice())
	                .verificationStatus(DocumentStatus.PENDING)
	                .build();

	        caseDoc = verificationCaseDocumentRepository.save(caseDoc);

	        // 5. Fetch uploaded documents
	        List<Document> uploadedDocs =
	                documentRepository.findByVerificationCase_CaseIdAndDocTypeId(caseId, documentType);

	        // 6. Link documents
	        for (Document doc : uploadedDocs) {

	            boolean linkExists =
	                    verificationCaseDocumentLinkRepository
	                            .existsByCaseDocumentAndDocument(caseDoc, doc);

	            if (linkExists) continue;

	            VerificationCaseDocumentLink link = VerificationCaseDocumentLink.builder()
	                    .caseDocument(caseDoc)
	                    .document(doc)
	                    .status(DocumentStatus.SUBMITTED)
	                    .build();

	            verificationCaseDocumentLinkRepository.save(link);
	        }

	        // 7. Update overall status
	        if (!uploadedDocs.isEmpty()) {
	            caseDoc.setVerificationStatus(DocumentStatus.SUBMITTED);
	        }

	        verificationCaseDocumentRepository.save(caseDoc);
	    }
	}
	*/
	
	private int safeCount(Integer count) {
	    return count != null ? count : 1;
	}
}