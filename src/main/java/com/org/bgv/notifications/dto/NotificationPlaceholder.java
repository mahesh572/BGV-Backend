package com.org.bgv.notifications.dto;

public enum NotificationPlaceholder {
	
	// USER_FULL_NAME("userFullName","User Full Name"),
	
	// User
	
	USER_ACCOUNT_ACTIVATION_LINK("useraccountactiationlink","User Activation Link"),
	USER_FULL_NAME("userfullname","User Full Name"),

    // 🔹 Employer / Organization // who initiated BGV for candidate
    EMPLOYER_BRAND_NAME("employerBrandName", "Employer Brand Name"),
    EMPLOYER_LEGAL_NAME("employerLegalName", "Employer Legal Name"),
    EMPLOYER_SUPPORT_EMAIL("employerSupportEmail","Employer Support Email"),
    
    
    // 🔹 Platform
    PLATFORM_LEGAL_NAME("platformLegalName","Platform Legal Name"),
    PLATFORM_BRAND_NAME("platformBrandName", "Platform Brand Name"),
    PLATFORM_SUPPORT_EMAIL("platformSupportEmail", "Platform Support Email"),

    // 🔹 Employee / Employer Account
    EMPLOYEE_NAME("employeeName", "Employee Name"),
    EMPLOYEE_EMAIL("employeeEmail", "Employee Email"),
   // EMPLOYEE_LINK_EXPIRY_DURATION("",""),

    TEMPORARY_PASSWORD("temporaryPassword", "Temporary Password"),
    RESET_PASSWORD_LINK("resetPasswordLink", "Reset Password Link"),
    PASSWORD_LINK_EXPIRY_DURATION("linkExpiryDuration", "Reset Link Expiry Duration"),

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
    INTERNAL_REMARKS("internalRemarks", "Internal Remarks"),
	
    VERIFICATION_LINK("verificationLink","Verification Link"),
    VERIFICATION_LINK_EXPIRY_DATE("verificationExpiryLink","Verification Expiry Link"),
	CURRENT_YEAR("currentYear","Current Year"),
	
	// CHECK
	CHECK_NAME("checkName","Check Name"),
	INSUFFICIENT_COUNT("insufficientCount","Insufficient Count"),
	
	// Verification Methods
	// Verification Method - HR Verification
	HR_NAME("hrName", "HR Contact Name"),
	HR_EMAIL("hrEmail", "HR Email"),

	COMPANY_NAME("companyName", "Company Name"),

	EMPLOYEE_ID("employeeId", "Employee ID"),
	DESIGNATION("designation", "Designation"),
	DEPARTMENT("department", "Department"),
	EMPLOYMENT_PERIOD("employmentPeriod", "Employment Period"),

	//VERIFICATION_METHOD("verificationMethod", "Verification Method"),
	//VERIFICATION_REQUEST_ID("verificationRequestId", "Verification Request ID"),

	//VERIFICATION_PORTAL_URL("verificationPortalUrl", "Verification Portal URL"),
	//UPLOAD_EVIDENCE_URL("uploadEvidenceUrl", "Upload Evidence URL"),

	VENDOR_NAME("vendorName", "Vendor Name"),
	VENDOR_EMAIL("vendorEmail", "Vendor Email"),
	VENDOR_PHONE("vendorPhone", "Vendor Phone");
	

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

// employerSupportEmail,platformLegalName
