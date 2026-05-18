package com.org.bgv.company.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.company.entity.EmployerPackageCheckCategoryAllowedRuleType;

@Repository
public interface EmployerPackageCheckCategoryAllowedRuleTypeRepository
        extends JpaRepository<EmployerPackageCheckCategoryAllowedRuleType, Long> {

    // 🔹 Get all allowed rules for an employer package
    List<EmployerPackageCheckCategoryAllowedRuleType>
    findByEmployerPackage_Id(Long employerPackageId);

    // 🔹 Get by package + category
    List<EmployerPackageCheckCategoryAllowedRuleType>
    findByEmployerPackage_IdAndCheckCategoryId(
            Long employerPackageId,
            Long checkCategoryId
    );

    // 🔹 Get by package + multiple categories (bulk)
    List<EmployerPackageCheckCategoryAllowedRuleType>
    findByEmployerPackage_IdAndCheckCategoryIdIn(
            Long employerPackageId,
            Set<Long> categoryIds
    );

    // 🔹 Get specific rule
    Optional<EmployerPackageCheckCategoryAllowedRuleType>
    findByEmployerPackage_IdAndCheckCategoryIdAndRuleType_RuleTypeId(
            Long employerPackageId,
            Long checkCategoryId,
            Long ruleTypeId
    );

	/*
	 * // 🔹 Bulk fetch for rules List<EmployerPackageCheckCategoryAllowedRuleType>
	 * findByEmployerPackage_IdAndRuleTypeIdIn1( Long employerPackageId, Set<Long>
	 * ruleTypeIds );
	 */

    // 🔹 Delete all rules for package (useful on reassign/reset)
    void deleteByEmployerPackage_Id(Long employerPackageId);
}