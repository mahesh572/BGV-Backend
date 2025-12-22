package com.org.bgv.constants;

public enum DocumentType {
    AADHAR("Aadhar Card"),
    PAN_CARD("PAN Card"),
    PASSPORT("Passport"),
    DRIVING_LICENSE("Driving License"),
    VOTER_ID("Voter ID"),
    DEGREE("Degree Certificate"),
    MARKSHEET("Marksheet"),
    EXPERIENCE_LETTER("Experience Letter"),
    PAYSLIP("Payslip"),
    BANK_STATEMENT("Bank Statement"),
    ADDRESS_PROOF("Address Proof"),
    PHOTOGRAPH("Photograph"),
    SIGNATURE("Signature"),
    RESUME("Resume"),
    OTHER("Other");
    
    private final String displayName;
    
    DocumentType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
