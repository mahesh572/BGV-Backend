package com.org.bgv.vendor.entity;

import com.org.bgv.entity.CheckCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "category_evidence_types",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"category_id", "evidence_type_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEvidenceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identity / Education / Work Experience
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CheckCategory category;

    // Evidence type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_type_id", nullable = false)
    private EvidenceType evidenceType;

    // Category-specific rules
    @Column(name = "mandatory", nullable = false)
    private Boolean mandatory = false;

    @Column(name = "max_files")
    private Integer maxFiles;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
