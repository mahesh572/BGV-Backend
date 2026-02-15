package com.org.bgv.company.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.org.bgv.company.dto.IndustryType;
import com.org.bgv.company.dto.OrganizationLegalType;
import com.org.bgv.company.dto.OrganizationSize;
import com.org.bgv.company.dto.OrganizationType;


@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Organization Identity
    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization_type", nullable = false)
    private OrganizationType organizationType;
    // EMPLOYER, VENDOR, UNIVERSITY, PLATFORM

    @Enumerated(EnumType.STRING)
    @Column(name = "legal_type", nullable = false)
    private OrganizationLegalType legalType;

    @Column(name = "registration_number", unique = true)
    private String registrationNumber;

    @Column(name = "tax_id", unique = true)
    private String taxId;

    @Column(name = "incorporation_date")
    private LocalDate incorporationDate;

    // Business Classification
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IndustryType industry;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization_size")
    private OrganizationSize organizationSize;

    @Column(name = "website")
    private String website;

    @Column(name = "description", length = 1000)
    private String description;

    // Primary Contact (Admin / SPOC)
    @Column(name = "contact_person_name")
    private String contactPersonName;

    @Column(name = "contact_person_title")
    private String contactPersonTitle;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    // Address
    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "zip_code")
    private String zipCode;

    // Metadata
    @Column(name = "status")
    private String status; // ACTIVE, INACTIVE, SUSPENDED

    @Column(name = "linkedin_profile")
    private String linkedinProfile;

    @Column(name = "admin_profile_picture_path")
    private String adminProfilePicturePath;

    // Audit
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

