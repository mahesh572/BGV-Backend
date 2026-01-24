package com.org.bgv.notifications;

public enum NotificationEvent {
    
 // ======================
    // ACCOUNT / AUTH
    // ======================
    USER_REGISTERED,
    USER_ACTIVATED,
    USER_DEACTIVATED,
    PASSWORD_RESET_REQUESTED,
    PASSWORD_CHANGED,
    LOGIN_FAILED,
    ACCOUNT_LOCKED,

    // ======================
    // CANDIDATE
    // ======================
    CANDIDATE_INVITE,
    CANDIDATE_REGISTERED,
    CANDIDATE_PROFILE_COMPLETED,
    CANDIDATE_REMINDER,

    // ======================
    // DOCUMENT
    // ======================
    DOCUMENT_UPLOADED,
    DOCUMENT_APPROVED,
    DOCUMENT_REJECTED,
    DOCUMENT_REUPLOAD_REQUESTED,

    // ======================
    // VERIFICATION
    // ======================
    VERIFICATION_STARTED,
    VERIFICATION_IN_PROGRESS,
    VERIFICATION_FAILED,
    VERIFICATION_COMPLETED,

    // ======================
    // CASE
    // ======================
    CASE_CREATED,
    CASE_ASSIGNED,
    CASE_ON_HOLD,
    CASE_COMPLETED,
    CASE_CANCELLED,

    // ======================
    // SLA
    // ======================
    SLA_WARNING,
    SLA_BREACHED,

    // ======================
    // EMPLOYER / VENDOR
    // ======================
    EMPLOYER_CREATED,
    EMPLOYER_DEACTIVATED,
    VENDOR_ASSIGNED,
    VENDOR_REPLACED,

    // ======================
    // REPORT
    // ======================
    REPORT_GENERATED,
    REPORT_SHARED
}

