package com.org.bgv.onboarding.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.onboarding.entity.CompanyAttributeDefinition;
import com.org.bgv.onboarding.entity.CompanyAttributeOption;

@Repository
public interface CompanyAttributeOptionRepository
        extends JpaRepository<CompanyAttributeOption, Long> {

    /**
     * Get options for an attribute ordered by display order.
     */
    List<CompanyAttributeOption> findByAttributeDefinitionOrderByDisplayOrderAsc(
            CompanyAttributeDefinition attributeDefinition);

    /**
     * Get option by code for an attribute.
     */
    CompanyAttributeOption findByAttributeDefinitionAndOptionCode(
            CompanyAttributeDefinition attributeDefinition,
            String optionCode);

    /**
     * Check whether options exist for an attribute.
     */
    boolean existsByAttributeDefinition(
            CompanyAttributeDefinition attributeDefinition);

    /**
     * Get all options for multiple attribute definitions.
     */
    List<CompanyAttributeOption> findByAttributeDefinitionInOrderByDisplayOrderAsc(
            List<CompanyAttributeDefinition> attributeDefinitions);
}
