package com.org.bgv.vendor.entity;

import java.time.LocalDateTime;

import com.org.bgv.vendor.enums.LocationSource;
import com.org.bgv.vendor.enums.VisitLocationType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class FieldVisitLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private FieldVisitAssignment assignment;

    private Double latitude;

    private Double longitude;

    private String address;

    private Double accuracy;

    @Enumerated(EnumType.STRING)
    private LocationSource source; // GPS, MANUAL, HYBRID

    @Enumerated(EnumType.STRING)
    private VisitLocationType visitType;

    private LocalDateTime capturedAt;

    private Long capturedBy;
}