package com.org.bgv.enums;

public enum CompanyStatus {

    DRAFT("Draft"),

    PENDING_APPROVAL("Pending Approval"),

    ACTIVE("Active"),

    REJECTED("Rejected"),

    SUSPENDED("Suspended"),

    INACTIVE("Inactive"),

    CLOSED("Closed");

    private final String displayName;

    CompanyStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
