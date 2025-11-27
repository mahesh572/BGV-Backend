package com.org.bgv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.PackageCheckCategoryAllowedDocument;

@Repository
public interface PackageCheckCategoryAllowedDocumentRepository extends JpaRepository<PackageCheckCategoryAllowedDocument, Long> {
    
    List<PackageCheckCategoryAllowedDocument> findByBgvPackagePackageId(Long packageId);
    
    List<PackageCheckCategoryAllowedDocument> findByBgvPackagePackageIdAndCheckCategoryCategoryId(Long packageId, Long checkCategoryId);
    
    @Modifying
    @Query("DELETE FROM PackageCheckCategoryAllowedDocument pccad WHERE pccad.bgvPackage.packageId = :packageId")
    void deleteByPackageId(@Param("packageId") Long packageId);
    
    @Modifying
    @Query("DELETE FROM PackageCheckCategoryAllowedDocument pccad WHERE pccad.bgvPackage.packageId = :packageId AND pccad.checkCategory.categoryId = :categoryId")
    void deleteByPackageIdAndCategoryId(@Param("packageId") Long packageId, @Param("categoryId") Long categoryId);
}
