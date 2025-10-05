package com.org.bgv.common;

public class Status {
	
	 public static final String USER_TYPE_VENDOR = "VENDOR";
	 public static final String USER_TYPE_EMPLOYER = "EMPLOYER";
	 public static final String USER_TYPE_EMPLOYEE = "EMPLOYEE";
	 
	 public static final String USER_STATUS = "DRAFT";
	 public static final String USER_ACTIVE = "ACTIVE";
	 public static final String USER_PENDING = "PENDING";
	 public static final String USER_SUBMITTED = "SUBMITTED";
	 
	 
	// Main Status Types
    public static final String PENDING = "PENDING";
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
    public static final String SUSPENDED = "SUSPENDED";
    public static final String REJECTED = "REJECTED";
    public static final String DELETED = "DELETED";
    
    // Verification Status
    public static final String VERIFICATION_PENDING = "VERIFICATION_PENDING";
    public static final String VERIFIED = "VERIFIED";
    public static final String VERIFICATION_FAILED = "VERIFICATION_FAILED";
    
    // Profile Completion Status
    public static final String PROFILE_INCOMPLETE = "PROFILE_INCOMPLETE";
    public static final String PROFILE_COMPLETE = "PROFILE_COMPLETE";
    public static final String PROFILE_UNDER_REVIEW = "PROFILE_UNDER_REVIEW";
    
    // Availability Status
    public static final String AVAILABLE = "AVAILABLE";
    public static final String BUSY = "BUSY";
    public static final String ON_LEAVE = "ON_LEAVE";
    public static final String NOT_AVAILABLE = "NOT_AVAILABLE";
    
    // Work Status
    public static final String LOOKING_FOR_PROJECTS = "LOOKING_FOR_PROJECTS";
    public static final String CURRENTLY_WORKING = "CURRENTLY_WORKING";
    public static final String NOT_LOOKING = "NOT_LOOKING";
    
    public static final String USER_TYPE_COMPANY = "COMPANY";
    
    public static final String  ROLE_COMPANY_ADMIN = "ROLE_COMPANY_ADMIN";
    public static final String  ROLE_COMPANY_HR_MANAGER = "ROLE_COMPANY_HR_MANAGER";
    
    // Get all main statuses
    public static String[] getAllMainStatuses() {
        return new String[]{PENDING, ACTIVE, INACTIVE, SUSPENDED, REJECTED, DELETED};
    }
    
    // Get active statuses
    public static String[] getActiveStatuses() {
        return new String[]{ACTIVE, VERIFIED, AVAILABLE, LOOKING_FOR_PROJECTS};
    }
    
    // Get inactive statuses
    public static String[] getInactiveStatuses() {
        return new String[]{INACTIVE, SUSPENDED, NOT_AVAILABLE, NOT_LOOKING};
    }
    
    // Check if status is active
    public static boolean isActive(String status) {
        return ACTIVE.equals(status) || VERIFIED.equals(status) || AVAILABLE.equals(status);
    }
    
    // Check if status allows work
    public static boolean canAcceptWork(String status) {
        return ACTIVE.equals(status) && !SUSPENDED.equals(status) && !INACTIVE.equals(status);
    }
	
}
