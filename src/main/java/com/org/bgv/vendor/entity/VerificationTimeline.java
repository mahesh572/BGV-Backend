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
@Table(name = "verification_timeline")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationTimeline {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timeline_id")
    private Long timelineId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_check_id", nullable = false)
    private VerificationCaseCheck verificationCaseCheck;
    
    @Column(name = "action")
    private String action;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "performed_by")
    private String performedBy;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
