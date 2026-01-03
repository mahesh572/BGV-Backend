package com.org.bgv.common;

public enum DocumentStatus {
	    UPLOADED,        // Candidate uploaded the document
	    UNDER_REVIEW,    // Vendor started verification
	    VERIFIED,        // Vendor verified successfully
	    REJECTED,       // Vendor rejected the document
	    PENDING,
	    INSUFFICIENT,
	    DELETED;         // Soft-deleted (hidden from UI)
}
