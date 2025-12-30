package com.org.bgv.constants;


public enum CaseStatus {
	CREATED,
	ASSIGNED, 
    IN_PROGRESS, 
    UNDER_REVIEW, 
    COMPLETED, 
    CANCELLED,
    FAILED,
    REJECTED,
    AWAITING,
    VERIFIED,
    PENDING,
    ON_HOLD,
    DELAYED,
    INSUFFICIENT,
    REASSIGNED,
    SUBMITTED;
	
	
    
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