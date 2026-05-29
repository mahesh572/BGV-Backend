package com.org.bgv.enums;


public enum InAppTemplatePlaceholder {

    // =========================================================
    // CANDIDATE
    // =========================================================

    CANDIDATE_NAME(
            "candidateName",
            "Candidate Name"
    ),

    // =========================================================
    // CASE
    // =========================================================

    CASE_ID(
            "caseId",
            "Case ID"
    ),

    CASE_STATUS(
            "caseStatus",
            "Case Status"
    ),

    // =========================================================
    // CHECK
    // =========================================================

    CHECK_NAME(
            "checkName",
            "Check Name"
    ),

    CHECK_STATUS(
            "checkStatus",
            "Check Status"
    ),

    CHECK_TYPE(
            "checkType",
            "Check Type"
    ),

    INSUFFICIENT_COUNT(
            "insufficientCount",
            "Insufficient Count"
    ),

    // =========================================================
    // DOCUMENT
    // =========================================================

    DOCUMENT_NAME(
            "documentName",
            "Document Name"
    ),

    REJECTION_REASON(
            "rejectionReason",
            "Rejection Reason"
    ),

    // =========================================================
    // ACTION
    // =========================================================

    ACTION_URL(
            "actionUrl",
            "Action URL"
    ),

    ACTION_LABEL(
            "actionLabel",
            "Action Label"
    ),

    // =========================================================
    // NOTIFICATION
    // =========================================================

    NOTIFICATION_TITLE(
            "notificationTitle",
            "Notification Title"
    ),

    NOTIFICATION_MESSAGE(
            "notificationMessage",
            "Notification Message"
    ),

    PRIORITY(
            "priority",
            "Priority"
    ),

    ICON(
            "icon",
            "Notification Icon"
    ),

    // =========================================================
    // USER / ACTOR
    // =========================================================

    VENDOR_NAME(
            "vendorName",
            "Vendor Name"
    ),

    ADMIN_NAME(
            "adminName",
            "Admin Name"
    ),

    ACTOR_NAME(
            "actorName",
            "Actor Name"
    ),

    // =========================================================
    // ORGANIZATION
    // =========================================================

    EMPLOYER_BRAND_NAME(
            "employerBrandName",
            "Employer Brand Name"
    ),

    PLATFORM_BRAND_NAME(
            "platformBrandName",
            "Platform Brand Name"
    ),

    // =========================================================
    // COMMON
    // =========================================================

    CURRENT_YEAR(
            "currentYear",
            "Current Year"
    );

    private final String key;

    private final String label;

    InAppTemplatePlaceholder(String key, String label) {

        this.key = key;

        this.label = label;
    }

    public String key() {
        return key;
    }

    public String label() {
        return label;
    }
}
