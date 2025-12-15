package com.org.bgv.common;

public enum ActivityStatus {
    COMPLETED("completed"),
    IN_PROGRESS("in_progress"),
    PENDING("pending"),
    FAILED("failed");
    
    private final String value;
    
    ActivityStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static ActivityStatus fromValue(String value) {
        for (ActivityStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown activity status: " + value);
    }
}