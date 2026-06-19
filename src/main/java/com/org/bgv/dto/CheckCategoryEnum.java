package com.org.bgv.dto;

import java.util.Arrays;

public enum CheckCategoryEnum {

    IDENTITY("Identity", "IDENTITY", "Identity Verification"),

    EDUCATION("Education", "EDUCATION", "Education Verification"),

    WORK_EXPERIENCE("Work Experience", "WORK EXPERIENCE", "Employment Verification"),
    
    EMPLOYEMENT("Employement", "EMPLOYEMENT", "Employment Verification"),

    ADDRESS("Address", "ADDRESS", "Address Verification"),

    REFERENCE("Reference", "REFERENCE", "Reference Check"),

    COURT("Court", "COURT", "Court / Criminal Check"),

    DATABASE("Database", "DATABASE", "Database Verification"),

    CREDIT("Credit", "CREDIT", "Credit Verification"),

    GLOBAL("Global", "GLOBAL", "Global Database Check"),

    DRUG("Drug", "DRUG", "Drug Test Verification"),

    OTHER("Other", "OTHER", "Other Verification");

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

    public static CheckCategoryEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid category code: " + code));
    }
    
    public static CheckCategoryEnum fromName(String name) {

        return Arrays.stream(values())
                .filter(e -> e.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid category name: " + name));
    }
}