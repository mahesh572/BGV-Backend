package com.org.bgv.company.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.org.bgv.entity.User;

@Entity
@Table(
    name = "organization_users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization_id", "user_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Direct ID fields for easy querying (read-only)
    @Column(name = "organization_id", insertable = false, updatable = false)
    private Long organizationId;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

