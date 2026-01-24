package com.org.bgv.common;


public class RoleConstants {
	
	public static final String ADMINISTRATOR = "Administrator";

	
	public static final Long TYPE_REGULAR = 1L;
	public static final String TYPE_REGULAR_LABEL = "Regular";
	
	public static final Long TYPE_COMPANY = 2L;
	public static final String TYPE_COMPANY_LABEL = "Company";

	public static final Long TYPE_VENDOR = 3L;

	public static final String TYPE_VENDOR_LABEL = "Vendor";

	public static final String ROLE_CANDIDATE = "Candidate";
	
	public static final String ROLE_COMAPNY_ADMINISTRATOR = "Company Administrator";
	
	

	

	public static String getTypeLabel(int type) {
		if (type == TYPE_COMPANY) {
			return TYPE_COMPANY_LABEL;
		}
		else if (type == TYPE_VENDOR) {
			return TYPE_VENDOR_LABEL;
		}

		return TYPE_REGULAR_LABEL;
	}
	
	

}
