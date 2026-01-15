package com.org.bgv.global.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.org.bgv.entity.AddressType;
import com.org.bgv.entity.User;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =====================
       OWNERSHIP
       ===================== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /* =====================
       ADDRESS DETAILS
       ===================== */

    private String addressLine1;
    private String addressLine2;

    private String city;
    private String state;
    private String country;
    private String zipCode;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;   // CURRENT / PERMANENT / OFFICE

    @Column(name = "is_default")
    private Boolean defaultAddress = false;

    @Column(name = "is_my_permanent_address", nullable = false)
    private Boolean isMyPermanentAddress = false;

    @Column(name = "currently_residing_from")
    private LocalDate currentlyResidingFrom;

    @Column(name = "currently_residing_at_this_address", nullable = false)
    private Boolean currentlyResidingAtThisAddress = false;

    /* =====================
       VALIDATION (NON-BGV)
       ===================== */

    @Column(name = "is_validated")
    private boolean isValidated = false;

    @Column(name = "validation_source", length = 100)
    private String validationSource;   // manual, google_maps

    @Column(name = "validation_date")
    private LocalDate validationDate;

    private Double latitude;
    private Double longitude;

    @Column(name = "formatted_address", length = 1000)
    private String formattedAddress;

    @Column(name = "place_id", length = 200)
    private String placeId;

    @Column(name = "nearest_landmark", length = 200)
    private String nearestLandmark;

    @Column(name = "ownership_type", length = 50)
    private String ownershipType; // owned, rented, company_provided

    /* =====================
       STATUS
       ===================== */

    @Column(name = "status", length = 20)
    private String status = "active"; // active, inactive, archived

    /* =====================
       AUDIT
       ===================== */

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

    /* =====================
       DERIVED HELPERS
       ===================== */

    public Integer calculateDurationOfStay() {
        if (currentlyResidingFrom == null) return 0;

        return (int) java.time.temporal.ChronoUnit.MONTHS.between(
                currentlyResidingFrom.withDayOfMonth(1),
                LocalDate.now().withDayOfMonth(1)
        );
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();

        if (addressLine1 != null && !addressLine1.isBlank())
            sb.append(addressLine1);

        if (addressLine2 != null && !addressLine2.isBlank())
            sb.append(", ").append(addressLine2);

        if (city != null && !city.isBlank())
            sb.append(", ").append(city);

        if (state != null && !state.isBlank())
            sb.append(", ").append(state);

        if (zipCode != null && !zipCode.isBlank())
            sb.append(" - ").append(zipCode);

        if (country != null && !country.isBlank())
            sb.append(", ").append(country);

        return sb.toString();
    }
}

