package com.org.bgv.vendor.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.repository.VerificationCaseRepository;
import com.org.bgv.vendor.dto.CreateRejectionRequest;
import com.org.bgv.vendor.dto.RejectionLevel;
import com.org.bgv.vendor.dto.RejectionResponseDTO;
import com.org.bgv.vendor.dto.RejectionStatus;
import com.org.bgv.vendor.entity.RejectionReason;
import com.org.bgv.vendor.entity.RejectionReasonRepository;
import com.org.bgv.vendor.entity.VerificationRejection;
import com.org.bgv.vendor.repository.VerificationRejectionRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@Data
@RequiredArgsConstructor
public class RejectionService {
	
	private final VerificationCaseRepository verificationCaseRepository;
	private final VerificationCaseCheckRepository verificationCaseCheckRepository;
	private final RejectionReasonRepository rejectionReasonRepository;
	private final VerificationRejectionRepository verificationRejectionRepository;
	
	@Transactional
	public void reject(CreateRejectionRequest req, Long vendorUserId) {

	    VerificationCase verificationCase =
	    		verificationCaseRepository.findById(req.getCaseId())
	                    .orElseThrow(() -> new RuntimeException("Case not found"));

	    VerificationCaseCheck check = null;
	    if (req.getLevel() != RejectionLevel.CASE) {
	        check = verificationCaseCheckRepository.findById(req.getCaseCheckId())
	                .orElseThrow(() -> new RuntimeException("Check not found"));
	    }

	    RejectionReason reason =
	            rejectionReasonRepository.findById(req.getReasonId())
	                    .orElseThrow(() -> new RuntimeException("Reason not found"));

	    VerificationRejection rejection = VerificationRejection.builder()
	            .verificationCase(verificationCase)
	            .verificationCaseCheck(check)
	            .level(req.getLevel())
	            .objectId(req.getObjectId())
	            .documentId(req.getDocumentId())
	            .reason(reason)
	            .remarks(req.getRemarks())
	            .status(RejectionStatus.REJECTED)
	            .rejectedBy(vendorUserId)
	            .rejectedAt(LocalDateTime.now())
	            .build();

	    validateRejection(rejection);

	    verificationRejectionRepository.save(rejection);

	    // 🔁 Update check status
	    if (check != null) {
	        check.setStatus(CaseStatus.REJECTED);
	    }
	}
	
	@Transactional
	public List<RejectionResponseDTO> getRejections(Long caseCheckId) {

	    return verificationRejectionRepository
	            .findByVerificationCaseCheck_CaseCheckId(caseCheckId)
	            .stream()
	            .map(this::mapToDto)
	            .toList();
	}

	@Transactional
	public void resolve(Long rejectionId, Long userId) {

	    VerificationRejection rejection =
	    		verificationRejectionRepository.findById(rejectionId)
	                    .orElseThrow(() -> new RuntimeException("Rejection not found"));

	    rejection.setStatus(RejectionStatus.RESOLVED);
	    rejection.setResolvedAt(LocalDateTime.now());
	    rejection.setResolvedBy(userId);

	    // If no active rejections left → mark check back to IN_PROGRESS
	    VerificationCaseCheck check = rejection.getVerificationCaseCheck();
	    if (check != null) {
	        boolean hasActive =
	        		verificationRejectionRepository.existsByVerificationCaseCheckAndStatus(
	                        check, RejectionStatus.REJECTED);

	        if (!hasActive) {
	            check.setStatus(CaseStatus.IN_PROGRESS);
	        }
	    }
	}
	
 // Rejection Block....
    
    private void validateRejection(VerificationRejection r) {

        switch (r.getLevel()) {

            case CASE -> {
                if (r.getVerificationCaseCheck() != null ||
                    r.getObjectId() != null ||
                    r.getDocumentId() != null) {
                    throw new IllegalStateException("CASE rejection must not have target ids");
                }
            }

            case SECTION -> {
                if (r.getVerificationCaseCheck() == null ||
                    r.getObjectId() != null ||
                    r.getDocumentId() != null) {
                    throw new IllegalStateException("SECTION rejection invalid");
                }
            }

            case OBJECT -> {
                if (r.getObjectId() == null ||
                    r.getDocumentId() != null) {
                    throw new IllegalStateException("OBJECT rejection invalid");
                }
            }

            case DOCUMENT -> {
                if (r.getDocumentId() == null) {
                    throw new IllegalStateException("DOCUMENT rejection invalid");
                }
            }
        }
    }

    private RejectionResponseDTO mapToDto(VerificationRejection rejection) {

        return RejectionResponseDTO.builder()
                .id(rejection.getId())
                .level(rejection.getLevel())

                // Scope
                .objectId(rejection.getObjectId())
                .documentId(rejection.getDocumentId())

                // Reason
                .reasonCode(
                        rejection.getReason() != null
                                ? rejection.getReason().getCode()
                                : null
                )
                .reasonLabel(
                        rejection.getReason() != null
                                ? rejection.getReason().getLabel()
                                : null
                )

                // Remarks
                .remarks(rejection.getRemarks())

                // Status
                .status(rejection.getStatus())

                // Audit
                .rejectedBy(String.valueOf(rejection.getRejectedBy()))
                .rejectedAt(rejection.getRejectedAt())

                .resolvedAt(rejection.getResolvedAt())

                .build();
    }

	
}
