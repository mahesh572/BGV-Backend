package com.org.bgv.company.entity;

import java.math.BigDecimal;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.enums.PricingType;
import com.org.bgv.onboarding.entity.Company;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "employer_document_pricing")
public class EmployerDocumentPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Company company;

    @ManyToOne
    private CheckCategory checkCategory;

    @ManyToOne
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    private PricingType pricingType;  // FLAT / PER_RECORD

    private BigDecimal  unitPrice;

    private Double minCharge;
    private Double maxCharge;

    private Boolean active = true;
}