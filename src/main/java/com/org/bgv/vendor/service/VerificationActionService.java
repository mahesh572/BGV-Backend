package com.org.bgv.vendor.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.org.bgv.config.SecurityUtils;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.repository.VerificationCaseRepository;
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
	public Long createRequestInfoAction(VerificationActionRequest req) {

	    VerificationAction action = verificationActionRepository.save(
	        VerificationAction.builder()
	            .actionType(req.getActionType())
	            .actionLevel(req.getActionLevel())
	            .verificationCase(verificationCaseRepository.getReferenceById(req.getCaseId()))
	            .verificationCaseCheck(verificationCaseCheckRepository.getReferenceById(req.getCheckId()))
	            .reason(actionReasonRepository.getReferenceById(req.getReasonId()))
	            .remarks(req.getRemarks())
	            .candidateId(getCandidateId(req))
	            .status(ActionStatus.OPEN)
	            .performedBy(SecurityUtils.getCurrentUserId())
	            .performedAt(LocalDateTime.now())
	            .build()
	    );

	    // 🔗 LINK EVIDENCE
	    if (req.getEvidences() != null) {
	        List<Long> evidenceIds = req.getEvidences()
	                .stream()
	                .map(EvidenceLinkRequest::getEvidenceId)
	                .toList();

	        List<VerificationActionEvidence> evidences =
	        		evidenceRepository.findAllById(evidenceIds);

	        evidences.forEach(ev -> ev.setAction(action));
	    }

	    return action.getId();
	}

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

}
