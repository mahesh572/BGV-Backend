package com.org.bgv.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.vendor.entity.VerificationTimeline;

@Repository
public interface VerificationTimelineRepository extends JpaRepository<VerificationTimeline, Long> {
    List<VerificationTimeline> findByVerificationCaseCheckOrderByTimestampAsc(VerificationCaseCheck check);
}
