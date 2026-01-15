package com.org.bgv.global.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.global.entity.UserIdentityProof;

@Repository
public interface UserIdentityProofRepository
        extends JpaRepository<UserIdentityProof, Long> {

    /* ---------- User level ---------- */

	Optional<UserIdentityProof> findByUser_UserId(Long userId);

    Optional<UserIdentityProof> findByUser_UserIdAndPrimaryTrue(Long userId);
    
    Optional<UserIdentityProof> findByUser_UserIdAndDocTypeId(Long userId,Long docTypeId);

    boolean existsByUser_UserIdAndDocTypeId(Long userId, Long docTypeId);

    /* ---------- Profile level ---------- */

    List<UserIdentityProof> findByProfile_ProfileId(Long profileId);

    Optional<UserIdentityProof> findByProfile_ProfileIdAndPrimaryTrue(Long profileId);

    /* ---------- Document type ---------- */

    List<UserIdentityProof> findByDocTypeId(Long docTypeId);
}