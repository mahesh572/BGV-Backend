package com.org.bgv.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vendor")
public class Vendor {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    
    // Professional Information
    
    @Column(name = "vendor_type", nullable = false)
    private String vendorType;
    
        
    @Column(name = "experience_years")
    private String experience;
    
        
    // Business Information
    @Column(name = "business_name", length = 255)
    private String businessName;
    
   
    @Column(name = "business_type")
    private String businessType;
    
    @Column(name = "registration_number", length = 100)
    private String registrationNumber;
    
    @Column(name = "tax_id", length = 100)
    private String taxId;
    
    @Column(name = "website", length = 255)
    private String website;
    
    // Address Information
    @Column(name = "address_line1")
    private String addressLine1;
    
    @Column(name = "address_line2")
    private String addressLine2;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "state", length = 100)
    private String state;
    
    @Column(name = "country", length = 100)
    private String country;
    
    @Column(name = "zip_code", length = 20)
    private String zipCode;
    
    // Additional Information
    @Column(name = "linkedin_profile", length = 255)
    private String linkedinProfile;
    
    @Column(name = "portfolio_url", length = 255)
    private String portfolioUrl;
    
    @Column(name = "hourly_rate")
    private Double hourlyRate;
    
    
    @Column(name = "availability")
    private String availability;
    
    
    @Column(name = "preferred_work_type")
    private String preferredWorkType;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    // Status
   
    @Column(name = "status")
    private String status ;
    
    // Timestamps
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    
}