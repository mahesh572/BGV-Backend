package com.org.bgv.vendor.entity;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.vendor.dto.RejectionLevel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "rejection_reason",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RejectionReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rejectionReasonId;

    @Column(nullable = false, length = 50)
    private String code; // DOC_BLURRY, SEC_INSUFFICIENT_DATA

    @Column(nullable = false, length = 150)
    private String label; // Display text

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RejectionLevel level; 
    // SECTION / OBJECT / DOCUMENT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CheckCategory category; 
    // Identity / Education / Work Experience (nullable = common reasons)

    @Column(nullable = false)
    private Boolean active = true;

    private Integer sortOrder;
}
