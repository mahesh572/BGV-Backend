package com.org.bgv.repository;

import com.org.bgv.entity.EmployerPackageDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployerPackageDocumentRepository extends JpaRepository<EmployerPackageDocument, Long> {
    
    List<EmployerPackageDocument> findByEmployerPackageId(Long employerPackageId);
    
    List<EmployerPackageDocument> findByEmployerPackageIdAndCheckCategoryCategoryId(Long employerPackageId, Long categoryId);
    
    @Query("SELECT epd FROM EmployerPackageDocument epd WHERE epd.employerPackage.id = :employerPackageId AND epd.includedInBase = true")
    List<EmployerPackageDocument> findIncludedDocumentsByPackage(@Param("employerPackageId") Long employerPackageId);
    
    @Query("SELECT epd FROM EmployerPackageDocument epd WHERE epd.employerPackage.id = :employerPackageId AND epd.includedInBase = false")
    List<EmployerPackageDocument> findAddonDocumentsByPackage(@Param("employerPackageId") Long employerPackageId);
    
    void deleteByEmployerPackageId(Long employerPackageId);
    
    
}