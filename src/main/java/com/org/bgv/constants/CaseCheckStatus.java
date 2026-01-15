package com.org.bgv.constants;

public enum CaseCheckStatus {
	IN_PROGRESS,        // Candidate filling data / uploading
    SUBMITTED,          // Candidate submitted to vendor
    PENDING,            // Waiting for vendor verification
    AWAITING_CANDIDATE, // Vendor asked clarification
    RESUBMITTED,        // Candidate re-submitted
    COMPLETED,          // Vendor verified
    ON_HOLD,
    INSUFFICIENT,
    REJECTED,
    PENDING_CANDIDATE,
    FAILED,             // Vendor rejected
    INFO_REQUESTED,
    VERIFIED,
    REVERIFY_REQUIRED,
    ESCALATED;
}