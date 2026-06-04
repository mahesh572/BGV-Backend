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
    VERIFICATION_CHECK_ACTION_REQUIRED,

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
    EMPLOYEE_ACCOUNT_CREATED,

    // ======================
    // REPORT
    // ======================
    REPORT_GENERATED,
    REPORT_SHARED,
    
    // Verification Methods
    
    EMPLOYMENT_VERIFICATION_EMAIL_REQUESTED
    
    
    

   // EMPLOYMENT_VERIFICATION_EMAIL_SENT,

   // EMPLOYMENT_VERIFICATION_RESPONSE_RECEIVED,

   // EMPLOYMENT_VERIFICATION_REMINDER_SENT,

   // EMPLOYMENT_VERIFICATION_COMPLETED
  //  EMPLOYMENT_VERIFICATION_EMAIL_DELIVERY_FAILED
  //  EMPLOYMENT_VERIFICATION_RESPONSE_RECEIVED
  //  EMPLOYMENT_VERIFICATION_COMPLETED
}

