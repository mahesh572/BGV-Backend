package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.BgvPackage;

@Repository
public interface BgvPackageRepository extends JpaRepository<BgvPackage, Long> {
    
    Optional<BgvPackage> findByCode(String code);
    
    List<BgvPackage> findByIsActiveTrue();
    
    boolean existsByCode(String code);
    
    boolean existsByCodeAndPackageIdNot(String code, Long packageId);
    
    @Query("SELECT p FROM BgvPackage p LEFT JOIN FETCH p.packageCheckCategories WHERE p.packageId = :packageId")
    Optional<BgvPackage> findByIdWithCategories(@Param("packageId") Long packageId);
}