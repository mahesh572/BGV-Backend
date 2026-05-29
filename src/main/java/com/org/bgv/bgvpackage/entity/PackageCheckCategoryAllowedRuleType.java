package com.org.bgv.bgvpackage.entity;


import com.org.bgv.entity.BgvPackage;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "package_checkcategory_allowed_ruletype",
uniqueConstraints = @UniqueConstraint(
        columnNames = {"package_id", "check_category_id", "rule_type_id"}
    )
		)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageCheckCategoryAllowedRuleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pcr_id")
    private Long id;

    // FK → Package
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private BgvPackage bgvPackage;

    // FK → Check Category (Education / Employment / Identity etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_category_id", nullable = false)
    private CheckCategory checkCategory;

    // FK → Rule Type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_type_id", nullable = false)
    private RuleTypes ruleType;

    // Whether this rule is mandatory inside package
    @Column(name = "is_required")
    private Boolean required;

    // Optional ordering if multiple rules exist
    @Column(name = "priority_order")
    private Integer priorityOrder;
}

