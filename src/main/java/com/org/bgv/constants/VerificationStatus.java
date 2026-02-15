package com.org.bgv.constants;


public enum VerificationStatus {
        
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    SUBMITTED("Submitted"),
    UNDER_REVIEW("Under Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
	UPLOADED("Uploaded"),
	VERIFIED("Verified"),
	ACTIVE("ACTIVE"),
    DELETED("DELETED"),
    CANDIDATE_PENDING("Candidate Pending"),  // Waiting for initial submission
    ACTION_REQUIRED("Action Required"), // Vendor reviewed and asked for clarification
	NOT_UPLOADED("Not Uploaded");
	
	
private final String displayName;
    
    VerificationStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
