package com.org.bgv.candidate.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.org.bgv.entity.ActivityTimeline;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.JobApplication;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "candidates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Long candidateId;
    
    @Column(name = "candidate_ref", nullable = false, unique = true)
    private String candidateRef;
    
    /*
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
     
            
    @Column(name = "nationality")
    private String nationality;

    @Column(name = "gender")
    private String gender;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
    
    @Column(name = "marital_status")
    private String maritalStatus;
    
    @Column(name = "email_address")
    private String emailAddress;

    */
    
    private String sourceType;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;
    
    
    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;
    
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "verification_status")
    private String verificationStatus; // PENDING, VERIFIED, REJECTED
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;
    
    @Column(name = "job_search_status")
    private String jobSearchStatus; // ACTIVE, PASSIVE, NOT_LOOKING
    
    @Column(name = "isConsentProvided")
    private Boolean isConsentProvided;
    
    // One-to-Many with Applications
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JobApplication> jobApplications = new ArrayList();
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    
    
    @OneToMany(mappedBy = "candidate", 
            cascade = CascadeType.ALL, 
            fetch = FetchType.LAZY,
            orphanRemoval = true)
   @OrderBy("timestamp DESC")
   private List<ActivityTimeline> activityTimeline = new ArrayList<>();
    
    // Helper method to add activity
    public void addActivity(ActivityTimeline activity) {
        activityTimeline.add(activity);
        activity.setCandidate(this);
    }
    
    // Helper method to remove activity
    public void removeActivity(ActivityTimeline activity) {
        activityTimeline.remove(activity);
        activity.setCandidate(null);
    }
    
 // Lifecycle Methods
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastActiveAt = LocalDateTime.now();
        
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.isVerified == null) {
            this.isVerified = false;
        }
        if (this.verificationStatus == null) {
            this.verificationStatus = "PENDING";
        }
        if (this.jobSearchStatus == null) {
            this.jobSearchStatus = "ACTIVE";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}