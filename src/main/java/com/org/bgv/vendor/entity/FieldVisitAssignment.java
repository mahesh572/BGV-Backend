package com.org.bgv.vendor.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.org.bgv.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldVisitAssignment {

	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    @ManyToOne
    private VerificationMethodExecution execution;
    
    private LocalDateTime assignedAt;

    @ManyToOne
    private User fieldAgent;

    private String visitAddress;

  //  private Double latitude;

 //   private Double longitude;

    private String outcome;

    private LocalDate scheduledDate;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private String remarks;
    
  //  private LocalDateTime locationCapturedAt;

 //   private Long locationUpdatedBy;
}