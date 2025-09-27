package com.org.bgv.common;

public enum ProfileStatus {
    CREATED,          // Profile just created, not yet verified
    PENDING_REVIEW,   // Awaiting manual review or automated verification
    VERIFIED,         // Profile is verified and approved
    REJECTED,         // Profile failed verification / review
    INCOMPLETE,       // Profile is partially filled, needs more info
    DEACTIVATED,      // Profile is deactivated by admin or user
	SUBMITTED;

    // Optional: Add helper method to check "active" status
    public boolean isActive() {
        return this == VERIFIED;
    }
}