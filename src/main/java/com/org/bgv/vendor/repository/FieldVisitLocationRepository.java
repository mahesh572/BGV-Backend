package com.org.bgv.vendor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.vendor.entity.FieldVisitLocation;

@Repository
public interface FieldVisitLocationRepository
        extends JpaRepository<FieldVisitLocation, Long> {

    List<FieldVisitLocation> findByAssignmentAssignmentIdOrderByCapturedAtAsc(
            Long assignmentId);
    
    Optional<FieldVisitLocation>
    findTopByAssignmentAssignmentIdOrderByCapturedAtDesc(
            Long assignmentId);

}