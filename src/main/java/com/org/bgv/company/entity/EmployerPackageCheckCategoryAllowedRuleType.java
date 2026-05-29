package com.org.bgv.company.entity;

import com.org.bgv.entity.EmployerPackage;
import com.org.bgv.entity.RuleTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(
    name = "employer_package_allowed_ruletype",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"employer_package_id", "check_category_id", "rule_type_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerPackageCheckCategoryAllowedRuleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 FK → Employer Package
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_package_id", nullable = false)
    private EmployerPackage employerPackage;

    // 🔹 Category (flatten for performance)
    @Column(name = "check_category_id", nullable = false)
    private Long checkCategoryId;

    // 🔹 Rule Type (flatten)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_type_id", nullable = false)
    private RuleTypes ruleType;

    // 🔹 Whether employer MUST select this rule
    @Column(name = "is_required")
    private Boolean required;

    // 🔹 Whether rule is part of base package
    @Column(name = "included_in_base")
    private Boolean includedInBase;

    // 🔹 For ANY_X / MIN_X rules
    @Column(name = "requires_count")
    private Boolean requiresCount;

    @Column(name = "min_count")
    private Integer minCount;

    @Column(name = "max_count")
    private Integer maxCount;

    // 🔹 Default value from admin (can be overridden by employer)
    @Column(name = "default_selected_count")
    private Integer defaultSelectedCount;

    // 🔹 UI ordering
    @Column(name = "priority_order")
    private Integer priorityOrder;
}
