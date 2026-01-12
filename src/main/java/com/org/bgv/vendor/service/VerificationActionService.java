package com.org.bgv.vendor.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.org.bgv.common.DocumentStatus;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.entity.Document;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.repository.DocumentRepository;
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
	/*
	@Transactional
	public Long createRequestInfoAction(VerificationActionRequest req) {

	    VerificationAction action = verificationActionRepository.save(
	        VerificationAction.builder()
	            .actionType(req.getActionType())
	            .actionLevel(req.getActionLevel())
	            .verificationCase(
	                verificationCaseRepository.getReferenceById(req.getCaseId())
	            )
	            .verificationCaseCheck(
	                verificationCaseCheckRepository.getReferenceById(req.getCheckId())
	            )
	            .reason(
	                actionReasonRepository.getReferenceById(req.getReasonId())
	            )
	            .remarks(req.getRemarks())
	            .candidateId(getCandidateId(req))
	            .status(ActionStatus.OPEN)
	            .performedBy(SecurityUtils.getCurrentUserId())
	            .performedAt(LocalDateTime.now())
	            .build()
	    );

	    // 🔗 HANDLE EVIDENCE
	    if (req.getEvidences() != null && !req.getEvidences().isEmpty()) {

	        for (EvidenceLinkRequest evReq : req.getEvidences()) {

	            // -------------------------------
	            // 1️⃣ VENDOR UPLOAD (already exists)
	            // -------------------------------
	            if (evReq.getSource() == EvidenceSource.VENDOR_UPLOAD) {

	                VerificationActionEvidence evidence =
	                        evidenceRepository.findById(evReq.getEvidenceId())
	                            .orElseThrow(() ->
	                                new IllegalArgumentException(
	                                    "Evidence not found: " + evReq.getEvidenceId()
	                                )
	                            );

	                evidence.setAction(action);
	            }

	            // --------------------------------
	            // 2️⃣ CANDIDATE DOCUMENT (create new)
	            // --------------------------------
	            else if (evReq.getSource() == EvidenceSource.CANDIDATE_DOCUMENT) {

	                VerificationActionEvidence evidence =
	                        VerificationActionEvidence.builder()
	                            .action(action)
	                            .source(EvidenceSource.CANDIDATE_DOCUMENT)
	                            .documentId(evReq.getDocumentId())
	                            .uploadedBy(SecurityUtils.getCurrentUserId())
	                            .build();

	                evidenceRepository.save(evidence);
	            }
	        }
	    }

	    return action.getId();
	}
*/
	
	@Transactional
	public Long createAction(VerificationActionRequest req) {

	    VerificationAction action = buildBaseAction(req);

	    verificationActionRepository.save(action);

	    linkEvidences(req, action);
	    
	    switch (req.getActionLevel()) {
      //  case CASE -> updateCaseStatus(req, action);
      //  case CHECK -> updateCheckStatus(req, action);
        case DOCUMENT -> updateDocumentStatus(req, action);
      //  case OBJECT -> updateObjectStatus(req, action);
      }
	    
	   // updateCheckStatus(req, action);
	    
	    emailService.sendCandidateActionRequiredEmail(req.getCaseId(), req.getCheckId(), req.getActionType(), action.getReason().getLabel(), req.getRemarks());
	    
	    
	  //  applyActionSideEffects(req, action); // 👈 optional hooks

	    return action.getId();
	}

	private VerificationAction buildBaseAction(VerificationActionRequest req) {

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
	            .status(ActionStatus.OPEN)
	            .performedBy(SecurityUtils.getCurrentUserId())
	            .performedAt(LocalDateTime.now())
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

	private void updateDocumentStatus(
	        VerificationActionRequest req,
	        VerificationAction action
	) {
	    if (req.getDocumentId() == null) {
	        throw new IllegalArgumentException("DocumentId is required for DOCUMENT level action");
	    }

	    Document document = documentRepository.findById(req.getDocumentId())
	            .orElseThrow(() ->
	                    new EntityNotFoundException("Document not found: " + req.getDocumentId()));

	    DocumentStatus newStatus = resolveDocumentStatus(req.getActionType());

	    document.setStatus(newStatus);
	    document.setLastAction(action);
	    document.setUpdatedAt(LocalDateTime.now());

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


}
