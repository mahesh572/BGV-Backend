package com.org.bgv.enums;

public enum VerificationOutcome {

    VERIFIED(
            "Verified",
            "Information provided by the source matches the candidate details.",
            true,
            true,
            true),

    DISCREPANCY(
            "Discrepancy Found",
            "Differences were identified between the source information and the candidate's submitted details.",
            false,
            true,
            true),

    PARTIALLY_VERIFIED(
            "Partially Verified",
            "Some information was verified while other information could not be confirmed.",
            true,
            true,
            true),

    UNABLE_TO_VERIFY(
            "Unable to Verify",
            "Verification could not be completed after all permitted attempts.",
            false,
            false,
            true),

    NO_RESPONSE(
            "No Response",
            "No response was received after repeated follow-up attempts.",
            false,
            false,
            true);

    private final String label;
    private final String description;
    private final boolean success;
    private final boolean evidenceRequired;
    private final boolean remarksRequired;

    VerificationOutcome(String label,
                        String description,
                        boolean success,
                        boolean evidenceRequired,
                        boolean remarksRequired) {
        this.label = label;
        this.description = description;
        this.success = success;
        this.evidenceRequired = evidenceRequired;
        this.remarksRequired = remarksRequired;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isEvidenceRequired() {
        return evidenceRequired;
    }

    public boolean isRemarksRequired() {
        return remarksRequired;
    }
}
