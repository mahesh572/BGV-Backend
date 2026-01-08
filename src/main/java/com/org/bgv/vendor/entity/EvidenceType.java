package com.org.bgv.vendor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "evidence_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvidenceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // System code (used by backend/UI)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code; 
    // e.g. MANUAL_VERIFICATION, DISCREPANCY_PROOF, CLARIFICATION_DOC

    @Column(name = "label", nullable = false, length = 100)
    private String label;
    // e.g. "Manual Verification Evidence"

    @Column(name = "description", length = 255)
    private String description;

    // Rules
    @Column(name = "requires_file", nullable = false)
    private Boolean requiresFile = true;

    @Column(name = "requires_remarks", nullable = false)
    private Boolean requiresRemarks = false;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
