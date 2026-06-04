package com.org.bgv.entity;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.enums.ActivityType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "activity_timeline")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===============================
    // 🔥 UNIVERSAL ENTITY MODEL
    // ===============================

    private Long caseId;

    private Long checkId;

    private Long executionId;

    private Long documentId;

    private Long noteId;

    // ===============================
    // 🔥 ACTIVITY DETAILS
    // ===============================

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "activity_status")
    private String activityStatus; // COMPLETED, FAILED, PENDING

    @Column(name = "severity")
    private String severity; // INFO, WARN, ERROR

    // ===============================
    // 👤 ACTOR DETAILS
    // ===============================

    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "actor_role")
    private String actorRole; // SYSTEM, VENDOR, HR, ADMIN

    @Column(name = "source")
    private String source; // UI, API, SYSTEM

    // ===============================
    // 🔁 STATE TRANSITION TRACKING
    // ===============================

    @Column(name = "status_from")
    private String statusFrom;

    @Column(name = "status_to")
    private String statusTo;

    // ===============================
    // 📦 FLEXIBLE PAYLOAD (IMPORTANT)
    // ===============================

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String,Object> metadata;

    // ===============================
    // 👤 CANDIDATE LINK (OPTIONAL BUT USEFUL)
    // ===============================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    // ===============================
    // ⏱ AUDIT FIELDS
    // ===============================

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}