package com.org.bgv.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    
    private String company_name;
    private String position;
    private LocalDate start_date;
    private LocalDate end_date;
    private String reason;
    private Long employee_id;
    private String manager_email_id;
    private String hr_email_id;
    private String address;
    
    private Boolean currentlyWorking;
    private String city;
    private String country;
    private String state;
    private String noticePeriod;
    private String employmentType;

    // getters and setters
}