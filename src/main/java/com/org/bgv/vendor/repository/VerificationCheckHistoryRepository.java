package com.org.bgv.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.vendor.entity.VerificationCheckHistory;

@Repository
public interface VerificationCheckHistoryRepository extends JpaRepository<VerificationCheckHistory, Long> {
    List<VerificationCheckHistory> findByVerificationCaseCheckOrderByTimestampDesc(VerificationCaseCheck check);
}