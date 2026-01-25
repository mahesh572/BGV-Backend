package com.org.bgv.notifications.dto;

import java.util.EnumSet;
import java.util.Set;


public enum PlaceholderRolePolicy {

	PLATFORM_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),
	
	SUPPORT_EMAIL(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),
	
	// 🔹 Employee / Employer Account
    EMPLOYEE_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    EMPLOYEE_EMAIL(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    TEMPORARY_PASSWORD(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR   // 🔒 restricted
    )),

    RESET_PASSWORD_LINK(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    LINK_EXPIRY_DURATION(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),
	
	
    COMPANY_NAME(EnumSet.of(TemplateUserRole.ADMINISTRATOR, TemplateUserRole.COMPANY_ADMINISTRATOR)),
    EVENT_NAME(EnumSet.of(TemplateUserRole.ADMINISTRATOR, TemplateUserRole.COMPANY_ADMINISTRATOR)),

    CANDIDATE_NAME(EnumSet.of(TemplateUserRole.ADMINISTRATOR, TemplateUserRole.COMPANY_ADMINISTRATOR)),
    CANDIDATE_EMAIL(EnumSet.of(TemplateUserRole.ADMINISTRATOR)),

    CASE_ID(EnumSet.of(TemplateUserRole.ADMINISTRATOR, TemplateUserRole.COMPANY_ADMINISTRATOR)),
    CASE_STATUS(EnumSet.of(TemplateUserRole.ADMINISTRATOR, TemplateUserRole.COMPANY_ADMINISTRATOR)),

    DOCUMENT_NAME(EnumSet.of(TemplateUserRole.ADMINISTRATOR, TemplateUserRole.COMPANY_ADMINISTRATOR)),
    REJECTION_REASON(EnumSet.of(TemplateUserRole.ADMINISTRATOR, TemplateUserRole.COMPANY_ADMINISTRATOR)),

    SLA_DEADLINE(EnumSet.of(TemplateUserRole.ADMINISTRATOR)),
    INTERNAL_REMARKS(EnumSet.of(TemplateUserRole.ADMINISTRATOR));

    private final Set<TemplateUserRole> roles;

    PlaceholderRolePolicy(Set<TemplateUserRole> roles) {
        this.roles = roles;
    }

    public boolean allowedFor(TemplateUserRole role) {
        return roles.contains(role);
    }
}
