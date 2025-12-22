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
    
    List<CandidateVerification> findByEmployerId(String employerId);
    
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
}
