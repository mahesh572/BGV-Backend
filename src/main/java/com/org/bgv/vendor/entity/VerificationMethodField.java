package com.org.bgv.vendor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "verification_method_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationMethodField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verification_method_id")
    private VerificationMethod verificationMethod;

    @Column(nullable = false)
    private String fieldName;

    @Column(nullable = false)
    private String fieldLabel;

    @Column(nullable = false)
    private String fieldType;

    private Boolean requiredField = false;

    private Integer displayOrder;

    private String placeholder;

    private Boolean active = true;
}
