package com.org.bgv.vendor.entity;

import com.org.bgv.dto.CheckCategoryEnum;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "check_verification_method")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckVerificationMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CheckCategoryEnum checkType;

    @ManyToOne
    @JoinColumn(name = "method_id")
    private VerificationMethod verificationMethod;

    private Boolean mandatory = false;
}
