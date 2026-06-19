package com.org.bgv.vendor.entity;

import com.org.bgv.enums.FieldVerificationStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="verification_field")
@Data
public class VerificationField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fieldId;

    @ManyToOne
    private VerificationObject verificationObject;

    private String fieldName;

    private String displayName;

    private String candidateValue;

    private String sourceValue;

    private String finalValue;

    @Enumerated(EnumType.STRING)
    private FieldVerificationStatus status;

}