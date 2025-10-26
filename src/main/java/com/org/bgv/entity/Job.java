package com.org.bgv.entity;



import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Job {
 
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false)
 private String title;

 private String department;

 @Column(columnDefinition = "TEXT")
 private String description;

 @Column(columnDefinition = "TEXT")
 private String requirements;

 @Column(columnDefinition = "TEXT")
 private String responsibilities;

 @ElementCollection
 @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
 @Column(name = "skill")
 @Builder.Default
 private List<String> skills = new ArrayList<>();

 @Embedded
 @AttributeOverrides({
     @AttributeOverride(name = "city", column = @Column(name = "location_city")),
     @AttributeOverride(name = "state", column = @Column(name = "location_state")),
     @AttributeOverride(name = "country", column = @Column(name = "location_country")),
     @AttributeOverride(name = "remote", column = @Column(name = "location_remote"))
 })
 private Location location;

 @Embedded
 @AttributeOverrides({
     @AttributeOverride(name = "min", column = @Column(name = "salary_min")),
     @AttributeOverride(name = "max", column = @Column(name = "salary_max")),
     @AttributeOverride(name = "currency", column = @Column(name = "salary_currency")),
     @AttributeOverride(name = "period", column = @Column(name = "salary_period"))
 })
 private Salary salary;

 private String employmentType;
 private String experienceLevel;
 private String educationLevel;

 private Integer vacancies;

 @Column(name = "application_deadline")
 private LocalDateTime applicationDeadline;

 private String status;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "company_id")
 private Company company;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "created_by")
 private User createdBy;

 @CreationTimestamp
 @Column(name = "created_at", updatable = false)
 private LocalDateTime createdAt;

 @UpdateTimestamp
 @Column(name = "updated_at")
 private LocalDateTime updatedAt;

 /*
 @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
 @Builder.Default
 private List<JobApplication> applications = new ArrayList<>();
*/
 
 // Embedded classes for complex types
 @Embeddable
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 @Builder
 public static class Location {
     private String city;
     private String state;
     private String country;
     @Builder.Default
     private Boolean remote = false;
 }

 @Embeddable
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 @Builder
 public static class Salary {
     private Double min;
     private Double max;
     @Builder.Default
     private String currency = "USD";
     @Builder.Default
     private String period = "yearly";
 }

 
}
