package com.org.bgv.company.dto;

public enum CompanyType {

	EMPLOYER("Employer"),
    VENDOR("Vendor"),
    UNIVERSITY("University"),
    OTHER("Other"),
	DEFAULT("default");
	
	
    private final String displayName;

    CompanyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
	
}
