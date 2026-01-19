package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.org.bgv.common.EmailTemplateType;
import com.org.bgv.notifications.PolicySource;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "email_templates")
public class EmailTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "type", length = 50, nullable = false)
    private String type; // e.g., "account_creation", "password_reset"

    private Long companyId; // NULL = platform default
    
    private String templateCode;
    
    /*
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private EmailTemplateType type;
    */
    @Column(name = "subject", length = 255, nullable = false)
    private String subject;

    
    @Lob
    @Column(name = "body_html", columnDefinition = "TEXT")
    private String bodyHtml;

    @Column(name = "body_text", columnDefinition = "TEXT")
    private String bodyText;
    
    @Enumerated(EnumType.STRING)
    private PolicySource source;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Pre-persist callback
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

