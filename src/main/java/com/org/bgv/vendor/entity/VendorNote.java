package com.org.bgv.vendor.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.org.bgv.entity.VerificationCaseCheck;

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
    
    @Column(name = "type")
    private String type; // internal, verification, insufficiency
    
    @Column(name = "is_internal")
    private boolean isInternal;
}