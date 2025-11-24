package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "identity_proofs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentityProof {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;
    
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    

    @Column(name = "document_number", length = 100)
    private String documentNumber;
    
    private Date issueDate;
    private Date expiryDate;
    private Long docTypeId;
    
    @Column(name = "status", length = 50)
    private String status; // e.g. PENDING, VERIFIED, REJECTED

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    
    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
}
