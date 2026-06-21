package com.org.bgv.common;


public class RoleConstants {
	
	public static final String ADMINISTRATOR = "Administrator";

	
	public static final Long TYPE_REGULAR = 1L;
	public static final String TYPE_REGULAR_LABEL = "Regular";
	
	//public static final Long TYPE_COMPANY = 2L;
	//public static final String TYPE_COMPANY_LABEL = "Company";
	
	public static final Long TYPE_EMPLOYER = 2L;
	public static final String TYPE_EMPLOYER_LABEL = "Employer";
		
		

	public static final Long TYPE_VENDOR = 3L;

	public static final String TYPE_VENDOR_LABEL = "Vendor";

	public static final String ROLE_CANDIDATE = "Candidate";
	
	public static final String ROLE_COMAPNY_ADMINISTRATOR = "Company Administrator";
	
	public static final String ROLE_USER = "User";
	
	public static final String ROLE_FIELD_AGENT = "Field Agent";
	public static final String ROLE_VENDOR_AGENT = "Vendor Agent";
	
	
	

	

	public static String getTypeLabel(int type) {
		if (type == TYPE_EMPLOYER) {
			return TYPE_EMPLOYER_LABEL;
		}
		else if (type == TYPE_VENDOR) {
			return TYPE_VENDOR_LABEL;
		}

		return TYPE_REGULAR_LABEL;
	}
	
	

}
