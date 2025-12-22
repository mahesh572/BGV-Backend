package com.org.bgv.candidate.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.org.bgv.constants.VerificationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "candidate_verifications")
public class CandidateVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;
    
    @Column(name = "package_id")
    private Long packageId;
    
    @Column(name = "package_name")
    private String packageName;
    
    @Column(name = "employer_name")
    private String employerName;
    
    @Column(name = "employer_id")
    private String employerId;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VerificationStatus status = VerificationStatus.PENDING;
    
    @Column(name = "progress_percentage")
    private Integer progressPercentage = 0;
    
    @Column(name = "instructions", length = 1000)
    private String instructions;
    
    @Column(name = "support_email")
    private String supportEmail;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "verification_notes", length = 2000)
    private String verificationNotes;
    
    // JSON fields for section requirements
    @Column(name = "section_requirements", columnDefinition = "json")
    private String sectionRequirements;
    
    @Column(name = "section_status", columnDefinition = "json")
    private String sectionStatus;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
}
