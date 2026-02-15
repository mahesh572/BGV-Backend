package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "platform_email_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformEmailSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 📧 Sender info
    @Column(name = "from_name", nullable = false)
    private String fromName;

    @Column(name = "from_email", nullable = false)
    private String fromEmail;

    @Column(name = "reply_to_email")
    private String replyToEmail;

    @Column(name = "support_email")
    private String supportEmail;

    // 📡 SMTP
    @Column(name = "smtp_provider", nullable = false)
    private String smtpProvider;

    @Column(name = "smtp_config_json", columnDefinition = "TEXT")
    private String smtpConfigJson;

    // ✅ Status
    @Column(name = "is_active")
    private boolean active;

    // 🕒 Audit
    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}
