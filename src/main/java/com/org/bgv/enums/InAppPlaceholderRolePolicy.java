package com.org.bgv.enums;


import java.util.EnumSet;
import java.util.Set;

import com.org.bgv.notifications.dto.TemplateUserRole;

public enum InAppPlaceholderRolePolicy {

    // =========================================================
    // CANDIDATE
    // =========================================================

    CANDIDATE_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
           
    )),

    // =========================================================
    // CASE
    // =========================================================

    CASE_ID(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
           
    )),

    CASE_STATUS(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
           
    )),

    // =========================================================
    // CHECK
    // =========================================================

    CHECK_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
           
    )),

    CHECK_STATUS(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
           
    )),

    CHECK_TYPE(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
           
    )),

    INSUFFICIENT_COUNT(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
           
    )),

    // =========================================================
    // DOCUMENT
    // =========================================================

    DOCUMENT_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
           
    )),

    REJECTION_REASON(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
           
    )),

    // =========================================================
    // ACTION
    // =========================================================

    ACTION_URL(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    ACTION_LABEL(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    // =========================================================
    // NOTIFICATION
    // =========================================================

    NOTIFICATION_TITLE(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
           
    )),

    NOTIFICATION_MESSAGE(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
           
    )),

    PRIORITY(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    ICON(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    // =========================================================
    // USER / ACTOR
    // =========================================================

    VENDOR_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    ADMIN_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR
    )),

    ACTOR_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
           
    )),

    // =========================================================
    // ORGANIZATION
    // =========================================================

    EMPLOYER_BRAND_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    PLATFORM_BRAND_NAME(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
    )),

    // =========================================================
    // COMMON
    // =========================================================

    CURRENT_YEAR(EnumSet.of(
            TemplateUserRole.ADMINISTRATOR,
            TemplateUserRole.COMPANY_ADMINISTRATOR
           
    ));

    private final Set<TemplateUserRole> roles;

    InAppPlaceholderRolePolicy(Set<TemplateUserRole> roles) {
        this.roles = roles;
    }

    public boolean allowedFor(TemplateUserRole role) {
        return roles.contains(role);
    }
}