package com.org.bgv.candidate.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.org.bgv.entity.DegreeType;
import com.org.bgv.entity.FieldOfStudy;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;

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
@Table(name = "education_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_check_id")
    private VerificationCaseCheck verificationCaseCheck;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private VerificationCase verificationCase;
    
    @Column(name = "company_id")
    private Long companyId;

    @ManyToOne
    @JoinColumn(name = "degree_id") // Graduation/Associate/Bachelor etc.
    private DegreeType degree;

    @ManyToOne
    @JoinColumn(name = "field_id")
    private FieldOfStudy field;

    private String institute_name;
    private String university_name;
    private LocalDate fromDate;      // Start date (constructed from month/year)
    private LocalDate toDate;        // End date (constructed from month/year)

    private String city;
    private String state;
    private String country;
    private Integer yearOfPassing;
    private String typeOfEducation;  // Regular/Full Time, Part-Time, Distance etc.
    
    private String grade;
    private Double gpa;
    private String description;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column(name = "verified")
    private boolean verified = false;
    
    @Column(name = "verification_status")
    private String verificationStatus = "pending";
    
    @Column(name = "verified_by")
    private String verifiedBy;

    // getters and setters
}