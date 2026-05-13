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

	@Transactional
	public void populateSelections(Long caseId) {

		VerificationCase verificationCase = verificationCaseRepository.findById(caseId)
				.orElseThrow(() -> new RuntimeException("Case not found"));

		List<CandidatePackageRule> rules = candidatePackageRuleRepository.findByVerificationCase_CaseId(caseId);

		for (CandidatePackageRule rule : rules) {

			Long categoryId = rule.getCheckCategoryId();
			
			CheckCategory checkCategory =checkCategoryRepository.findByCategoryId(categoryId);

			// =========================
			// 1. DOCUMENT TYPE RULES
			// =========================
			List<CandidatePackageRuleDocument> docRules = candidatePackageRuleDocumentRepository.findByRule(rule);

			for (CandidatePackageRuleDocument docRule : docRules) {

				VerificationCaseSelection selection = createSelection(verificationCase, CheckCategoryEnum.IDENTITY,
						docRule.getDocumentTypeId());

				// Attach documents
				List<Document> docs = documentRepository.findByVerificationCase_CaseIdAndDocTypeIdDocTypeId(caseId,docRule.getDocumentTypeId());

				for (Document doc : docs) {
					if (doc.getSelection() == null) {
						doc.setSelection(selection);
						documentRepository.save(doc);
					}
				}

				updateSelectionStatus(selection);
			}

			// =========================
			// 2. EDUCATION RULES
			// =========================
			if (checkCategory!=null && checkCategory.getName().equalsIgnoreCase(CheckCategoryEnum.EDUCATION.getName())) {

				RuleTypes ruleType = ruleTypesRepository
			            .findById(rule.getRuleTypeId())
			            .orElse(null);

			    if (ruleType == null) continue;

				
				int maxCount = rule.getSelectedCount() != null ? rule.getSelectedCount() : 1;

				// List<EducationHistory> selectedEducation = selectLatestEducation(caseId, maxCount);
				
				List<EducationHistory> selectedEducation = new ArrayList<>();
				
				// 🔥 HIGHEST_ONLY LOGIC
			    if ("HIGHEST_EDUCATION".equalsIgnoreCase(ruleType.getCode())) {

			    	selectedEducation.addAll(selectLatestEducation(caseId,1));
			    }// 🔥 2. ALL EDUCATION
			    else if ("ALL_EDUCATION".equalsIgnoreCase(ruleType.getCode())) {

			        selectedEducation.addAll(
			                educationHistoryRepository.findByVerificationCaseCaseId(caseId)
			        );
			    }
			 // 🔥 3. COUNT BASED (e.g. last N records)
			    else if ("LAST_N_EDUCATION".equalsIgnoreCase(ruleType.getCode())) {

			        selectedEducation.addAll(selectLatestEducation(caseId, maxCount));
			    }

				for (EducationHistory edu : selectedEducation) {

					VerificationCaseSelection selection = createSelection(verificationCase, CheckCategoryEnum.EDUCATION,
							edu.getId());

					// attach case reference
					edu.setVerificationCase(verificationCase);
					educationHistoryRepository.save(edu);

					// link documents
					attachDocumentsToSelection(selection, edu.getId(), checkCategory.getCategoryId());

					updateSelectionStatus(selection);
				}
			}

			// =========================
			// 3. WORK RULES
			// =========================
			if (checkCategory!=null && checkCategory.getName().equalsIgnoreCase(CheckCategoryEnum.WORK.getName())) {

				RuleTypes ruleType = ruleTypesRepository
			            .findById(rule.getRuleTypeId())
			            .orElse(null);

			    if (ruleType == null) continue;
			    
			    List<WorkExperience> selectedWork = new ArrayList<>();
			    
			    String ruleCode = ruleType.getCode();
			    
			    int maxCount = rule.getSelectedCount() != null ? rule.getSelectedCount() : 2;
				
				
			 // 🔥 1. LAST N COMPANIES
			    if ("LAST_N_COMPANIES".equalsIgnoreCase(ruleCode)) {

			        selectedWork.addAll(selectLatestWork(caseId, maxCount));
			    }

			    // 🔥 2. ALL COMPANIES
			    else if ("ALL_COMPANIES".equalsIgnoreCase(ruleCode)) {

			        selectedWork.addAll(
			                workExperienceRepository.findByVerificationCaseCaseId(caseId)
			        );
			    }

				// List<WorkExperience> selectedWork = selectLatestWork(caseId, maxCount);

				for (WorkExperience work : selectedWork) {

					VerificationCaseSelection selection = createSelection(verificationCase, CheckCategoryEnum.WORK,
							work.getExperienceId());

					work.setVerificationCase(verificationCase);
					workExperienceRepository.save(work);

					attachDocumentsToSelection(selection, work.getExperienceId(), checkCategory.getCategoryId());

					updateSelectionStatus(selection);
				}
			}
			
			// =========================
			// 4. ADDRESS RULES
			// =========================
			if (checkCategory != null &&
			    checkCategory.getName().equalsIgnoreCase(CheckCategoryEnum.ADDRESS.getName())) {

			    RuleTypes ruleType = ruleTypesRepository
			            .findById(rule.getRuleTypeId())
			            .orElse(null);

			    if (ruleType == null) continue;

			    List<Address> selectedAddresses = new ArrayList<>();

			    String ruleCode = ruleType.getCode();

			    int value = rule.getSelectedCount() != null ? rule.getSelectedCount() : 1;

			    // 🔥 1. LAST N ADDRESSES
			    if ("LAST_N_ADDRESS".equalsIgnoreCase(ruleCode)) {

			        selectedAddresses.addAll(selectLatestAddresses(caseId, value));
			    }

			    // 🔥 2. ADDRESS DURATION (X YEARS)
			    else if ("ADDRESS_DURATION".equalsIgnoreCase(ruleCode)) {

			        selectedAddresses.addAll(selectAddressByDuration(caseId, value));
			    }

			    // 🔥 SAVE SELECTIONS
			    for (Address addr : selectedAddresses) {

			        VerificationCaseSelection selection = createSelection(
			                verificationCase,
			                CheckCategoryEnum.ADDRESS,
			                addr.getId()
			        );

			        addr.setVerificationCase(verificationCase);
			        addressRepository.save(addr);

			        attachDocumentsToSelection(
			                selection,
			                addr.getId(),
			                checkCategory.getCategoryId()
			        );

			        updateSelectionStatus(selection);
			    }
			}
		}
	}

	private VerificationCaseSelection createSelection(VerificationCase caseObj, CheckCategoryEnum type, Long referenceId) {

		Optional<VerificationCaseSelection> existing = verificationCaseSelectionRepository
				.findByVerificationCaseAndTypeAndReferenceId(caseObj, type, referenceId);

		if (existing.isPresent()) {
			return existing.get();
		}

		VerificationCaseSelection selection = VerificationCaseSelection.builder().verificationCase(caseObj).type(type)
				.referenceId(referenceId)
				.status(CaseCheckStatus.PENDING.name())
				.build();

		return verificationCaseSelectionRepository.save(selection);
	}

	private void attachDocumentsToSelection(VerificationCaseSelection selection, Long objectId,
			Long checkCategoryId) {

		List<Document> docs = documentRepository.findByCategory_CategoryIdAndObjectId(checkCategoryId,objectId);

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
	            .sorted(
	                Comparator.<EducationHistory, LocalDate>comparing(e -> {
	                    if (e.getToDate() != null) {
	                        return e.getToDate();
	                    } else if (e.getYearOfPassing() != null) {
	                        return LocalDate.of(e.getYearOfPassing(), 12, 31);
	                    } else {
	                        return LocalDate.MIN;
	                    }
	                }).reversed()
	            )
	            .limit(limit)
	            .toList();
	}
	
	private List<WorkExperience> selectLatestWork(Long caseId, int limit) {

	    return workExperienceRepository.findByVerificationCaseCaseId(caseId)
	            .stream()
	            .sorted(
	                Comparator.<WorkExperience, LocalDate>comparing(w -> {
	                    if (w.getEnd_date() != null) {
	                        return w.getEnd_date();
	                    } else {
	                        return LocalDate.now(); // current job
	                    }
	                }).reversed()
	            )
	            .limit(limit)
	            .toList();
	}
	
	private List<Address> selectLatestAddresses(Long caseId, int limit) {

	    return addressRepository.findByVerificationCaseCaseId(caseId)
	            .stream()
	            .sorted(
	                Comparator.<Address, LocalDate>comparing(a -> {
	                    if (a.getToDate() != null) {
	                        return a.getToDate();
	                    } else {
	                        return LocalDate.now(); // current address
	                    }
	                }).reversed()
	            )
	            .limit(limit)
	            .toList();
	}
	
	private List<Address> selectAddressByDuration(Long caseId, int years) {

	    List<Address> addresses = addressRepository
	            .findByVerificationCaseCaseId(caseId)
	            .stream()
	            .sorted(
	                Comparator.<Address, LocalDate>comparing(a -> {
	                    if (a.getFromDate() != null) {
	                        return a.getFromDate();
	                    } else {
	                        return LocalDate.MIN;
	                    }
	                }).reversed()
	            )
	            .toList();

	    List<Address> result = new ArrayList<>();

	    LocalDate cutoffDate = LocalDate.now().minusYears(years);

	    for (Address addr : addresses) {

	        result.add(addr);

	        LocalDate toDate = addr.getToDate() != null
	                ? addr.getToDate()
	                : LocalDate.now();

	        // 🔥 stop when coverage reaches required duration
	        if (toDate.isBefore(cutoffDate)) {
	            break;
	        }
	    }

	    return result;
	}
}
