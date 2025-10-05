package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"company_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Direct ID fields for easier querying
    @Column(name = "company_id", insertable = false, updatable = false)
    private Long companyId;
    
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;
    
    // Entity relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}