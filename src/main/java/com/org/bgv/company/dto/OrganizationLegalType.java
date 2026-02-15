package com.org.bgv.company.dto;

public enum OrganizationLegalType {
	PRIVATE_LIMITED("Private Limited Company"),
    PUBLIC_LIMITED("Public Limited Company"),
    LLP("Limited Liability Partnership (LLP)"),
    PARTNERSHIP("Partnership Firm"),
    PROPRIETORSHIP("Sole Proprietorship"),
    OPC("One Person Company (OPC)"),
    NON_PROFIT("Non-Profit Organization"),
    GOVERNMENT("Government Organization");

    private final String displayName;

    OrganizationLegalType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
