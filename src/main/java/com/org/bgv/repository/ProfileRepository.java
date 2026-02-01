package com.org.bgv.repository;

import com.org.bgv.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    Optional<Profile> findByPhoneNumber(String phoneNumber);
    
    // FIXED: Use the actual field name 'userId' from User entity
    Profile findByUserUserId(Long userId);
    
    // FIXED: Correct JPQL query to use the actual field names
    @Query("SELECT p.profileId FROM Profile p WHERE p.user.userId = :userId")
    Optional<Long> findProfileIdByUserId(@Param("userId") Long userId);
    
    // FIXED: Use the correct property name
    boolean existsByUserUserId(Long userId);
    
   // Optional<Profile> findByEmailAddress(String emailAddress);
    
    @Query("SELECT p FROM Profile p WHERE p.user.email = :email")
    Optional<Profile> findByUserEmail(@Param("email") String email);
    
    // Alternative method names that will also work:
    Optional<Profile> findByUser_UserId(Long userId);
    
    // verification
    
  //  Optional<Profile> findByCandidateId(Long candidateId);
    
    
  //  boolean existsByCandidateId(Long candidateId);
    
  //  boolean existsByEmailAddress(String email);
    
  //  @Query("SELECT cp FROM Profile cp WHERE cp.candidateId = :candidateId AND cp.status = 'active'")
  //  Optional<Profile> findActiveByCandidateId(@Param("candidateId") Long candidateId);

}