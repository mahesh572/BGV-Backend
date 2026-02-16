package com.org.bgv.bgvpackage.entity;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.PricingType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employer_check_pricing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerCheckPricing {

	//Employer + Category + RuleType must be unique
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_category_id", nullable = false)
    private CheckCategory checkCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingType pricingType;   // FLAT / PER_RECORD

    @Column(nullable = false)
    private Double unitPrice;

    private Double minCharge;
    private Double maxCharge;

    private Boolean active = true;
    
    @ManyToOne
    private RuleTypes ruleType;
}
