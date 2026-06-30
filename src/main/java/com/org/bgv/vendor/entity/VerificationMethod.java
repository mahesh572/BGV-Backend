package com.org.bgv.vendor.entity;

import com.org.bgv.enums.VerificationMethodCode;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "verification_method")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long methodId;

    private VerificationMethodCode code;

    private String name;

    private String description;
    
    private Integer maxAttempts;

    private Integer retryIntervalHours;

    private Boolean retryAllowed;

    private Boolean active = true;
}
