package com.org.bgv.constants;

public enum SectionStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    VERIFIED("Verified"),
    REJECTED("Rejected"),
    PENDING_REVIEW("Pending Review"),
    NOT_REQUIRED("Not Required");
    
    private final String displayName;
    
    SectionStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}