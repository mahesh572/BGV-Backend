package com.org.bgv.constants;

public enum EmployerPackageStatus {
    DRAFT,       // Package is being configured, not yet active
    ACTIVE,      // Package is active and can be assigned to candidates
    INACTIVE,    // Package is temporarily inactive
    EXPIRED,     // Package has expired (time-based)
    SUSPENDED,   // Package is suspended (admin action)
    DELETED      // Package is soft deleted
}