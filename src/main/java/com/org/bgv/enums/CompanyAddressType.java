package com.org.bgv.enums;

public enum CompanyAddressType {
	REGISTERED("Registered Office"),
	PRIMARY("Primary Address"),
	CORRESPONDENCE("Correspondence Address"),
	BILLING("Billing Address");
	
	private final String displayName;

	CompanyAddressType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
