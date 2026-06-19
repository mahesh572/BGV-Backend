package com.org.bgv.common.entity;

import com.org.bgv.dto.CheckCategoryEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(name = "field_configuration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // EDUCATION, EMPLOYMENT, IDENTITY
    @Enumerated(EnumType.STRING)
    private CheckCategoryEnum checkType;

    // instituteName
    private String fieldName;

    // Institute Name
    private String displayName;

    // STRING, NUMBER, DATE
    private String dataType;

    // Candidate must enter?
    private Boolean mandatory;

    // Compare with source?
    private Boolean comparable;

    // Visible in UI?
    private Boolean visible;

    // Show in report?
    private Boolean reportable;

    // Order in UI
    private Integer sequenceNo;
}
