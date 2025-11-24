package com.org.bgv.repository;

import com.org.bgv.entity.PackageCheckCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageCheckCategoryRepository extends JpaRepository<PackageCheckCategory, Long> {
    
	List<PackageCheckCategory> findByBgvPackage_PackageId(Long packageId);

    List<PackageCheckCategory> findByCategory_CategoryId(Long categoryId);

    @Modifying
    @Query("DELETE FROM PackageCheckCategory pcc WHERE pcc.bgvPackage.packageId = :packageId")
    void deleteByPackageId(@Param("packageId") Long packageId);
}