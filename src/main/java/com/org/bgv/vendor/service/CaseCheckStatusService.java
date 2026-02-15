package com.org.bgv.vendor.service;

import org.springframework.stereotype.Service;

import com.org.bgv.common.DocumentStatus;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.repository.VerificationCaseDocumentRepository;
import com.org.bgv.vendor.dto.ActionStatus;
import com.org.bgv.vendor.dto.ActionType;
import com.org.bgv.vendor.repository.VerificationActionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CaseCheckStatusService {

    private final VerificationActionRepository actionRepository;
    private final VerificationCaseCheckRepository checkRepository;
    private final VerificationCaseDocumentRepository documentRepository;

    @Transactional
    public CaseCheckStatus recalculateCheckStatus(Long checkId) {

        VerificationCaseCheck check = checkRepository.findById(checkId)
                .orElseThrow(() -> new IllegalArgumentException("Check not found"));

        // 1️⃣ Terminal failure first
        if (isFailed(checkId)) {
            check.setStatus(CaseCheckStatus.FAILED);
            return CaseCheckStatus.FAILED;
        }

        // 2️⃣ Open action override (Candidate ownership)
        if (existsOpenAction(checkId)) {
            check.setStatus(CaseCheckStatus.ACTION_REQUIRED);
            return CaseCheckStatus.ACTION_REQUIRED;
        }

        // 3️⃣ All required docs verified
        if (allRequiredDocumentsVerified(checkId)) {
            check.setStatus(CaseCheckStatus.VERIFIED);
            return CaseCheckStatus.VERIFIED;
        }

        // 4️⃣ Vendor assigned and working
        if (isVendorAssigned(check)) {
            check.setStatus(CaseCheckStatus.IN_PROGRESS);
            return CaseCheckStatus.IN_PROGRESS;
        }

        // 5️⃣ Candidate submitted but vendor not started
        if (isSubmitted(check)) {
            check.setStatus(CaseCheckStatus.SUBMITTED);
            return CaseCheckStatus.SUBMITTED;
        }

        // 6️⃣ Default: candidate still filling
        check.setStatus(CaseCheckStatus.PENDING_CANDIDATE);
        return CaseCheckStatus.PENDING_CANDIDATE;
    }

    // -------------------------------------------------
    // Helper Methods
    // -------------------------------------------------

    private boolean existsOpenAction(Long checkId) {
        return actionRepository.existsByVerificationCaseCheckCaseCheckIdAndStatus(
                checkId,
                ActionStatus.OPEN
        );
    }

    private boolean isFailed(Long checkId) {
        return actionRepository.existsByVerificationCaseCheckCaseCheckIdAndActionTypeAndStatus(
                checkId,
                ActionType.REJECT,
                ActionStatus.RESOLVED
        );
    }

    private boolean allRequiredDocumentsVerified(Long checkId) {

        long requiredCount = documentRepository.countByVerificationCaseCheckCaseCheckIdAndRequiredTrue(checkId);

        long verifiedCount = documentRepository
                .countByVerificationCaseCheckCaseCheckIdAndRequiredTrueAndVerificationStatus(
                        checkId,
                        DocumentStatus.VERIFIED
                );

        return requiredCount > 0 && requiredCount == verifiedCount;
    }

    private boolean isVendorAssigned(VerificationCaseCheck check) {
        return check.getVendorId() != null;
    }

    private boolean isSubmitted(VerificationCaseCheck check) {
        return check.getStatus() == CaseCheckStatus.SUBMITTED;
    }
}
