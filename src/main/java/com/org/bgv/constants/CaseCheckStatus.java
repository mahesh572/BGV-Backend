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
    ACTION_REQUIRED,
    ESCALATED;
	
//	CANDIDATE_PENDING("Candidate Pending"),  // Waiting for initial submission
 //   ACTION_REQUIRED("Action Required"), // Vendor reviewed and asked for clarification
	
	
	
	// Candidate phase
   // CANDIDATE_PENDING,   // Candidate filling details
  //  SUBMITTED,           // Submitted for review

    // Vendor phase
  //  IN_PROGRESS,         // Vendor verifying

    // Back to candidate
  //  ACTION_REQUIRED,     // Open verification action exists

 //   // Terminal states
 //   VERIFIED,            // Successfully verified
 //   FAILED,              // Rejected by vendor

    // Exceptional
 //   ON_HOLD,
  //  ESCALATED
}