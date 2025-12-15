package com.org.bgv.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.ActivityTimeline;
import com.org.bgv.entity.Candidate;

@Repository
public interface ActivityTimelineRepository extends JpaRepository<ActivityTimeline, Long> {
    
    List<ActivityTimeline> findByCandidateCandidateIdOrderByTimestampDesc(Long candidateId);
    
    List<ActivityTimeline> findByCandidateUuidOrderByTimestampDesc(String candidateUuid);
    
    List<ActivityTimeline> findByCandidateAndStatusOrderByTimestampDesc(
            Candidate candidate, String status);
    
    @Query("SELECT a FROM ActivityTimeline a WHERE a.candidate.candidateId = :candidateId " +
           "AND a.timestamp BETWEEN :startDate AND :endDate ORDER BY a.timestamp DESC")
    List<ActivityTimeline> findByCandidateAndDateRange(
            @Param("candidateId") Long candidateId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}