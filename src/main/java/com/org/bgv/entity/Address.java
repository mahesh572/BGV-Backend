package com.org.bgv.entity;



import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;
    
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private boolean isDefault;
    
    @Enumerated(EnumType.STRING)
    private AddressType addressType;
    
    @Column(name = "currently_residing_from")
    private LocalDate currentlyResidingFrom;
    
    @Column(name = "currently_residing_at_this_address", nullable = false)
    private Boolean currentlyResidingAtThisAddress = false;
    

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

 // Verification fields
    @Column(name = "verified", nullable = false)
    private boolean verified = false;
    
    @Column(name = "verification_status", length = 50)
    private String verificationStatus = "pending";
    
    @Column(name = "verified_by", length = 100)
    private String verifiedBy;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "verification_notes", length = 1000)
    private String verificationNotes;
    
    @Column(name = "is_my_permanent_address", nullable = false)
    private Boolean isMyPermanentAddress = false;
    
    
 // Address validation fields
    @Column(name = "is_validated")
    private boolean isValidated = false;
    
    @Column(name = "validation_source", length = 100)
    private String validationSource; // manual, google_maps, postal_service
    
    @Column(name = "validation_date")
    private LocalDate validationDate;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "formatted_address", length = 1000)
    private String formattedAddress;
    
    @Column(name = "place_id", length = 200)
    private String placeId;
    
    @Column(name = "nearest_landmark", length = 200)
    private String nearestLandmark;
    
    @Column(name = "duration_of_stay_months")
    private Integer durationOfStayMonths;
    
    @Column(name = "ownership_type", length = 50)
    private String ownershipType; // owned, rented, leased, company_provided
    
 // Status
    @Column(name = "status", length = 20)
    private String status = "active"; // active, inactive, archived
    
 // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    
    
    public Integer calculateDurationOfStay() {
        if (currentlyResidingFrom == null) {
            return 0;
        }
        
        LocalDate today = LocalDate.now();
        return (int) java.time.temporal.ChronoUnit.MONTHS.between(
            currentlyResidingFrom.withDayOfMonth(1),
            today.withDayOfMonth(1)
        );
    }
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        
        if (addressLine1 != null && !addressLine1.trim().isEmpty()) {
            sb.append(addressLine1);
        }
        
        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(addressLine2);
        }
        
        if (city != null && !city.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        
        if (state != null && !state.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(state);
        }
        
        if (zipCode != null && !zipCode.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(zipCode);
        }
        
        if (country != null && !country.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        
        return sb.toString();
    }
    
}

