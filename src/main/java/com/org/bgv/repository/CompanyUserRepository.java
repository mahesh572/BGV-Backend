package com.org.bgv.repository;

import com.org.bgv.entity.CompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyUserRepository extends JpaRepository<CompanyUser, Long> {
    
    // Method 1: Using property path (if User entity has userId field)
    List<CompanyUser> findByUserUserId(Long userId);
    
    // Method 2: Using @Query (more explicit)
    @Query("SELECT cu FROM CompanyUser cu WHERE cu.user.userId = :userId")
    List<CompanyUser> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT cu FROM CompanyUser cu WHERE cu.company.id = :companyId")
    List<CompanyUser> findByCompanyId(@Param("companyId") Long companyId);
    
    @Query("SELECT cu FROM CompanyUser cu WHERE cu.company.id = :companyId AND cu.user.userId = :userId")
    Optional<CompanyUser> findByCompanyIdAndUserId(@Param("companyId") Long companyId, @Param("userId") Long userId);
    
    @Query("SELECT CASE WHEN COUNT(cu) > 0 THEN true ELSE false END FROM CompanyUser cu WHERE cu.company.id = :companyId AND cu.user.userId = :userId")
    boolean existsByCompanyIdAndUserId(@Param("companyId") Long companyId, @Param("userId") Long userId);
    
    // Find all active company associations for a user
    @Query("SELECT cu FROM CompanyUser cu WHERE cu.user.userId = :userId")
    List<CompanyUser> findUserCompanies(@Param("userId") Long userId);
    
    // Find all users for a company
    @Query("SELECT cu FROM CompanyUser cu WHERE cu.company.id = :companyId")
    List<CompanyUser> findCompanyUsers(@Param("companyId") Long companyId);
    
    @Modifying
    @Query(value = "INSERT INTO company_users (company_id, user_id, created_at) " +
                   "VALUES (:companyId, :userId, :createdAt) " +
                   "ON CONFLICT (company_id, user_id) DO NOTHING", 
           nativeQuery = true)
    void insertCompanyUser(@Param("companyId") Long companyId, 
                          @Param("userId") Long userId,
                          @Param("createdAt") LocalDateTime createdAt);
}