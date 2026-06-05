package com.org.bgv.vendor.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.vendor.entity.FieldVisitAssignment;

@Repository
public interface FieldVisitAssignmentRepository
        extends JpaRepository<FieldVisitAssignment, Long> {

    Optional<FieldVisitAssignment> findByExecutionExecutionId(Long executionId);

    List<FieldVisitAssignment> findByFieldAgentUserId(Long userId);

    boolean existsByExecutionExecutionId(Long executionId);
}
