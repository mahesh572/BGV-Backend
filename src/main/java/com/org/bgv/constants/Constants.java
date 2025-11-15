package com.org.bgv.constants;

public class Constants {

	 public static final String USER_TYPE_VENDOR = "VENDOR";
	// public static final String USER_TYPE_EMPLOYER = "EMPLOYER";
	// public static final String USER_TYPE_EMPLOYEE = "EMPLOYEE";
	 public static final String USER_TYPE_COMPANY = "COMPANY";
	 public static final String USER_TYPE_CANDIDATE = "CANDIDATE";
	 
	 public static final String CANDIDATE_SOURCE_SELF = "SELF";
	 public static final String CANDIDATE_SOURCE_EMPLOYER = "COMPANY";
	 public static final String CANDIDATE_SOURCE_ADMIN = "ADMIN";
	 
	 public static final String USER_STATUS_ACTIVE = "ACTIVE";
	 
	// Candidate record is just created in the system but no background verification has started.
	 public static final String CANDIDATE_STATUS_CREATED = "CREATED";
	 
	 // Candidate details submitted and waiting for verification process to begin
	 public static final String CANDIDATE_PENDING_VERIFICATION = "PENDING_VERIFICATION";
	
	 // Background verification process is currently underway
	 public static final String CANDIDATE_IN_PROGRESS = "IN_PROGRESS";
	 
	 // All verification checks are complete and successful
	 public static final String CANDIDATE_VERIFIED = "VERIFIED";
	 
	 // Candidate failed one or more verification checks
	 public static final String CANDIDATE_REJECTED = "REJECTED";
	 
	 // Verification temporarily paused (e.g., missing documents, clarification needed).
	 public static final String CANDIDATE_ON_HOLD = "ON_HOLD";
	 
	 // Candidate record removed or disabled (soft delete)
	 public static final String CANDIDATE_INACTIVE = "INACTIVE";
	 
}
