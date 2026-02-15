package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "degree_document_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DegreeDocumentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Degree (BTECH, MBA, 10TH etc)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "degree_id", nullable = false)
    private DegreeType degreeType;

    // Document Type (MARKSHEET, PROVISIONAL, CERTIFICATE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_type_id", nullable = false)
    private DocumentType documentType;

    // Whether this document is mandatory for this degree
    @Column(name = "is_required")
    private Boolean required;

    // Optional: Order of document display
    @Column(name = "display_order")
    private Integer displayOrder;

    // Optional: Active flag
    @Column(name = "active")
    private Boolean active = true;
}
