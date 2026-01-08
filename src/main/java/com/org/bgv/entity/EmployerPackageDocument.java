package com.org.bgv.entity;

import java.time.LocalDateTime;

import com.org.bgv.constants.SelectionType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employer_package_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerPackageDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employer_package_document_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_package_id", nullable = false)
    private EmployerPackage employerPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_category_id", nullable = false)
    private CheckCategory checkCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;

    @Column(name = "addon_price")
    private Double addonPrice;

    @Column(name = "included_in_base")
    private Boolean includedInBase;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "selection_type")
    private SelectionType selectionType;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
}

