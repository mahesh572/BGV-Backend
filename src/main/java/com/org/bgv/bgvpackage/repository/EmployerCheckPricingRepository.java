package com.org.bgv.bgvpackage.repository;

import com.org.bgv.bgvpackage.entity.EmployerCheckPricing;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.onboarding.entity.Company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployerCheckPricingRepository
        extends JpaRepository<EmployerCheckPricing, Long> {

    // Get pricing for employer + category
    Optional<EmployerCheckPricing>
    findByCompanyAndCheckCategoryAndActiveTrue(
            Company company,
            CheckCategory checkCategory
    );

    // Get all pricing configs for employer
    List<EmployerCheckPricing>
    findByCompanyAndActiveTrue(Company company);

    // Check if pricing exists
    boolean existsByCompanyAndCheckCategory(
            Company company,
            CheckCategory checkCategory
    );
    
    
    Optional<EmployerCheckPricing>
    findByCompanyAndCheckCategoryAndRuleTypeAndActiveTrue(
    		Company company,
            CheckCategory category,
            RuleTypes ruleType
    );
    
    
    
    Optional<EmployerCheckPricing> findByCompany_IdAndCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(
            Long companyId,
            Long categoryId,
            Long ruleTypeId
    );
    
    List<EmployerCheckPricing> 
    findAllByCompany_IdAndCheckCategory_CategoryIdInAndRuleType_RuleTypeIdInAndActiveTrue(
        Long companyId,
        Set<Long> categoryIds,
        Set<Long> ruleTypeIds
    );

    List<EmployerCheckPricing> findByCompany_IdAndCheckCategory_CategoryIdAndActiveTrue(
            Long companyId,
            Long categoryId
    );
    
    Optional<EmployerCheckPricing>
    findByCompany_IdAndCheckCategory_CategoryIdAndRuleType_RuleTypeId(
            Long companyId,
            Long categoryId,
            Long ruleTypeId
    );
    
}
