package com.org.bgv.vendor.entity;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionType;
import com.org.bgv.vendor.dto.ReasonLevel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "action_reason",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actionReasonId;

    @Column(nullable = false, length = 50)
    private String code; // DOC_BLURRY, SEC_INSUFFICIENT_DATA

    @Column(nullable = false, length = 150)
    private String label; // Display text

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActionLevel level; 
    // SECTION / OBJECT / DOCUMENT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CheckCategory category; 
    // Identity / Education / Work Experience (nullable = common reasons)
    
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Column(nullable = false)
    private Boolean active = true;
    
    private Boolean requiresEvidence;
    private Boolean requiresRemarks;
    private Boolean terminal;

    private Integer sortOrder;
}
