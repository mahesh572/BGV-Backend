package com.org.bgv.company.entity;

import java.math.BigDecimal;

import com.org.bgv.entity.EmployerPackage;
import com.org.bgv.entity.RuleTypes;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "employer_package_selected_rule",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"employer_package_id", "check_category_id", "rule_type_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerPackageSelectedRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Employer Package
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_package_id", nullable = false)
    private EmployerPackage employerPackage;

    // 🔹 Category (flattened for performance)
    @Column(name = "check_category_id", nullable = false)
    private Long checkCategoryId;

    // 🔹 Selected Rule
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_type_id", nullable = false)
    private RuleTypes ruleType;

    // 🔹 Selection Count (for ANY_X rules)
    @Column(name = "selected_count")
    private Integer selectedCount;

    // 🔹 Pricing Snapshot (if rule impacts price)
    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    // 🔹 Whether this rule is part of base package
    @Column(name = "included_in_base")
    private Boolean includedInBase;

    // 🔹 Flags for validation
    @Column(name = "is_required")
    private Boolean required;

    @Column(name = "requires_count")
    private Boolean requiresCount;
}