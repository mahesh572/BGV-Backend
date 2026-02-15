package com.org.bgv.company.dto;

public enum OrganizationType {
	
    EMPLOYER("Employer"),
    VENDOR("Vendor"),
    UNIVERSITY("University"),
    PLATFORM("Platform"),
    OTHER("Other");
	
	
    private final String displayName;

    OrganizationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
