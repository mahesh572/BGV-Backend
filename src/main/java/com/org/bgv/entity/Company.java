package com.org.bgv.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Company Information
    @Column(name = "company_name", nullable = false)
    private String companyName;
    
    @Column(name = "company_type")
    private String companyType;
    
    @Column(name = "registration_number", unique = true)
    private String registrationNumber;
    
    @Column(name = "tax_id", unique = true)
    private String taxId;
    
    @Column(name = "incorporation_date")
    private LocalDate incorporationDate;
    
    @Column(name = "industry")
    private String industry;
    
    @Column(name = "company_size")
    private String companySize;
    
    @Column(name = "website")
    private String website;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    // Contact Information
    @Column(name = "contact_person_name")
    private String contactPersonName;
    
    @Column(name = "contact_person_title")
    private String contactPersonTitle;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    // Address Information
    @Column(name = "address_line1")
    private String addressLine1;
    
    @Column(name = "address_line2")
    private String addressLine2;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "zip_code")
    private String zipCode;
    
    // Additional Information
    @Column(name = "linkedin_profile")
    private String linkedinProfile;
    
    
    @Column(name = "admin_profile_picture_path")
    private String adminProfilePicturePath;
    
    // Audit fields
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}