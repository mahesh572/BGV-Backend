package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "type", length = 50, unique = true, nullable = false)
    private String type; // e.g., "account_creation", "password_reset"

    @Column(name = "subject", length = 255, nullable = false)
    private String subject;

    @Column(name = "body_html", columnDefinition = "TEXT", nullable = false)
    private String bodyHtml;

    @Column(name = "body_text", columnDefinition = "TEXT")
    private String bodyText;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Pre-persist callback
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
}

