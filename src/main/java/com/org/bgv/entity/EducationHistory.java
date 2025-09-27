package com.org.bgv.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // getters and setters
}