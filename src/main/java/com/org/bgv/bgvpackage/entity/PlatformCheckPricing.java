package com.org.bgv.bgvpackage.entity;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.PricingType;

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
@Table(name = "platform_check_pricing")
public class PlatformCheckPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private CheckCategory checkCategory;

    @ManyToOne
    private RuleTypes ruleType;

    @Enumerated(EnumType.STRING)
    private PricingType pricingType; // FLAT / PER_RECORD

    private Double unitPrice;

    private Boolean active = true;
}

