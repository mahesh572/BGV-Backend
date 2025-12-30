package com.org.bgv.constants;

public enum SectionStatus {
    PENDING("pending"),
    IN_PROGRESS("in-progress"),
    COMPLETED("completed"),
    NOT_REQUIRED("not-required"),
    NOT_STARTED("not-started"),
    FAILED("failed"),
    VERIFIED("verified");
    
    private final String displayName;
    
    SectionStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    // Method to get enum from string value
    public static SectionStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return NOT_STARTED;
        }
        
        // Try exact match first (case-insensitive)
        for (SectionStatus status : SectionStatus.values()) {
            if (status.displayName.equalsIgnoreCase(text)) {
                return status;
            }
        }
        
        // Try matching enum name
        try {
            return SectionStatus.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle variations
            String normalized = text.toUpperCase().replace("-", "_");
            try {
                return SectionStatus.valueOf(normalized);
            } catch (IllegalArgumentException ex) {
                // Default to NOT_STARTED if no match found
                return NOT_STARTED;
            }
        }
    }
}