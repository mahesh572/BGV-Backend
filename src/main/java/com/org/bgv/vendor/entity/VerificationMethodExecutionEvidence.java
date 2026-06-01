package com.org.bgv.vendor.entity;


import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "verification_method_execution_evidence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationMethodExecutionEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =========================
       Parent Method Execution
       ========================= */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "method_execution_id", nullable = false)
    private VerificationMethodExecution methodExecution;

   

    /* =========================
       Document Reference (optional)
       ========================= */
    @Column(name = "document_id")
    private Long documentId;

    /* =========================
       File Evidence (optional)
       ========================= */
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String storageKey;

    /* =========================
       Additional Context
       ========================= */
    @Column(length = 500)
    private String remarks;

    /* =========================
       Audit
       ========================= */
    @Column(nullable = false)
    private Long uploadedBy;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private Boolean archived = false;

    /* =========================
       Auto timestamp
       ========================= */
    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
        if (this.archived == null) {
            this.archived = false;
        }
    }
}
