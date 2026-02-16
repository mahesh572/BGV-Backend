package com.org.bgv.bgvpackage.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.bgvpackage.entity.PackageCheckCategoryAllowedRuleType;

@Repository
public interface PackageCheckCategoryAllowedRuleTypeRepository
        extends JpaRepository<PackageCheckCategoryAllowedRuleType, Long> {
	
	List<PackageCheckCategoryAllowedRuleType>
	findByBgvPackage_PackageIdAndCheckCategory_CategoryId(
	        Long packageId,
	        Long categoryId
	);
	
	
	boolean existsByBgvPackage_PackageIdAndCheckCategory_CategoryIdAndRuleType_RuleTypeId(
	        Long packageId,
	        Long categoryId,
	        Long ruleTypeId
	);

	void deleteByBgvPackage_PackageIdAndCheckCategory_CategoryId(
	        Long packageId,
	        Long categoryId
	);
	
	
}

