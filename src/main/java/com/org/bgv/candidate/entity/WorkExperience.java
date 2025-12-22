package com.org.bgv.candidate.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.org.bgv.entity.Profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;



@Entity
@Table(name = "work_experiance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experiance_id")
    private Long experienceId;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    
    @Column(name = "candidate_id")
    private Long candidateId;
    
    private String company_name;
    private String position;
    private LocalDate start_date;
    private LocalDate end_date;
    private String reason;
    private String employee_id;
    private String manager_email_id;
    private String hr_email_id;
    private String address;
    
    private Boolean currentlyWorking;
    private String city;
    private String country;
    private String state;
    private String noticePeriod;
    private String employmentType;

    
    @Column(name = "verified", nullable = false)
    private boolean verified = false;
    
    @Column(name = "verification_status", length = 50)
    private String verificationStatus = "pending";
    
    @Column(name = "verified_by", length = 100)
    private String verifiedBy;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    // getters and setters
    
 // Helper methods
    public int getDurationInMonths() {
        if (start_date == null) {
            return 0;
        }
        
        LocalDate end = currentlyWorking ? LocalDate.now() : end_date;
        if (end == null) {
            return 0;
        }
        
        return (int) java.time.temporal.ChronoUnit.MONTHS.between(
            start_date.withDayOfMonth(1),
            end.withDayOfMonth(1)
        );
    }
    
    public double getDurationInYears() {
        int months = getDurationInMonths();
        return months / 12.0;
    }
    
}