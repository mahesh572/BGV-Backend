package com.org.bgv.vendor.entity;

import java.time.LocalDateTime;

import com.org.bgv.entity.VerificationCaseCheck;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "verification_check_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationCheckHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_check_id", nullable = false)
    private VerificationCaseCheck verificationCaseCheck;
    
    @Column(name = "action")
    private String action; // status_update, note_added, evidence_uploaded
    
    @Column(name = "from_status")
    private String fromStatus;
    
    @Column(name = "to_status")
    private String toStatus;
    
    @Column(name = "performed_by")
    private String performedBy;
    
    @Column(name = "performed_by_id")
    private Long performedById;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
