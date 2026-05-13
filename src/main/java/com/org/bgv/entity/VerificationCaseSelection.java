package com.org.bgv.entity;

import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.VerificationCase;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "verification_case_selection")
public class VerificationCaseSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    

    @ManyToOne
    private VerificationCase verificationCase;

    private Long referenceId; // educationId / workId

    @Enumerated(EnumType.STRING)
    private CheckCategoryEnum type; // EDUCATION / WORK

    private String status; // PENDING / VERIFIED / REJECTED

    private String remarks;
}
