package com.org.bgv.vendor.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.enums.VendorNoteType;

@Entity
@Table(name = "vendor_note")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorNote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private Long noteId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_check_id", nullable = false)
    private VerificationCaseCheck verificationCaseCheck;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Enumerated(EnumType.STRING)
    private VendorNoteType type; // internal, verification, insufficiency
    
    private Boolean visibleToEmployer;

    private Boolean visibleToCandidate;

    
    @Column(name = "is_internal")
    private boolean isInternal;
}