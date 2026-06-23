package com.org.bgv.vendor.service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.common.DocumentTypeInfo;
import com.org.bgv.common.RoleConstants;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.*;
import com.org.bgv.enums.VendorNoteType;
import com.org.bgv.repository.*;
import com.org.bgv.service.util.UserServiceUtil;
import com.org.bgv.vendor.action.dto.ActionDTO;
import com.org.bgv.vendor.action.dto.VendorActionCatalog;
import com.org.bgv.vendor.builder.ObjectBuilderRegistry;
import com.org.bgv.vendor.dto.ActionType;
import com.org.bgv.vendor.dto.CandidateInfoDTO;
import com.org.bgv.vendor.dto.EvidenceTypeDTO;
import com.org.bgv.vendor.dto.RequirementDTO;
import com.org.bgv.vendor.dto.SlaInfoDTO;
import com.org.bgv.vendor.dto.TimelineEventDTO;
import com.org.bgv.vendor.dto.VendorNoteDTO;
import com.org.bgv.vendor.dto.VerificationCheckResponseDTO;
import com.org.bgv.vendor.dto.VerificationDocumentDTO;
import com.org.bgv.vendor.dto.VerificationHistoryDTO;
import com.org.bgv.vendor.entity.CategoryEvidenceType;
import com.org.bgv.vendor.entity.EvidenceType;
import com.org.bgv.vendor.entity.VendorNote;
import com.org.bgv.vendor.entity.VerificationCheckHistory;
import com.org.bgv.vendor.entity.VerificationTimeline;
import com.org.bgv.vendor.repository.CategoryEvidenceTypeRepository;
import com.org.bgv.vendor.repository.VendorNoteRepository;
import com.org.bgv.vendor.repository.VerificationCheckHistoryRepository;
import com.org.bgv.vendor.repository.VerificationTimelineRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCheckService {

	private final VerificationCaseCheckRepository verificationCaseCheckRepository;
	private final VerificationCaseRepository verificationCaseRepository;
	private final CandidateRepository candidateRepository;
	private final VerificationCaseDocumentRepository verificationCaseDocumentRepository;
	private final VendorNoteRepository vendorNoteRepository;
	private final VerificationTimelineRepository timelineRepository;
	private final VerificationCheckHistoryRepository historyRepository;
	private final VerificationCaseDocumentLinkRepository verificationCaseDocumentLinkRepository;
	private final CategoryEvidenceTypeRepository categoryEvidenceTypeRepository;
	private final CheckCategoryRepository checkCategoryRepository;
	private final ObjectBuilderRegistry objectBuilderRegistry;
	private final UserServiceUtil userServiceUtil;

	@Transactional(readOnly = true)
	public VerificationCheckResponseDTO getVerificationCheck(Long checkId, Long vendorId) {

		log.info("Fetching verification check {} for vendor {}", checkId, vendorId);
		
		VerificationCaseCheck check = null;
		
		boolean isVendorAdmin =
		        userServiceUtil.hasRole(
		                vendorId,
		                RoleConstants.ROLE_VENDOR_ADMINISTRATOR);
		
		if(isVendorAdmin) {
			
			check = verificationCaseCheckRepository.findById(checkId).get();
			
		}else {
			check = verificationCaseCheckRepository.findByAssignedVendorUser_UserIdAndCaseCheckId(vendorId, checkId).get();
		}
		// .orElseThrow(() -> new RuntimeException("Verification check not found"));
		log.info("getVerificationCheck:::::::::::**************************::::::::::::::::::");
		

		if (!isVendorAdmin &&
		    (check.getAssignedVendorUser() == null ||
		     !Objects.equals(
		             check.getAssignedVendorUser().getUserId(),
		             vendorId))) {

		    throw new AccessDeniedException(
		            "Vendor not authorized to access this check");
		}

		log.info("getVerificationCheck:::::::::::1111111111111111111::::::::::::::::::");
		
		VerificationCase verificationCase = check.getVerificationCase();

		Candidate candidate = candidateRepository.findById(verificationCase.getCandidateId())
				.orElseThrow(() -> new RuntimeException("Candidate not found"));
		
		log.info("getVerificationCheck:::::::::::222222222222222::::::::::::::::::");

		// 2️⃣ Build base response
		VerificationCheckResponseDTO response = buildVerificationCheckResponse(check, verificationCase, candidate);
		
		log.info("getVerificationCheck:::::::::::3333333333333333::::::::::::::::::");

		CheckCategory category = checkCategoryRepository.findByCategoryId(check.getCategory().getCategoryId());

		// 3️⃣ Objects (SECTION-WISE DATA)
		// response.setObjects(buildObjects(check, category));
		
		response.setObjects(
		        objectBuilderRegistry
		                .getStrategy(
		                        CheckCategoryEnum.fromName(
		                                category.getName()))
		                .buildObjects(check));
		
		log.info("getVerificationCheck:::::::::::44444444444::::::::::::::::::");

		// 4️⃣ Static / unchanged lists
		response.setEvidenceTypeList(getAllowedEvidenceTypes(check.getCategory().getCategoryId()));
		
		log.info("getVerificationCheck:::::::::::555555555555555::::::::::::::::::");

		response.setDocumentTypeInfos(getDocumentTypesForCategory(check));

		return response;
	}

	private VerificationCheckResponseDTO buildVerificationCheckResponse(VerificationCaseCheck check,
			VerificationCase verificationCase, Candidate candidate) {

		return VerificationCheckResponseDTO.builder()
				.caseId(String.valueOf(verificationCase.getCaseId()))
				.caseRef(getCaseReference(verificationCase))
				.checkId(String.valueOf(check.getCaseCheckId()))
				.checkRef(check.getCheckRef())
				//.checkType(check.getCategory().getName())
				.checkType(CheckCategoryEnum.fromName(check.getCategory().getName()))
				.checkName(check.getCategory().getName())
				.status(check.getStatus().name())
				.candidate(mapCandidateInfo(candidate))
				.sendNotification(check.getStatus().name().equalsIgnoreCase(CaseCheckStatus.ACTION_REQUIRED.name()))
				// .audit(buildAudit(check))
				 .actions(resolveCheckActions(check.getStatus())) 
				 
				.build();
	}


	private List<EvidenceTypeDTO> getAllowedEvidenceTypes(Long categoryId) {
		return categoryEvidenceTypeRepository.findByCategoryCategoryIdAndActiveTrue(categoryId).stream()
				.map(CategoryEvidenceType::getEvidenceType) // extract EvidenceType
				.map(this::toDTO).toList();

	}

	private EvidenceTypeDTO toDTO(EvidenceType et) {
		return EvidenceTypeDTO.builder().id(et.getId()).name(et.getLabel()).value(et.getCode()).build();
	}

	private List<DocumentTypeInfo> getDocumentTypesForCategory(VerificationCaseCheck check) {
		// Long categoryId = check.getCategory().getId(); // get category ID from check

		return verificationCaseDocumentRepository
				.findByVerificationCaseCaseIdAndCheckCategoryCategoryId(check.getVerificationCase().getCaseId(),
						check.getCategory().getCategoryId())
				.stream().map(VerificationCaseDocument::getDocumentType) // extract DocumentType
				.map(this::toDocumentTypeInfo).toList(); // Java 16+, otherwise use Collectors.toList()
	}

	private DocumentTypeInfo toDocumentTypeInfo(DocumentType dt) {
		return DocumentTypeInfo.builder().docTypeId(dt.getDocTypeId()).name(dt.getName()).code(dt.getCode())
				// .price(dt.getPrice())
				.build();
	}

	

	

	// Common operations for all check types
	@Transactional
	public void updateCheckStatus(Long checkId, Long vendorId, String status, String notes) {
		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		if (!check.getAssignedVendorUser().getUserId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized");
		}

		String currentStatus = mapCheckStatus(check.getStatus());
		CaseCheckStatus newStatus = mapToCaseStatus(status);

		check.setStatus(newStatus);
		check.setUpdatedAt(LocalDateTime.now());

		saveVerificationHistory(check, currentStatus, status, vendorId, notes);
		saveTimelineEvent(check, "status_update", String.format("Status changed to %s", status), "Vendor Agent");

		verificationCaseCheckRepository.save(check);
		updateParentCaseStatus(check.getVerificationCase());
	}

	@Transactional
	public void addNote(Long checkId, Long vendorId, String content, VendorNoteType noteType) {
		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		if (!check.getAssignedVendorUser().getUserId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized");
		}

		VendorNote note = VendorNote.builder().verificationCaseCheck(check).content(content).createdBy("Vendor Agent")
				.createdAt(LocalDateTime.now()).type(noteType).isInternal(noteType.equals("internal")).build();

		vendorNoteRepository.save(note);
		saveTimelineEvent(check, "note_added", "Note added", "Vendor Agent");
	}

	
	@Transactional
	public void completeCheck(Long checkId, Long vendorId, String finalStatus, String summary) {
		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		if (!check.getAssignedVendorUser().getUserId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized");
		}

		CaseCheckStatus status = "verified".equals(finalStatus) ? CaseCheckStatus.COMPLETED
				: "insufficient".equals(finalStatus) ? CaseCheckStatus.INSUFFICIENT : CaseCheckStatus.IN_PROGRESS;

		check.setStatus(status);
		check.setUpdatedAt(LocalDateTime.now());

		addNote(checkId, vendorId, String.format("Check completed. Status: %s. Summary: %s", finalStatus, summary),
				VendorNoteType .VERIFICATION);

		String eventDescription = String.format("%s verification completed: %s", check.getCategory().getCode(),
				finalStatus);
		saveTimelineEvent(check, "verification_completed", eventDescription, "Vendor Agent");

		verificationCaseCheckRepository.save(check);
		updateParentCaseStatus(check.getVerificationCase());
	}

	// Common helper methods (same as before but more generic)
	private CandidateInfoDTO mapCandidateInfo(Candidate candidate) {

	    if (candidate == null) {
	        return CandidateInfoDTO.builder()
	                .name("Unknown Candidate")
	                .build();
	    }

	  //  Profile profile = candidate.getProfile();

	    String name = "Profile Pending";
	    String phone = null;

	    /*
	    if (profile != null) {
	        name = Stream.of(profile.getFirstName(), profile.getLastName())
	                .filter(Objects::nonNull)
	                .collect(Collectors.joining(" "));
	        phone = profile.getPhoneNumber();
	    }
*/
	    return CandidateInfoDTO.builder()
	            .candidateId(String.valueOf(candidate.getCandidateId()))
	            .candidateRef(candidate.getCandidateRef())
	            .name(name.isBlank() ? "Profile Pending" : name)
	            .phone(phone)
	            .build();
	}



	@Transactional(readOnly = true)
	public List<VerificationDocumentDTO> getDocuments(VerificationCaseCheck check) {
		// First, get the VerificationCaseDocument records for this case and check type
		List<VerificationCaseDocument> caseDocuments = verificationCaseDocumentRepository
				.findByVerificationCaseCaseIdAndCheckCategoryCategoryId(check.getVerificationCase().getCaseId(),
						check.getCategory().getCategoryId());

		return caseDocuments.stream().flatMap(caseDoc -> getLinkedDocuments(caseDoc).stream())
				.map(this::mapToDocumentDTO).collect(Collectors.toList());
	}

	private List<Document> getLinkedDocuments(VerificationCaseDocument caseDocument) {
		// Get all linked Document entities through VerificationCaseDocumentLink
		List<VerificationCaseDocumentLink> links = verificationCaseDocumentLinkRepository
				.findByCaseDocument(caseDocument);

		return links.stream().map(VerificationCaseDocumentLink::getDocument).filter(Objects::nonNull)
				.filter(doc -> doc.getStatus() != DocumentStatus.DELETED).collect(Collectors.toList());
	}

	private VerificationDocumentDTO mapToDocumentDTO(Document document) {
		return VerificationDocumentDTO.builder().id(document.getDocId()).name(buildDisplayName(document)) // ✅ PAN Card
																											// (abc.jpg)
				.type(getDocumentTypeName(document)).size(formatFileSize(document.getFileSize()))
				.uploadedBy(document.getUploadedBy()).uploadedAt(document.getUploadedAt())
				.status(mapDocumentStatus(document)).url(document.getFileUrl()).documentPath(document.getAwsDocKey())
				.mimeType(document.getFileType()).verificationNotes(document.getVerificationNotes())
				.comments(document.getComments()).verified(document.isVerified()).verifiedAt(document.getVerifiedAt())
				.verifiedBy(document.getVerifiedBy()).createdAt(document.getCreatedAt())
				.updatedAt(document.getUpdatedAt()).build();
	}

	private String buildDisplayName(Document document) {
		String docType = getDocumentTypeName(document); // PAN Card
		String fileName = document.getOriginalFileName() != null ? document.getOriginalFileName()
				: document.getFileName();

		if (docType != null && fileName != null) {
			return docType + " (" + fileName + ")";
		}

		return fileName != null ? fileName : docType;
	}

	private String getDocumentTypeName(Document document) {
		if (document.getDocTypeId() != null) {
			return document.getDocTypeId().getName(); // Assuming DocumentType has a 'name' field
		}
		return document.getFileType();
	}

	private String mapDocumentStatus(Document document) {
		if (document.isVerified()) {
			return "verified";
		}
		return document.getStatus() != null ? document.getStatus().name() : DocumentStatus.UPLOADED.name();
	}

	private String formatFileSize(Long bytes) {
		if (bytes == null)
			return "0 B";

		if (bytes < 1024) {
			return bytes + " B";
		} else if (bytes < 1024 * 1024) {
			return String.format("%.1f KB", bytes / 1024.0);
		} else {
			return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
		}
	}

	// Other helper methods remain similar but are now generic
	private String mapCheckStatus(CaseCheckStatus status) {
		if (status == null)
			return "pending";
		switch (status) {
		case COMPLETED:
			return "verified";
		case IN_PROGRESS:
			return "in_progress";
		case PENDING:
			return "pending";
		case ON_HOLD:
			return "on_hold";
		// case DELAYED: return "delayed";
		 case INSUFFICIENT: return "insufficient";
		default:
			return "pending";
		}
	}

	private CaseCheckStatus mapToCaseStatus(String status) {
		switch (status) {
		case "verified":
		case "completed":
			return CaseCheckStatus.COMPLETED;
		case "in_progress":
			return CaseCheckStatus.IN_PROGRESS;
		// case "pending": return CaseStatus.PENDING;
		// case "on_hold": return CaseStatus.ON_HOLD;
		// case "delayed": return CaseStatus.DELAYED;
		// case "insufficient": return CaseStatus.INSUFFICIENT;
		default:
			return CaseCheckStatus.IN_PROGRESS;
		}
	}

	

	private SlaInfoDTO getSlaInfo(VerificationCaseCheck check) {
		LocalDateTime assignedDate = check.getCreatedAt();
		LocalDateTime dueDate = assignedDate.plusDays(7); // 7-day SLA
		long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), dueDate);

		String slaStatus = "on_track";
		if (daysRemaining <= 0) {
			slaStatus = "breached";
		} else if (daysRemaining <= 2) {
			slaStatus = "at_risk";
		}

		return SlaInfoDTO.builder().assignedDate(assignedDate).dueDate(dueDate)
				.daysRemaining(daysRemaining > 0 ? daysRemaining : 0).status(slaStatus)
				.completedAt(check.getStatus() == CaseCheckStatus.COMPLETED ? check.getUpdatedAt() : null).build();
	}

	@Transactional(readOnly = true)
	public List<VendorNoteDTO> getVendorNotes(
	        Long checkId,
	        Long vendorId) {

	    VerificationCaseCheck check =
	            verificationCaseCheckRepository
	                    .findById(checkId)
	                    .orElseThrow(() ->
	                            new RuntimeException("Verification check not found"));
	    
	    boolean isVendorAdmin =
		        userServiceUtil.hasRole(
		                vendorId,
		                RoleConstants.ROLE_VENDOR_ADMINISTRATOR);

		if (!isVendorAdmin &&
		    (check.getAssignedVendorUser() == null ||
		     !Objects.equals(
		             check.getAssignedVendorUser().getUserId(),
		             vendorId))) {

		    throw new AccessDeniedException(
		            "Vendor not authorized to access this check");
		}

	    return vendorNoteRepository
	            .findByVerificationCaseCheck_CaseCheckIdOrderByCreatedAtDesc(checkId)
	            .stream()
	            .map(this::toDto)
	            .toList();
	}

	

	private void saveVerificationHistory(VerificationCaseCheck check, String fromStatus, String toStatus,
			Long performedById, String notes) {
		VerificationCheckHistory history = VerificationCheckHistory.builder().verificationCaseCheck(check)
				.action("status_update").fromStatus(fromStatus).toStatus(toStatus).performedBy("Vendor Agent") // Get
																												// from
																												// vendor
																												// details
				.performedById(performedById).timestamp(LocalDateTime.now()).notes(notes).build();

		historyRepository.save(history);
	}
	


	private String getCaseReference(VerificationCase verificationCase) {
		// Generate or fetch case reference
		// For now, use pattern CASE-{id}
		return "CASE-" + verificationCase.getCaseId();
	}

	private void saveTimelineEvent(VerificationCaseCheck check, String action, String description, String performedBy) {
		VerificationTimeline timeline = VerificationTimeline.builder().verificationCaseCheck(check).action(action)
				.description(description).performedBy(performedBy).timestamp(LocalDateTime.now()).build();

		timelineRepository.save(timeline);
	}

	private void updateParentCaseStatus(VerificationCase verificationCase) {
		// Check if all checks are completed
		List<VerificationCaseCheck> checks = verificationCase.getCaseChecks();
		boolean allCompleted = checks.stream().allMatch(check -> check.getStatus() == CaseCheckStatus.COMPLETED
				|| check.getStatus() == CaseCheckStatus.INSUFFICIENT);

		boolean anyInProgress = checks.stream().anyMatch(check -> check.getStatus() == CaseCheckStatus.IN_PROGRESS);

		if (allCompleted) {
			verificationCase.setStatus(CaseStatus.COMPLETED);
			verificationCase.setCompletedAt(LocalDateTime.now());
		} else if (anyInProgress) {
			verificationCase.setStatus(CaseStatus.IN_PROGRESS);
		}

		verificationCase.setUpdatedAt(LocalDateTime.now());
		verificationCaseRepository.save(verificationCase);
	}

	@Transactional
	public void addVendorNote(Long checkId, Long vendorId, String content, VendorNoteType  noteType) {
		log.info("Adding note to check {} by vendor {}", checkId, vendorId);

		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		// Verify vendor has access
		if (check.getAssignedVendorUser()==null || !check.getAssignedVendorUser().getUserId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized to add notes");
		}

		VendorNote note = VendorNote.builder()
				.verificationCaseCheck(check)
				.content(content)
				.createdBy("Vendor Agent") 
				.createdAt(LocalDateTime.now())
				.type(noteType)
				.isInternal(noteType.equals("internal"))
				.build();

		vendorNoteRepository.save(note);

		// Add timeline event
		saveTimelineEvent(check, "note_added", "Vendor added a note", "Vendor Agent");
	}

	/**
	 * Mark requirement as completed
	 */
	@Transactional
	public void updateRequirementStatus(Long checkId, Long vendorId, String requirementId, String status,
			String notes) {
		log.info("Updating requirement {} for check {} to {}", requirementId, checkId, status);

		// In a real implementation, you would have a Requirement entity
		// For now, we'll just update the check and add a note
		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		// Verify vendor has access
		if (check.getAssignedVendorUser()==null || !check.getAssignedVendorUser().getUserId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized to update requirements");
		}

		// Add a note about requirement update
		addVendorNote(checkId, vendorId,
				String.format("Requirement %s marked as %s. Notes: %s", requirementId, status, notes), VendorNoteType .VERIFICATION);

		// Add timeline event
		saveTimelineEvent(check, "requirement_updated",
				String.format("Requirement %s updated to %s", requirementId, status), "Vendor Agent");
	}

	/**
	 * Complete verification check
	 */
	@Transactional
	public void completeVerification(Long checkId, Long vendorId, String finalStatus, String summary) {
		log.info("Completing verification check {} by vendor {}", checkId, vendorId);

		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		// Verify vendor has access
		if (check.getAssignedVendorUser()==null || !check.getAssignedVendorUser().getUserId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized to complete verification");
		}

		// Update status
		CaseCheckStatus status = "verified".equals(finalStatus) ? CaseCheckStatus.COMPLETED
				: "insufficient".equals(finalStatus) ? CaseCheckStatus.INSUFFICIENT : CaseCheckStatus.IN_PROGRESS;

		check.setStatus(status);
		check.setUpdatedAt(LocalDateTime.now());

		// Add final note
		addVendorNote(checkId, vendorId,
				String.format("Verification completed. Final status: %s. Summary: %s", finalStatus, summary),
				VendorNoteType .VERIFICATION);

		// Add timeline event
		saveTimelineEvent(check, "verification_completed",
				String.format("Verification completed with status: %s", finalStatus), "Vendor Agent");

		verificationCaseCheckRepository.save(check);

		// Update parent case
		updateParentCaseStatus(check.getVerificationCase());
	}

	
	
	
	private List<ActionDTO> resolveCheckActions(CaseCheckStatus status) {

	    boolean restricted = switch (status) {
	        case 
	             VERIFIED,
	             REJECTED,
	             FAILED,
	             ESCALATED,
	             ACTION_REQUIRED -> true;
	        default -> false;
	    };

	    if (!restricted) {
	        return VendorActionCatalog.checkActions();
	    }

	    // 🔒 Read-only
	    return VendorActionCatalog.checkActions().stream()
	    		.map(action -> {
	                if (action.getCode() == ActionType.VIEW ||
	                    action.getCode() == ActionType.DOWNLOAD) {
	                    return action;
	                }

	                return ActionDTO.builder()
	                        .code(action.getCode())
	                        .label(action.getLabel())
	                        .level(action.getLevel())
	                        .enabled(false)
	                        .build();
	            })
	            .toList();
	}
	
	
	
	private List<ActionDTO> resolveObjectActions(CaseCheckStatus checkStatus) {

	    boolean restricted = checkStatus == CaseCheckStatus.ACTION_REQUIRED
	            || checkStatus == CaseCheckStatus.VERIFIED
	            || checkStatus == CaseCheckStatus.REJECTED;

	    if (!restricted) {
	        return VendorActionCatalog.objectActions();
	    }

	    return VendorActionCatalog.objectActions().stream()
	            .map(action -> {
	                if (action.getCode() == ActionType.VIEW ||
	                    action.getCode() == ActionType.DOWNLOAD) {
	                    return action;
	                }

	                return ActionDTO.builder()
	                        .code(action.getCode())
	                        .label(action.getLabel())
	                        .level(action.getLevel())
	                        .enabled(false)
	                        .build();
	            })
	            .toList();
	}

	
	private VendorNoteDTO toDto(VendorNote entity) {

	    if (entity == null) {
	        return null;
	    }

	    VendorNoteDTO dto = new VendorNoteDTO();

	    dto.setNoteId(entity.getNoteId());
	    dto.setContent(entity.getContent());
	    dto.setCreatedBy(entity.getCreatedBy());
	    dto.setCreatedAt(entity.getCreatedAt());
	    dto.setType(entity.getType());

	    dto.setVisibleToEmployer(entity.getVisibleToEmployer());
	    dto.setVisibleToCandidate(entity.getVisibleToCandidate());

	    dto.setInternal(entity.isInternal());

	    return dto;
	}
	
	
}
