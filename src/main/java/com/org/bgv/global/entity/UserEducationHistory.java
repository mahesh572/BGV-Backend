package com.org.bgv.global.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.org.bgv.entity.DegreeType;
import com.org.bgv.entity.FieldOfStudy;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_education_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEducationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ---------- Ownership ---------- */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    /* ---------- Education Details ---------- */

    @ManyToOne
    @JoinColumn(name = "degree_id")
    private DegreeType degree;

    @ManyToOne
    @JoinColumn(name = "field_id")
    private FieldOfStudy field;

    @Column(name = "institute_name")
    private String instituteName;

    @Column(name = "university_name")
    private String universityName;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String city;
    private String state;
    private String country;

    private Integer yearOfPassing;
    private String typeOfEducation; // Full-time / Part-time / Distance

    private String grade;
    private Double gpa;

    @Column(length = 1000)
    private String description;

    /* ---------- Audit ---------- */

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
