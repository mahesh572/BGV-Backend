package com.org.bgv.company.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.company.entity.EmployerPackageSelectedRule;

@Repository
public interface EmployerPackageSelectedRuleRepository 
        extends JpaRepository<EmployerPackageSelectedRule, Long> {

    // 🔹 Get all selected rules for a package
    List<EmployerPackageSelectedRule> findByEmployerPackageId(Long employerPackageId);

    // 🔹 Get all selected rules for a package + category
    List<EmployerPackageSelectedRule> findByEmployerPackageIdAndCheckCategoryId(
            Long employerPackageId, Long checkCategoryId);

    // 🔹 Get specific selected rule
    Optional<EmployerPackageSelectedRule> 
        findByEmployerPackageIdAndCheckCategoryIdAndRuleTypeRuleTypeId(
            Long employerPackageId,
            Long checkCategoryId,
            Long ruleTypeId
        );

    // 🔹 Check if rule already selected (avoid duplicates)
    boolean existsByEmployerPackageIdAndCheckCategoryIdAndRuleTypeRuleTypeId(
            Long employerPackageId,
            Long checkCategoryId,
            Long ruleTypeId
        );

    // 🔹 Delete all rules for a package (useful for reset/update)
    void deleteByEmployerPackageId(Long employerPackageId);

    // 🔹 Delete rules by package + category
    void deleteByEmployerPackageIdAndCheckCategoryId(
            Long employerPackageId, Long checkCategoryId);

}