package com.org.bgv.dto;


import java.util.Arrays;

public enum CheckCategoryEnum {

    IDENTITY("Identity", "IDENTITY", "Identity"),
    EDUCATION("Education", "EDUCATION", "Education"),
    WORK("Work Experience", "WORK", "Professional / Work Experience"),
    ADDRESS("Address", "ADDRESS", "Address Verification"),
    COURT("Court", "COURT", "Court / Criminal Check"),
    OTHER("Other", "OTHER", "Other");

    private final String name;
    private final String code;
    private final String label;

    CheckCategoryEnum(String name, String code, String label) {
        this.name = name;
        this.code = code;
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    // Optional helper: get enum by code
    public static CheckCategoryEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid category code: " + code));
    }
}