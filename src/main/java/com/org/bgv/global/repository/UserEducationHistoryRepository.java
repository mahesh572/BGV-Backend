package com.org.bgv.global.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.global.entity.UserEducationHistory;

@Repository
public interface UserEducationHistoryRepository
        extends JpaRepository<UserEducationHistory, Long> {

    /* ---------- Core queries ---------- */

    // All education records for a user
    List<UserEducationHistory> findByUser_UserId(Long userId);

    // All education records for a user + profile
    List<UserEducationHistory> findByUser_UserIdAndProfile_ProfileId(
            Long userId,
            Long profileId
    );
    
    Optional<UserEducationHistory> findByUser_UserIdAndId(Long userId, Long educationId);

    /* ---------- Optional helpers ---------- */

    boolean existsByUser_UserId(Long userId);

    long countByUser_UserId(Long userId);

    void deleteByUser_UserIdAndId(Long userId, Long educationId);
}
