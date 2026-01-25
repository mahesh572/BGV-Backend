package com.org.bgv.notifications.dto;

public enum NotificationPlaceholder {

    // 🔹 Common
    COMPANY_NAME("companyName", "Company Name"),
    EVENT_NAME("eventName", "Event Name"),
    PLATFORM_NAME("platformName", "Platform Name"),
    SUPPORT_EMAIL("supportEmail", "Support Email"),

    // 🔹 Employee / Employer Account
    EMPLOYEE_NAME("employeeName", "Employee Name"),
    EMPLOYEE_EMAIL("employeeEmail", "Employee Email"),
    TEMPORARY_PASSWORD("temporaryPassword", "Temporary Password"),
    RESET_PASSWORD_LINK("resetPasswordLink", "Reset Password Link"),
    LINK_EXPIRY_DURATION("linkExpiryDuration", "Reset Link Expiry Duration"),

    // 🔹 Candidate
    CANDIDATE_NAME("candidateName", "Candidate Name"),
    CANDIDATE_EMAIL("candidateEmail", "Candidate Email"),

    // 🔹 Case
    CASE_ID("caseId", "Case ID"),
    CASE_STATUS("caseStatus", "Case Status"),

    // 🔹 Document
    DOCUMENT_NAME("documentName", "Document Name"),
    REJECTION_REASON("rejectionReason", "Rejection Reason"),

    // 🔹 SLA / Internal
    SLA_DEADLINE("slaDeadline", "SLA Deadline"),
    INTERNAL_REMARKS("internalRemarks", "Internal Remarks");

    private final String key;
    private final String label;

    NotificationPlaceholder(String key, String label) {
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
