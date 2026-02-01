package com.org.bgv.company.dto;

public enum IndustryType {

    TECHNOLOGY("Technology"),
    HEALTHCARE("Healthcare"),
    FINANCE("Finance"),
    EDUCATION("Education"),
    MANUFACTURING("Manufacturing"),
    RETAIL("Retail"),
    CONSTRUCTION("Construction"),
    TRANSPORTATION("Transportation"),
    ENERGY("Energy"),
    MEDIA_ENTERTAINMENT("Media & Entertainment"),
    REAL_ESTATE("Real Estate"),
    AGRICULTURE("Agriculture"),
    OTHER("Other");

    private final String displayName;

    IndustryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

