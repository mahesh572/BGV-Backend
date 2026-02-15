package com.org.bgv.common;

public enum DocumentStatus {
	
	UPLOADED,           // Candidate uploaded the document
	RE_UPLOADED,
	IN_PROGRESS,        // Candidate uploading
    SUBMITTED,          // Candidate finished upload
    PENDING,            // Vendor review queue
    AWAITING_CANDIDATE, // Clarification needed
    RESUBMITTED,        // Candidate re-uploaded
    VERIFIED,           // Vendor approved
    REJECTED,           // Vendor rejected
    NONE,
    INSUFFICIENT,
    REQUEST_INFO,
    DELETED,            // Soft delete
	ACTION_REQUIRED,
	FAILED;
	
}
