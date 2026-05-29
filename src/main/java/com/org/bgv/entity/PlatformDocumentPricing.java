package com.org.bgv.entity;

import java.math.BigDecimal;

import com.org.bgv.enums.PricingType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "platform_document_pricing")
public class PlatformDocumentPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
