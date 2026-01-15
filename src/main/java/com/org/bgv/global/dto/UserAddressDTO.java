package com.org.bgv.global.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.org.bgv.entity.AddressType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAddressDTO {

    /* =========================
       IDENTITY
       ========================= */
    private Long id;
    private Long userId;

    /* =========================
       ADDRESS CORE
       ========================= */
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    private AddressType addressType;
    private Boolean isDefault;
    private Boolean isMyPermanentAddress;

    /* =========================
       RESIDENCE DETAILS
       ========================= */
    private LocalDate currentlyResidingFrom;
    private Boolean currentlyResidingAtThisAddress;
    private Integer durationOfStayMonths;

    /* =========================
       GEO / VALIDATION
       ========================= */
    private Boolean isValidated;
    private String validationSource;
    private LocalDate validationDate;

    private Double latitude;
    private Double longitude;
    private String formattedAddress;
    private String placeId;
    private String nearestLandmark;

    private String ownershipType;

    /* =========================
       VERIFICATION (READ-ONLY)
       ========================= */
    private Boolean verified;
    private String verificationStatus;
    private String verifiedBy;
    private LocalDateTime verifiedAt;
    private String verificationNotes;

    /* =========================
       STATUS & AUDIT
       ========================= */
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
