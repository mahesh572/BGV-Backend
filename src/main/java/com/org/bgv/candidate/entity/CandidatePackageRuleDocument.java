package com.org.bgv.candidate.entity;

import java.math.BigDecimal;

import com.org.bgv.entity.VerificationCase;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "candidate_package_rule_document")
public class CandidatePackageRuleDocument {

	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Link to rule
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private CandidatePackageRule rule;

    // 🔗 DIRECT link to case (🔥 IMPORTANT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private VerificationCase verificationCase;

    // 🔗 Category (optional but useful)
    @Column(name = "category_id")
    private Long categoryId;

    // 🔗 Which document
    @Column(name = "document_type_id", nullable = false)
    private Long documentTypeId;

    // flags
    @Column(name = "is_required")
    private Boolean required;

    @Column(name = "is_selected")
    private Boolean selected;

    // pricing snapshot
    @Column(name = "price")
    private BigDecimal price;
    
    @Column(name = "included_in_package")
    private Boolean includedInPackage = false;
}