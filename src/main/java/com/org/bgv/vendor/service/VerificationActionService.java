package com.org.bgv.vendor.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.Document;
import com.org.bgv.entity.User;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.notifications.service.NotificationDispatcher;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.DocumentRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.repository.VerificationCaseRepository;
import com.org.bgv.service.EmailService;
import com.org.bgv.vendor.action.dto.EvidenceLinkRequest;
import com.org.bgv.vendor.action.dto.VerificationActionRequest;
import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionReasonDTO;
import com.org.bgv.vendor.dto.ActionStatus;
import com.org.bgv.vendor.dto.ActionType;
import com.org.bgv.vendor.dto.EvidenceSource;
import com.org.bgv.vendor.entity.ActionReason;
import com.org.bgv.vendor.entity.VerificationAction;
import com.org.bgv.vendor.entity.VerificationActionEvidence;
import com.org.bgv.vendor.evidence.dto.EvidenceUploadRequest;
import com.org.bgv.vendor.evidence.dto.EvidenceUploadResponse;
import com.org.bgv.vendor.repository.ActionReasonRepository;
import com.org.bgv.vendor.repository.VerificationActionEvidenceRepository;
import com.org.bgv.vendor.repository.VerificationActionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationActionService {
	
	private final ActionReasonRepository actionReasonRepository;
	private final VerificationActionEvidenceRepository evidenceRepository;
	private final VerificationActionRepository verificationActionRepository;
	private final VerificationCaseRepository verificationCaseRepository;
	private final VerificationCaseCheckRepository verificationCaseCheckRepository;
	private final EmailService emailService;
	private final DocumentRepository documentRepository;
	private final CaseCheckStatusService caseCheckStatusService;
	private final CheckSyncService checkSyncService;
	private final CandidateRepository candidateRepository;
	 private final UserRepository userRepository;
	 private final NotificationDispatcher notificationDispatcher;
	 private final CompanyRepository companyRepository;
	
	public List<ActionReasonDTO> getReasons(
	        Long categoryId,
	        ActionType actionType,
	        ActionLevel level) {

	    List<ActionReason> reasons =
	        actionReasonRepository.findApplicableReasons(
	            actionType,
	            level,
	            categoryId
	        );

	    return reasons.stream()
	        .map(this::toDto)
	        .toList();
	}

	private ActionReasonDTO toDto(ActionReason reason) {
	    return ActionReasonDTO.builder()
	        .id(reason.getActionReasonId())
	        .code(reason.getCode())
	        .label(reason.getLabel())
	        .description(reason.getDescription())
	        .requiresEvidence(reason.getRequiresEvidence())
	        .requiresRemarks(reason.getRequiresRemarks())
	        .terminal(reason.getTerminal())
	        .sortOrder(reason.getSortOrder())
	        .build();
	}
	
	
	public EvidenceUploadResponse createUploadedEvidence(EvidenceUploadRequest request) {
        // Create evidence entity
        VerificationActionEvidence evidence = VerificationActionEvidence.builder()
            .source(EvidenceSource.VENDOR_UPLOAD)
            .fileName(request.getFileName())
            .originalFileName(request.getOriginalFileName())
            .fileUrl(request.getFileUrl())
            .fileType(request.getFileType())
            .fileSize(request.getFileSize())
            .storageKey(request.getStorageKey())
            .uploadedBy(request.getUploadedBy())
            .uploadedAt(LocalDateTime.now())
            
            .archived(false)
            .build();
        
        // Note: action is not set yet - it will be linked when the action is created
        // documentId is also null for vendor uploads
        
        // Save evidence
        VerificationActionEvidence savedEvidence = evidenceRepository.save(evidence);
        
        log.info("Evidence uploaded: ID={}, File={}, Size={} bytes", 
            savedEvidence.getId(), request.getFileName(), request.getFileSize());
        
        return EvidenceUploadResponse.builder()
            .evidenceId(savedEvidence.getId())
            .fileKey(savedEvidence.getStorageKey())
            .documentId(savedEvidence.getId().toString()) // Use evidence ID as documentId for frontend
            .fileName(savedEvidence.getFileName())
            .fileSize(savedEvidence.getFileSize())
            .fileType(savedEvidence.getFileType())
            .fileUrl(savedEvidence.getFileUrl())
            .uploadedAt(savedEvidence.getUploadedAt())
            .build();
    }
    
	public void linkEvidenceToAction(Long evidenceId, Long actionId) {
        VerificationActionEvidence evidence = evidenceRepository.findById(evidenceId)
            .orElseThrow(() -> new EntityNotFoundException("Evidence not found: " + evidenceId));
        
        VerificationAction action = verificationActionRepository.findById(actionId)
            .orElseThrow(() -> new EntityNotFoundException("Action not found: " + actionId));
        
        evidence.setAction(action);
        evidenceRepository.save(evidence);
        
        log.info("Evidence {} linked to action {}", evidenceId, actionId);
    }
	
	
	@Transactional
	public Long createAction(VerificationActionRequest req) {

	    VerificationAction action = buildBaseAction(req);
	    action = verificationActionRepository.save(action);

	    linkEvidences(req, action);

	    boolean documentChanged = false;

	    switch (req.getActionLevel()) {

	        case DOCUMENT -> {
	            updateDocumentStatus(req, action);
	            documentChanged = true; // ✅ important
	        }

	        case SECTION -> {
	            handleSectionAction(req, action); // only side effects
	        }
	    }

	    // ✅ ONLY recalc when document changed
	    if (documentChanged) {
	        recalculateAndUpdateCheckStatus(req.getCheckId(), action);
	    }

	    return action.getId();
	}
	
	private void updateDocumentStatus(
	        VerificationActionRequest req,
	        VerificationAction action
	) {

	    if (req.getDocumentId() == null) {
	        throw new IllegalArgumentException(
	                "DocumentId is required for DOCUMENT level action"
	        );
	    }

	    Document document = documentRepository.findById(req.getDocumentId())
	            .orElseThrow(() -> new EntityNotFoundException(
	                    "Document not found: " + req.getDocumentId()));

	    DocumentStatus newStatus = resolveDocumentStatus(
	            document.getStatus(), // ✅ pass current state (future state machine ready)
	            req.getActionType()
	    );

	    document.setStatus(newStatus);
	    document.setLastAction(action);
	    document.setUpdatedAt(LocalDateTime.now());
	}
	
	private void handleSectionAction(
	        VerificationActionRequest req,
	        VerificationAction action
	) {

	    if (req.getActionType() == ActionType.REQUEST_INFO
	            || req.getActionType() == ActionType.INSUFFICIENT) {

	        try {
	            Candidate candidate = candidateRepository
	                    .findById(action.getCandidateId())
	                    .orElseThrow();

	            User user = userRepository
	                    .findById(candidate.getUser().getUserId())
	                    .orElseThrow();

	            VerificationCaseCheck check =
	                    verificationCaseCheckRepository.getReferenceById(req.getCheckId());

	            Company company = companyRepository
	                    .findById(check.getVerificationCase().getCompanyId())
	                    .orElseThrow();

	            int insufficientCount = (int) documentRepository
	                    .findByVerificationCaseCheck_CaseCheckId(check.getCaseCheckId())
	                    .stream()
	                    .filter(doc ->
	                            doc.getStatus() == DocumentStatus.INSUFFICIENT
	                                    || doc.getStatus() == DocumentStatus.REQUEST_INFO)
	                    .count();

	            notificationDispatcher.dispatchVerificationCheckActionRequired(
	                    company,
	                    candidate,
	                    user,
	                    check.getCategory().getName(),
	                    check.getVerificationCase().getCaseId(),
	                    check.getCaseCheckId(),
	                    insufficientCount,
	                    action.getRemarks()
	            );

	        } catch (Exception e) {
	            log.error("Error sending section action notification", e);
	        }
	    }
	}
	
	
	private void updateCheckStatus(VerificationActionRequest req,
	        VerificationAction action) {
		
		if (req.getActionLevel() == ActionLevel.SECTION
		        && (req.getActionType() == ActionType.REQUEST_INFO
		        || req.getActionType() == ActionType.INSUFFICIENT)) {

			// 🔔 SEND NOTIFICATION (ONLY ONCE PER CHECK)
		    try {
		        Candidate candidate = candidateRepository
		                .findById(action.getCandidateId())
		                .orElseThrow();

		        User user = userRepository
		                .findById(candidate.getUser().getUserId())
		                .orElseThrow();
		        
		        VerificationCaseCheck check =
			            verificationCaseCheckRepository.getReferenceById(req.getCheckId());
		        
		        Long companyId = check.getVerificationCase().getCompanyId();
		        
		        Company company = companyRepository.findById(companyId)
		                .orElseThrow(() -> new IllegalArgumentException("Company not found"));


		        // 🔹 Count insufficient documents
		        int insufficientCount = (int) documentRepository
		                .findByVerificationCaseCheck_CaseCheckId(check.getCaseCheckId())
		                .stream()
		                .filter(doc -> doc.getStatus() == DocumentStatus.INSUFFICIENT
		                        || doc.getStatus() == DocumentStatus.REQUEST_INFO)
		                .count();

		        notificationDispatcher.dispatchVerificationCheckActionRequired(
		                company,
		                candidate,
		                user,
		                check.getCategory().getName(), // check name
		                check.getVerificationCase().getCaseId(),
		                check.getCaseCheckId(),
		                insufficientCount,
		                action.getRemarks()
		        );
		    }catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private VerificationAction buildBaseAction(VerificationActionRequest req) {
		
		ActionStatus status = req.getActionType() == ActionType.VERIFY
		        ? ActionStatus.RESOLVED
		        : ActionStatus.OPEN;

	    return VerificationAction.builder()
	            .actionType(req.getActionType())
	            .actionLevel(req.getActionLevel())
	            .verificationCase(
	                verificationCaseRepository.getReferenceById(req.getCaseId())
	            )
	            .verificationCaseCheck(
	                verificationCaseCheckRepository.getReferenceById(req.getCheckId())
	            )
	            .reason(
	                req.getReasonId() != null
	                    ? actionReasonRepository.getReferenceById(req.getReasonId())
	                    : null
	            )
	            .remarks(req.getRemarks())
	            .candidateId(getCandidateId(req))
	            .status(status)
	            .performedBy(SecurityUtils.getCurrentUserId())
	            .performedAt(LocalDateTime.now())
	            .documentId(req.getDocumentId())
	            .objectId(req.getObjectId())
	            .build();
	}

	
	private void linkEvidences(
	        VerificationActionRequest req,
	        VerificationAction action
	) {

	    if (req.getEvidences() == null) return;

	    for (EvidenceLinkRequest evReq : req.getEvidences()) {

	        switch (evReq.getSource()) {

	            case VENDOR_UPLOAD -> {
	                VerificationActionEvidence evidence =
	                        evidenceRepository.findById(evReq.getEvidenceId())
	                            .orElseThrow(() ->
	                                new IllegalArgumentException(
	                                    "Evidence not found: " + evReq.getEvidenceId()
	                                )
	                            );
	                evidence.setAction(action);
	            }

	            case CANDIDATE_DOCUMENT -> {
	                evidenceRepository.save(
	                        VerificationActionEvidence.builder()
	                            .action(action)
	                            .source(EvidenceSource.CANDIDATE_DOCUMENT)
	                            .documentId(evReq.getDocumentId())
	                            .uploadedBy(SecurityUtils.getCurrentUserId())
	                            .build()
	                );
	            }
	        }
	    }
	}
/*
	private void applyActionSideEffects(
	        VerificationActionRequest req,
	        VerificationAction action
	) {

	    switch (req.getActionType()) {

	        case REQUEST_INFO -> {
	            // notify candidate
	        }

	        case REJECT -> {
	            closeCheck(action);
	        }

	        case APPROVE -> {
	            markCheckVerified(action);
	        }
	    }
	}
*/
	private Long getCandidateId(VerificationActionRequest req) {

	    if (req.getCaseId() == null) {
	        throw new IllegalArgumentException("caseId is mandatory to derive candidateId");
	    }

	    VerificationCase verificationCase = verificationCaseRepository
	            .findById(req.getCaseId())
	            .orElseThrow(() ->
	                    new EntityNotFoundException("Verification case not found: " + req.getCaseId())
	            );

	    if (verificationCase.getCandidateId() == null) {
	        throw new IllegalStateException("Case is not linked to a candidate");
	    }

	    return verificationCase.getCandidateId();
	}
	
	/*
	private CaseCheckStatus resolveCheckStatus(ActionType actionType,CaseCheckStatus currentStatus) {

	    return switch (actionType) {

	    case REQUEST_INFO -> CaseCheckStatus.INFO_REQUESTED;

        case INSUFFICIENT -> CaseCheckStatus.INSUFFICIENT;

        case VERIFY, APPROVE -> CaseCheckStatus.VERIFIED;

        case REJECT -> CaseCheckStatus.REJECTED;

        case FAIL -> CaseCheckStatus.FAILED;

        case REVERIFY -> CaseCheckStatus.REVERIFY_REQUIRED;

        case ESCALATE -> CaseCheckStatus.ESCALATED;

        // UI / non-status actions
        case VIEW, DOWNLOAD -> currentStatus;
	    };
	}
	*/
	
/*
	private void updateCheckStatus(
	        VerificationActionRequest req,
	        VerificationAction action
	) {

	    VerificationCaseCheck check =
	            verificationCaseCheckRepository.getReferenceById(req.getCheckId());

	    CaseCheckStatus newStatus = resolveCheckStatus(req.getActionType(),check.getStatus());

	    check.setStatus(newStatus);
	    check.setLastAction(action);
	    check.setUpdatedAt(LocalDateTime.now());
	}
*/
	private DocumentStatus resolveDocumentStatus(
	        DocumentStatus current,
	        ActionType actionType
	) {

	    return switch (current) {
/*
	        case PENDING -> switch (actionType) {
	            case REQUEST_INFO -> DocumentStatus.REQUEST_INFO;
	            case INSUFFICIENT -> DocumentStatus.INSUFFICIENT;
	            case VERIFY, APPROVE -> DocumentStatus.VERIFIED;
	            case REJECT -> DocumentStatus.REJECTED;
	            default -> throw invalid(actionType, current);
	        };
*/
	    case UPLOADED -> switch (actionType) {
        case REQUEST_INFO -> DocumentStatus.REQUEST_INFO;
        case INSUFFICIENT -> DocumentStatus.INSUFFICIENT;
        case VERIFY, APPROVE -> DocumentStatus.VERIFIED;
        case REJECT -> DocumentStatus.REJECTED;
        default -> throw invalid(actionType, current);
    };
	        case REQUEST_INFO, INSUFFICIENT -> switch (actionType) {
	            case VERIFY -> DocumentStatus.VERIFIED;
	            case REJECT -> DocumentStatus.REJECTED;
	            default -> throw invalid(actionType, current);
	        };

	        case VERIFIED, REJECTED -> throw new IllegalStateException("Final state reached: " + current);
		    default -> throw new IllegalArgumentException("Unexpected value: " + current);
	    };
	}
	private RuntimeException invalid(ActionType action, DocumentStatus state) {
	    return new IllegalStateException(
	            "Invalid action " + action + " for state " + state
	    );
	}

	
	// this below status map for candidate
	private DocumentStatus resolveDocumentStatus(ActionType actionType) {

	    return switch (actionType) {

	        case REQUEST_INFO -> DocumentStatus.REQUEST_INFO;

	        case INSUFFICIENT -> DocumentStatus.INSUFFICIENT;

	        case REJECT -> DocumentStatus.REJECTED;

	        case VERIFY, APPROVE -> DocumentStatus.VERIFIED;

	        default -> throw new IllegalStateException(
	                "ActionType " + actionType + " not supported for document"
	        );
	    };
	}
	private void recalculateAndUpdateCheckStatus(
	        Long checkId,
	        VerificationAction action
	) {

	    VerificationCaseCheck check =
	            verificationCaseCheckRepository.getReferenceById(checkId);

	    CaseCheckStatus oldStatus = check.getStatus();

	    CaseCheckStatus newStatus =
	            caseCheckStatusService.recalculateCheckStatus(checkId);

	    check.setStatus(newStatus);
	    check.setLastAction(action);
	    check.setUpdatedAt(LocalDateTime.now());

	    verificationCaseCheckRepository.save(check);

	    // ✅ trigger ONLY when status changes to ACTION_REQUIRED
	    if (!CaseCheckStatus.ACTION_REQUIRED.equals(oldStatus)
	            && CaseCheckStatus.ACTION_REQUIRED.equals(newStatus)) {

	        triggerActionRequiredSync(check, action);
	    }
	}

	private void triggerActionRequiredSync(
	        VerificationCaseCheck check,
	        VerificationAction action
	) {
	    try {
	        checkSyncService.markSectionActionRequired(
	                action.getCandidateId(),
	                check.getVerificationCase().getCaseId(),
	                check.getCategory().getName()
	        );
	    } catch (JsonProcessingException e) {
	        log.error("Error syncing ACTION_REQUIRED state", e);
	    }
	}
	
	private CaseCheckStatus resolveCheckStatusFromDocuments(List<Document> documents) {

	    if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.REJECTED)) {
	        return CaseCheckStatus.REJECTED;
	    }

	    if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.FAILED)) {
	        return CaseCheckStatus.FAILED;
	    }

	    if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.REQUEST_INFO)) {
	        return CaseCheckStatus.INFO_REQUESTED;
	    }

	    if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.INSUFFICIENT)) {
	        return CaseCheckStatus.INSUFFICIENT;
	    }

	    if (documents.stream().allMatch(d -> d.getStatus() == DocumentStatus.VERIFIED)) {
	        return CaseCheckStatus.VERIFIED;
	    }

	    return CaseCheckStatus.IN_PROGRESS;
	}

}
