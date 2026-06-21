package com.org.bgv.onboarding.dto;

public enum ContactType {

    PRIMARY("Primary"),
    HR("HR"),
    FINANCE("Finance"),
    LEGAL("Legal"),
    TECHNICAL("Technical"),
    BILLING("Billing"),
    OTHER("Other");

    private final String displayName;

    ContactType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}