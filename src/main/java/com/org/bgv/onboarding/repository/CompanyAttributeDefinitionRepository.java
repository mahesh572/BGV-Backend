package com.org.bgv.onboarding.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.company.dto.CompanyType;
import com.org.bgv.onboarding.entity.CompanyAttributeDefinition;

@Repository
public interface CompanyAttributeDefinitionRepository
        extends JpaRepository<CompanyAttributeDefinition, Long> {

    /**
     * Get all attribute definitions for a company type ordered by display order.
     */
    List<CompanyAttributeDefinition> findByCompanyTypeOrderByDisplayOrderAsc(
            CompanyType companyType);

    /**
     * Check whether definitions already exist for a company type.
     */
    boolean existsByCompanyType(CompanyType companyType);

    /**
     * Find a particular attribute definition.
     */
    CompanyAttributeDefinition findByCompanyTypeAndAttributeCode(
            CompanyType companyType,
            String attributeCode);

    /**
     * Find all required attributes for a company type.
     */
    List<CompanyAttributeDefinition> findByCompanyTypeAndRequiredTrueOrderByDisplayOrderAsc(
            CompanyType companyType);

    /**
     * Find by attribute code.
     */
    List<CompanyAttributeDefinition> findByAttributeCode(String attributeCode);
}
