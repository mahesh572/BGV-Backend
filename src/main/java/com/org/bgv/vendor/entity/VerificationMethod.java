package com.org.bgv.vendor.entity;

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

    private String code;

    private String name;

    private String description;

    private Boolean active = true;
}
