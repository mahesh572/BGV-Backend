package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.org.bgv.constants.EmployerPackageStatus;

@Entity
@Table(name = "employer_package")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerPackage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employer_package_id")
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private BgvPackage bgvPackage;

    @Column(name = "base_price", nullable = false)
    private Double basePrice;
    
    @Column(name = "addon_price")
    @Builder.Default
    private Double addonPrice = 0.0;
    
    @Column(name = "total_price")
    private Double totalPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private EmployerPackageStatus status = EmployerPackageStatus.DRAFT;
    
    @Column(name = "valid_from")
    private LocalDateTime validFrom;
    
    @Column(name = "valid_until")
    private LocalDateTime validUntil;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "employerPackage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EmployerPackageDocument> selectedDocuments = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods for validity checking
    public boolean isValid() {
        if (status != EmployerPackageStatus.ACTIVE) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        boolean validFromCheck = validFrom == null || !now.isBefore(validFrom);
        boolean validUntilCheck = validUntil == null || !now.isAfter(validUntil);
        
        return validFromCheck && validUntilCheck;
    }
    
    public boolean isExpired() {
        return validUntil != null && LocalDateTime.now().isAfter(validUntil);
    }
    
    public boolean canAssignToCandidates() {
        return isValid() && !isExpired();
    }
    
    // Getters and setters for validFrom and validUntil
    public LocalDateTime getValidFrom() {
        return validFrom;
    }
    
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }
    
    public LocalDateTime getValidUntil() {
        return validUntil;
    }
    
    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }
}