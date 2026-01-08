package com.org.bgv.entity;

import java.util.List;

import com.org.bgv.common.PackageDocumentRequest;
import com.org.bgv.common.RuleTypesDTO;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "package_checkcategory_ruletype")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageCheckCategoryRuleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Package ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private BgvPackage bgvPackage;

    // --- Check Category (Identity, Education, Address, etc) ---
    @Column(name = "check_category_id", nullable = false)
    private Long checkCategoryId;

    // --- Rule Type (ANY_1, min_2, REQUIRED_ALL) ---
    @Column(name = "rule_type_id", nullable = false)
    private Long ruleTypeId;
    
}