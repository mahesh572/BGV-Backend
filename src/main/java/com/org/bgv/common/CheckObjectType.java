package com.org.bgv.common;

public enum CheckObjectType {

    IDENTITY("Identity"),
    EDUCATION("Education"),
    ADDRESS("Address"),
    WORK_EXPERIENCE("Work Experience");

    private final String label;

    CheckObjectType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

