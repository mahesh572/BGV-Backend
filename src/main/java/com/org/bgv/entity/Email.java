package com.org.bgv.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.org.bgv.notifications.entity.EmailTemplate;

@Entity
@Table(name = "emails")
public class Email {
    
    public enum EmailStatus {
        PENDING, SENT, DELIVERED, FAILED, OPENED, CLICKED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_template_id", nullable = false)
    private EmailTemplate emailTemplate;

    @Column(name = "recipient_email", length = 255, nullable = false)
    private String recipientEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id")
    private User recipientUser; // Assuming you have a User entity

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private EmailStatus status = EmailStatus.PENDING;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Pre-persist callback
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}