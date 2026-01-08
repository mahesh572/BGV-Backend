package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.org.bgv.candidate.entity.Candidate;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(name = "application_status")
    private String applicationStatus; // APPLIED, UNDER_REVIEW, SHORTLISTED, INTERVIEW, OFFERED, HIRED, REJECTED

    @Column(name = "applied_date")
    private LocalDateTime appliedDate;

    @Column(name = "cover_letter", length = 3000)
    private String coverLetter;

    @Column(name = "resume_version")
    private String resumeVersion;

    @Column(name = "notes")
    private String notes;

    @Column(name = "source")
    private String source;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.appliedDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        if (this.applicationStatus == null) {
            this.applicationStatus = "APPLIED";
        }
    }
}
