package com.org.bgv.global.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.org.bgv.entity.Profile;
import com.org.bgv.entity.User;

@Entity
@Table(name = "user_work_experience")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experience_id")
    private Long id;

    /* =====================
       OWNERSHIP
       ===================== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    /* =====================
       WORK DETAILS
       ===================== */

    @Column(name = "company_name")
    private String companyName;

    private String position;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "currently_working")
    private Boolean currentlyWorking;

    private String employmentType;   // Full-Time / Contract / Intern
    private String noticePeriod;

    private String reason;            // Reason for leaving
    private String employeeId;

    private String managerEmailId;
    private String hrEmailId;

    private String address;
    private String city;
    private String state;
    private String country;

    /* =====================
       AUDIT
       ===================== */

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* =====================
       DERIVED HELPERS
       ===================== */

    public int getDurationInMonths() {
        if (startDate == null) return 0;

        LocalDate end = Boolean.TRUE.equals(currentlyWorking)
                ? LocalDate.now()
                : endDate;

        if (end == null) return 0;

        return (int) java.time.temporal.ChronoUnit.MONTHS.between(
                startDate.withDayOfMonth(1),
                end.withDayOfMonth(1)
        );
    }

    public double getDurationInYears() {
        return getDurationInMonths() / 12.0;
    }
}
