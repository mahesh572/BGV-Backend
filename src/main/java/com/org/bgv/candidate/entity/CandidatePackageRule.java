package com.org.bgv.candidate.entity;

import com.org.bgv.entity.EmployerPackage;
import com.org.bgv.entity.VerificationCase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;

@Builder
@Entity
@Table(name = "candidate_package_rule")
public class CandidatePackageRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_package_id")
    private EmployerPackage employerPackageId;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private VerificationCase verificationCase;
    
    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    private Long checkCategoryId;
    private Long ruleTypeId;

    // 🔹 Whether this rule is mandatory
    @Column(name = "is_required")
    private Boolean required = false;
    
    // 🔹 Whether it came from base package
    @Column(name = "included_in_package")
    private Boolean includedInPackage = false;
    
    @Column(name = "is_addon")
    private Boolean addon = false;

    
 // 🔹 Pricing snapshot (VERY IMPORTANT)
    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "selected_count")
    private Integer selectedCount;

    @Column(name = "total_price")
    private Double totalPrice;
    
}