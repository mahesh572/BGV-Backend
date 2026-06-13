package com.org.bgv.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.entity.ActivityTimeline;

@Repository
public interface ActivityTimelineRepository extends JpaRepository<ActivityTimeline, Long> {
    
	List<ActivityTimeline> findByCandidateCandidateIdOrderByCreatedAtDesc(Long candidateId);

	List<ActivityTimeline> findByCandidateUuidOrderByCreatedAtDesc(String candidateUuid);
/*
	List<ActivityTimeline> findByCandidateAndStatusOrderByCreatedAtDesc(
	        Candidate candidate, String status);
	        */
	
	List<ActivityTimeline> findByCheckIdAndObjectIdOrderByCreatedAtDesc(
            Long checkId,
            Long objectId
    );
	
}