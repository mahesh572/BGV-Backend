package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.org.bgv.constants.VerificationStatus;

@Entity
@Table(name = "candidate_case_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateCaseDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_document_id")
    private Long caseDocumentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CandidateCase candidateCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_category_id", nullable = false)
    private CheckCategory checkCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;

    @Column(name = "is_add_on", nullable = false)
    private Boolean isAddOn;

    @Column(name = "required", nullable = false)
    private Boolean required;

    @Column(name = "document_price", nullable = false)
    private Double documentPrice;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    private String documentUrl;
    
    private LocalDateTime uploadedAt;
    private LocalDateTime verifiedAt;
    private String verificationNotes;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
