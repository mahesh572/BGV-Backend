package com.org.bgv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.BgvPackage;
import com.org.bgv.entity.PackageCheckCategoryRuleType;

@Repository
public interface PackageCheckCategoryRuleTypeRepository extends JpaRepository<PackageCheckCategoryRuleType, Long>{

	// Get all rule mappings for a package
    List<PackageCheckCategoryRuleType> findByBgvPackagePackageId(Long packageId);

    // Find rule for a specific check category inside the package
    List<PackageCheckCategoryRuleType> findByBgvPackagePackageIdAndCheckCategoryId(
            Long packageId, Long checkCategoryId
    );

    // Delete all rules for a package
    void deleteByBgvPackagePackageId(Long packageId);

    // Check whether a rule already exists for this package + check
    boolean existsByBgvPackagePackageIdAndCheckCategoryId(
            Long packageId, Long checkCategoryId
    );
    boolean existsByBgvPackagePackageIdAndCheckCategoryIdAndRuleTypeId(
            Long packageId, 
            Long checkCategoryId,
            Long ruleTypeId
    );
	/*
	 * void deleteByBgvPackageAndCheckCategoryIdAndRuleTypeId( Long packageId, Long
	 * checkCategoryId, Long ruleTypeId );
	 */
    
    List<PackageCheckCategoryRuleType> findByBgvPackageAndCheckCategoryId(
            BgvPackage bgvPackage, 
            Long checkCategoryId
        );
    
    void deleteByBgvPackageAndCheckCategoryIdAndRuleTypeId(
            BgvPackage bgvPackage, 
            Long checkCategoryId, 
            Long ruleTypeId
        );
        
}
