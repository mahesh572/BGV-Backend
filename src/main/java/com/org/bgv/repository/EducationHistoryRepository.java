package com.org.bgv.repository;

import com.org.bgv.entity.EducationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EducationHistoryRepository extends JpaRepository<EducationHistory, Long> {
    List<EducationHistory> findByProfile_ProfileId(Long profileId);
    
    // Method 1: Using derived query method (Spring Data JPA will generate the query)
    @Transactional
    @Modifying
    void deleteByProfile_ProfileId(Long profileId);
    
    // Method 2: Using custom JPQL query (alternative approach)
    @Transactional
    @Modifying
    @Query("DELETE FROM EducationHistory e WHERE e.profile.profileId = :profileId")
    void deleteAllByProfileId(@Param("profileId") Long profileId);
    
    // Method 3: Using native SQL query (alternative approach)
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM education_history WHERE profile_id = :profileId", nativeQuery = true)
    void deleteByProfileIdNative(@Param("profileId") Long profileId);
}