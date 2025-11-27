package com.org.bgv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "package_checkcategory_allowed_document"
    
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageCheckCategoryAllowedDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pcad_id")
    private Long id;

    // FK → Package
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private BgvPackage bgvPackage;

    // FK → Check Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_category_id", nullable = false)
    private CheckCategory checkCategory;

    // FK → Document Type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;

    // REQUIRED / OPTIONAL
    @Column(name = "is_required", nullable = false)
    private Boolean required;

    // Useful if ruleType is MIN_2 or ANY_1 (optional)
    @Column(name = "priority_order")
    private Integer priorityOrder;
}
