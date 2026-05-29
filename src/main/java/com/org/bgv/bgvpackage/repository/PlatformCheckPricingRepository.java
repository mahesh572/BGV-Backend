package com.org.bgv.bgvpackage.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.bgvpackage.entity.EmployerCheckPricing;
import com.org.bgv.bgvpackage.entity.PlatformCheckPricing;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.PricingType;



@Repository
public interface PlatformCheckPricingRepository 
        extends JpaRepository<PlatformCheckPricing, Long> {

    // Find active pricing by category
    List<PlatformCheckPricing> findByCheckCategoryAndActiveTrue(CheckCategory checkCategory);

    // Find active pricing by category + rule type
    Optional<PlatformCheckPricing> 
        findByCheckCategoryAndRuleTypeAndActiveTrue(
            CheckCategory checkCategory,
            RuleTypes ruleType
        );

    // Find by pricing type
    List<PlatformCheckPricing> findByPricingTypeAndActiveTrue(PricingType pricingType);

    // Get all active pricing
    List<PlatformCheckPricing> findByActiveTrue();
    
    List<PlatformCheckPricing> 
    findByCheckCategory_CategoryIdAndActiveTrue(Long categoryId);
    
    Optional<PlatformCheckPricing> findByCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(Long categoryId,Long ruleTypeId);

}
