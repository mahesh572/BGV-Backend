package com.org.bgv.constants;


public enum CaseStatus {
	CREATED,        // Case created, no candidate action yet
	INITIATED,     // Case INITIATED, no candidate action yet both same
	SUBMITTED,
    IN_PROGRESS,    // Candidate or Vendor working
    COMPLETED,      // All checks verified
    PARTIAL,        // Some checks failed
    FAILED,         // Critical failure
    CANCELLED; 
	
	
    
    public static CaseStatus fromString(String value) {
	    if (value == null || value.trim().isEmpty()) {
	        return null;
	    }

	    try {
	        return CaseStatus.valueOf(value.trim().toUpperCase());
	    } catch (IllegalArgumentException ex) {
	        throw new IllegalArgumentException("Invalid CaseStatus: " + value);
	    }
	}

}