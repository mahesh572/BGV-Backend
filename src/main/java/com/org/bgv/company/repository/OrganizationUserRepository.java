package com.org.bgv.company.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.company.entity.OrganizationUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, Long> {

    // All orgs a user belongs to
    List<OrganizationUser> findByUserUserId(Long userId);

    @Query("SELECT ou FROM OrganizationUser ou WHERE ou.user.userId = :userId")
    List<OrganizationUser> findByUserId(@Param("userId") Long userId);

    // All users of an organization
    @Query("SELECT ou FROM OrganizationUser ou WHERE ou.organization.id = :organizationId")
    List<OrganizationUser> findByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("""
        SELECT ou FROM OrganizationUser ou
        WHERE ou.organization.id = :organizationId
          AND ou.user.userId = :userId
    """)
    Optional<OrganizationUser> findByOrganizationIdAndUserId(
        @Param("organizationId") Long organizationId,
        @Param("userId") Long userId
    );

    @Query("""
        SELECT CASE WHEN COUNT(ou) > 0 THEN true ELSE false END
        FROM OrganizationUser ou
        WHERE ou.organization.id = :organizationId
          AND ou.user.userId = :userId
    """)
    boolean existsByOrganizationIdAndUserId(
        @Param("organizationId") Long organizationId,
        @Param("userId") Long userId
    );

    // Native insert (PostgreSQL)
    @Modifying
    @Query(value = """
        INSERT INTO organization_users (organization_id, user_id, created_at)
        VALUES (:organizationId, :userId, :createdAt)
        ON CONFLICT (organization_id, user_id) DO NOTHING
        """, nativeQuery = true)
    void insertOrganizationUser(
        @Param("organizationId") Long organizationId,
        @Param("userId") Long userId,
        @Param("createdAt") LocalDateTime createdAt
    );
}
