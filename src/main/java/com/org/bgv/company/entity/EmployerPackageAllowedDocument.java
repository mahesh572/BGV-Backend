package com.org.bgv.company.entity;

import com.org.bgv.constants.SelectionType;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.EmployerPackage;

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
@Table(
    name = "employer_package_allowed_document",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"employer_package_id", "check_category_id", "document_type_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerPackageAllowedDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 FK → Employer Package
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_package_id", nullable = false)
    private EmployerPackage employerPackage;

    // 🔹 Flattened for performance
    @Column(name = "check_category_id", nullable = false)
    private Long checkCategoryId;

    // 🔹 Document
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;

    // 🔹 Admin copied fields
    @Column(name = "is_required")
    private Boolean required;

    @Column(name = "priority_order")
    private Integer priorityOrder;

    // 🔥 Employer customization

    // Whether included in base package
    @Column(name = "included_in_base")
    private Boolean includedInBase;

    // Add-on price (if not included)
    @Column(name = "addon_price")
    private Double addonPrice;

    // Whether selected by default
    @Column(name = "default_selected")
    private Boolean defaultSelected;

    // UI / logic support (MANDATORY / OPTIONAL)
    @Column(name = "selection_type")
    @Enumerated(EnumType.STRING)
    private SelectionType selectionType;
}
