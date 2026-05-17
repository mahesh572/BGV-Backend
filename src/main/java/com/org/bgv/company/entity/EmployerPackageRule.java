package com.org.bgv.company.entity;

import com.org.bgv.entity.EmployerPackage;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employer_package_rule")
public class EmployerPackageRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private EmployerPackage employerPackage;

    private Long checkCategoryId;
    private Long ruleTypeId;

    private Boolean includedInBase;

    // 🔥 PRICING SNAPSHOT
    private Double unitPrice;

    private Integer minCount;
    private Integer maxCount;

    private Boolean requiresCount;
    private Integer selectedCount;
}
