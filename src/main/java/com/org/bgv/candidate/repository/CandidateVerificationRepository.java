package com.org.bgv.candidate.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.candidate.entity.CandidateVerification;
import com.org.bgv.constants.VerificationStatus;

@Repository
public interface CandidateVerificationRepository extends JpaRepository<CandidateVerification, Long> {
    
    Optional<CandidateVerification> findByCandidateId(Long candidateId);
    
    Optional<CandidateVerification> findByCandidateIdAndVerificationCaseCaseId(Long candidateId,Long caseId);
    
  //  List<CandidateVerification> findByEmployerId(String employerId);
    
    List<CandidateVerification> findByStatus(VerificationStatus status);
    
    List<CandidateVerification> findByDueDateBeforeAndStatusNot(LocalDateTime dueDate, VerificationStatus status);
    
    @Query("SELECT cv FROM CandidateVerification cv WHERE cv.candidateId = :candidateId AND cv.status IN :statuses")
    List<CandidateVerification> findByCandidateIdAndStatusIn(
        @Param("candidateId") Long candidateId,
        @Param("statuses") List<VerificationStatus> statuses
    );
    
    @Query("SELECT COUNT(cv) FROM CandidateVerification cv WHERE cv.candidateId = :candidateId")
    Long countByCandidateId(@Param("candidateId") Long candidateId);
    
    boolean existsByCandidateIdAndStatus(Long candidateId, VerificationStatus status);
    
   // List<CandidateVerification> findByCandidateId(Long candidateId);
    
    // Find active verification for candidate
    @Query("SELECT cv FROM CandidateVerification cv WHERE cv.candidateId = :candidateId " +
           "AND cv.status IN ('PENDING', 'IN_PROGRESS', 'SUBMITTED') " +
           "ORDER BY cv.createdAt DESC")
    List<CandidateVerification> findActiveVerifications(@Param("candidateId") Long candidateId);
    
    // Find latest verification for candidate
    Optional<CandidateVerification> findFirstByCandidateIdOrderByCreatedAtDesc(Long candidateId);
    
    // Check if verification exists for candidate
    boolean existsByCandidateId(Long candidateId);
    
    // Find by candidate ID and status
    List<CandidateVerification> findByCandidateIdAndStatus(Long candidateId, String status);
    
    // Find overdue verifications
    @Query("SELECT cv FROM CandidateVerification cv WHERE cv.candidateId = :candidateId " +
           "AND cv.dueDate < CURRENT_TIMESTAMP " +
           "AND cv.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<CandidateVerification> findOverdueVerifications(@Param("candidateId") Long candidateId);

}
