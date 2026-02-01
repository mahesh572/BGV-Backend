package com.org.bgv.company.dto;

public enum CompanySize {

    SIZE_1_10("1-10 employees"),
    SIZE_11_50("11-50 employees"),
    SIZE_51_200("51-200 employees"),
    SIZE_201_500("201-500 employees"),
    SIZE_501_1000("501-1000 employees"),
    SIZE_1000_PLUS("1000+ employees");

    private final String displayName;

    CompanySize(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

