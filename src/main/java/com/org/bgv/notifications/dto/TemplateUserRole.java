package com.org.bgv.notifications.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TemplateUserRole {

    ADMINISTRATOR("Administrator"),
    COMPANY_ADMINISTRATOR("Company Administrator");

    private final String label;

    TemplateUserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Ensures JSON responses return the enum name
     * or label depending on what you want
     */
    @JsonValue
    public String getValue() {
        return this.label;
        // OR return label;  ← if frontend wants human-readable
    }
}

