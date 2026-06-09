package com.org.bgv.onboarding.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.Company;
import com.org.bgv.onboarding.entity.CompanyAttributeDefinition;
import com.org.bgv.onboarding.entity.CompanyAttributeValue;

@Repository
public interface CompanyAttributeValueRepository
        extends JpaRepository<CompanyAttributeValue, Long> {

    /**
     * Get all attribute values for a company.
     */
    List<CompanyAttributeValue> findByCompany(Company company);

    /**
     * Get attribute values ordered by display order.
     */
    List<CompanyAttributeValue>
    findByCompanyOrderByAttributeDefinition_DisplayOrderAsc(
            Company company);

    /**
     * Find a specific attribute value.
     */
    Optional<CompanyAttributeValue>
    findByCompanyAndAttributeDefinition(
            Company company,
            CompanyAttributeDefinition attributeDefinition);

    /**
     * Check whether a value exists.
     */
    boolean existsByCompanyAndAttributeDefinition(
            Company company,
            CompanyAttributeDefinition attributeDefinition);

    /**
     * Delete all attributes for a company.
     */
    void deleteByCompany(Company company);

    /**
     * Find values for multiple companies.
     */
    List<CompanyAttributeValue> findByCompanyIn(
            List<Company> companies);

    /**
     * Find by attribute code.
     */
    List<CompanyAttributeValue>
    findByAttributeDefinition_AttributeCode(
            String attributeCode);
}
