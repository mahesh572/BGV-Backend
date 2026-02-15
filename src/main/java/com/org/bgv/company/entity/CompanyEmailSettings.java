package com.org.bgv.company.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_email_settings",
       uniqueConstraints = @UniqueConstraint(columnNames = "company_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyEmailSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Tenant
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    // 📧 Sender info
    @Column(name = "from_name", nullable = false)
    private String fromName;

    @Column(name = "from_email", nullable = false)
    private String fromEmail;

    @Column(name = "reply_to_email")
    private String replyToEmail;

    @Column(name = "support_email")
    private String supportEmail;

    // 📡 SMTP / Provider
    @Column(name = "smtp_provider", nullable = false)
    private String smtpProvider; // SES, SENDGRID, SMTP

    @Column(name = "smtp_config_json", columnDefinition = "TEXT")
    private String smtpConfigJson; // encrypted JSON

    // ✅ Status
    @Column(name = "is_active")
    private boolean active;

    @Column(name = "is_verified")
    private boolean verified;

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
