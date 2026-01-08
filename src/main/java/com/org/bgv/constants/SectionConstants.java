package com.org.bgv.constants;

public enum SectionConstants {

    BASIC_DETAILS(1, "Basic Details"),
    IDENTITY(2, "Identity"),
    EDUCATION(3, "Education"),
    WORK_EXPERIENCE(4, "Work Experience"),
    ADDRESS(5, "Address"),
    DOCUMENTS(6, "Documents"),
    OTHER(7, "Other");

    private final int displayOrder;
    private final String value;

    SectionConstants(int displayOrder, String value) {
        this.displayOrder = displayOrder;
        this.value = value;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public String getValue() {
        return value;
    }
    public static SectionConstants fromNameOrValue(String input) {
        for (SectionConstants s : values()) {
            if (s.name().equalsIgnoreCase(input)
                || s.value.equalsIgnoreCase(input)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid section: " + input);
    }
}
