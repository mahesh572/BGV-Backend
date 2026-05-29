package com.org.bgv.company.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.company.entity.EmployerPackageRule;

@Repository
public interface EmployerPackageRuleRepository 
        extends JpaRepository<EmployerPackageRule, Long> {

    List<EmployerPackageRule> findByEmployerPackage_Id(Long employerPackageId);

    List<EmployerPackageRule> findByEmployerPackage_IdAndCheckCategoryId(
            Long employerPackageId, Long checkCategoryId
    );
    
    Optional<EmployerPackageRule> 
    findByEmployerPackage_IdAndRuleTypeId(Long packageId, Long ruleTypeId);

    void deleteByEmployerPackage_Id(Long employerPackageId);
}