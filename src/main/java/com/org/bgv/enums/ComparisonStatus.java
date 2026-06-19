package com.org.bgv.enums;

public enum ComparisonStatus {

    PENDING("Pending", "warning"),
    MATCH("Match", "success"),
    MISMATCH("Mismatch", "error"),
    NOT_AVAILABLE("Not Available", "default"),
    NOT_APPLICABLE("Not Applicable", "default"),
    MANUAL_REVIEW("Manual Review", "info");

    private final String label;
    private final String color;

    ComparisonStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }
}