package com.org.bgv.notifications.dto;

import java.util.EnumSet;
import java.util.Set;

public enum PlaceholderRolePolicy {

    // 🔹 Employer / Organization
    EMPLOYER_BRAND_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    EMPLOYER_LEGAL_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR // 🔒 legal only
    )),
    EMPLOYER_SUPPORT_EMAIL(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR)),

    // 🔹 Platform
    PLATFORM_BRAND_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    PLATFORM_SUPPORT_EMAIL(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),
    
    PLATFORM_LEGAL_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR)),

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
            TemplateUserRole.ADMINISTRATOR // 🔒 very sensitive
    )),

    RESET_PASSWORD_LINK(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    LINK_EXPIRY_DURATION(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    // 🔹 Candidate
    CANDIDATE_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    CANDIDATE_EMAIL(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR // 🔒 PII
    )),

    // 🔹 Case
    CASE_ID(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    CASE_STATUS(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    // 🔹 Document
    DOCUMENT_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    REJECTION_REASON(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    // 🔹 SLA / Internal
    SLA_DEADLINE(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR
    )),

    INTERNAL_REMARKS(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR
    )),
    VERIFICATION_LINK(EnumSet.of(TemplateUserRole.ADMINISTRATOR)),
    VERIFICATION_LINK_EXPIRY_DATE(EnumSet.of(TemplateUserRole.ADMINISTRATOR)),
    CURRENT_YEAR(EnumSet.of(TemplateUserRole.ADMINISTRATOR)),
	PASSWORD_LINK_EXPIRY_DURATION(EnumSet.of(TemplateUserRole.ADMINISTRATOR)),
	
	CHECK_NAME(EnumSet.of(TemplateUserRole.ADMINISTRATOR)),
	INSUFFICIENT_COUNT(EnumSet.of(TemplateUserRole.ADMINISTRATOR));
	

    private final Set<TemplateUserRole> roles;

    PlaceholderRolePolicy(Set<TemplateUserRole> roles) {
        this.roles = roles;
    }

    public boolean allowedFor(TemplateUserRole role) {
        return roles.contains(role);
    }
}
