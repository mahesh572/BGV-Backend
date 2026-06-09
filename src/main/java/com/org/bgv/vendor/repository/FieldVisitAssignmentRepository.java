package com.org.bgv.vendor.repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.User;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.vendor.entity.FieldVisitAssignment;

@Repository
public interface FieldVisitAssignmentRepository
        extends JpaRepository<FieldVisitAssignment, Long> {

    Optional<FieldVisitAssignment> findByExecutionExecutionId(Long executionId);

    List<FieldVisitAssignment> findByFieldAgentUserId(Long userId);

    boolean existsByExecutionExecutionId(Long executionId);
    
    
    
 // Get all assignments for a field agent
    List<FieldVisitAssignment> findByFieldAgent(User fieldAgent);
    
    // Get paginated assignments for a field agent
    Page<FieldVisitAssignment> findByFieldAgent(User fieldAgent, Pageable pageable);
    
    // Get assignments by status (through execution)
    @Query("SELECT f FROM FieldVisitAssignment f WHERE f.fieldAgent = :fieldAgent AND f.execution.status = :status")
    List<FieldVisitAssignment> findByFieldAgentAndStatus(@Param("fieldAgent") User fieldAgent, 
                                                          @Param("status") VerificationExecutionStatus status);
    
    // Get upcoming assignments (scheduled date >= today)
    @Query("SELECT f FROM FieldVisitAssignment f WHERE f.fieldAgent = :fieldAgent " +
           "AND f.scheduledDate >= :today AND f.execution.status NOT IN ('COMPLETED', 'CANCELLED') " +
           "ORDER BY f.scheduledDate ASC")
    List<FieldVisitAssignment> findUpcomingAssignments(@Param("fieldAgent") User fieldAgent, 
                                                        @Param("today") LocalDate today);
    
    // Get overdue assignments (scheduled date < today and not completed)
    @Query("SELECT f FROM FieldVisitAssignment f WHERE f.fieldAgent = :fieldAgent " +
           "AND f.scheduledDate < :today AND f.execution.status NOT IN ('COMPLETED', 'CANCELLED') " +
           "ORDER BY f.scheduledDate ASC")
    List<FieldVisitAssignment> findOverdueAssignments(@Param("fieldAgent") User fieldAgent, 
                                                       @Param("today") LocalDate today);
    
    // Get assignments by date range
    @Query("SELECT f FROM FieldVisitAssignment f WHERE f.fieldAgent = :fieldAgent " +
           "AND f.scheduledDate BETWEEN :startDate AND :endDate")
    List<FieldVisitAssignment> findByFieldAgentAndScheduledDateBetween(@Param("fieldAgent") User fieldAgent,
                                                                        @Param("startDate") LocalDate startDate,
                                                                        @Param("endDate") LocalDate endDate);
    
    // Get today's assignments
    @Query("SELECT f FROM FieldVisitAssignment f WHERE f.fieldAgent = :fieldAgent " +
           "AND f.scheduledDate = :today")
    List<FieldVisitAssignment> findTodayAssignments(@Param("fieldAgent") User fieldAgent, 
                                                     @Param("today") LocalDate today);
    
    // Get active assignments (not completed or cancelled)
    @Query("SELECT f FROM FieldVisitAssignment f WHERE f.fieldAgent = :fieldAgent " +
           "AND f.execution.status IN ('INITIATED', 'VISIT_ASSIGNED', 'VISIT_IN_PROGRESS')")
    List<FieldVisitAssignment> findActiveAssignments(@Param("fieldAgent") User fieldAgent);
    
    // Count assignments by status
    @Query("SELECT f.execution.status, COUNT(f) FROM FieldVisitAssignment f " +
           "WHERE f.fieldAgent = :fieldAgent GROUP BY f.execution.status")
    List<Object[]> countAssignmentsByStatus(@Param("fieldAgent") User fieldAgent);
}
