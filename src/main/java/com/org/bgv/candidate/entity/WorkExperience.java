package com.org.bgv.candidate.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.org.bgv.dto.document.DocumentTypeDto;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_check_id")
    private VerificationCaseCheck verificationCaseCheck;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private VerificationCase verificationCase;
    
    @Column(name = "company_id")
    private Long companyId;
    
    @Column(name="company_name")
    private String companyName;

    private String position;
    
    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;
    
    private String reason;
   
    @Column(name="employee_id")
    private String employeeId;
   
    @Column(name="hr_email_id")
    private String hrEmailId;

    @Column(name="manager_email_id")
    private String managerEmailId;
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
    
    
 // Helper methods
    public int getDurationInMonths() {
        if (startDate == null) {
            return 0;
        }
        
        LocalDate end = currentlyWorking ? LocalDate.now() : endDate;
        if (end == null) {
            return 0;
        }
        
        return (int) java.time.temporal.ChronoUnit.MONTHS.between(
        		startDate.withDayOfMonth(1),
            end.withDayOfMonth(1)
        );
    }
    
    public double getDurationInYears() {
        int months = getDurationInMonths();
        return months / 12.0;
    }
    
}