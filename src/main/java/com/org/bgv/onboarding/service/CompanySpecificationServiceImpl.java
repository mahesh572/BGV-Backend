package com.org.bgv.onboarding.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.onboarding.dto.CompanySpecificationRequest;
import com.org.bgv.onboarding.dto.CompanySpecificationResponse;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.onboarding.entity.CompanyAttributeDefinition;
import com.org.bgv.onboarding.entity.CompanyAttributeValue;
import com.org.bgv.onboarding.repository.CompanyAttributeDefinitionRepository;
import com.org.bgv.onboarding.repository.CompanyAttributeValueRepository;
import com.org.bgv.onboarding.service.CompanySpecificationService;
import com.org.bgv.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanySpecificationServiceImpl
        implements CompanySpecificationService {

    private final CompanyRepository companyRepository;
    private final CompanyAttributeDefinitionRepository definitionRepository;
    private final CompanyAttributeValueRepository valueRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CompanySpecificationResponse> getSpecifications(
            Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        List<CompanyAttributeDefinition> definitions =
                definitionRepository.findByCompanyTypeOrderByDisplayOrderAsc(
                        company.getCompanyType());

        List<CompanySpecificationResponse> responseList =
                new ArrayList<>();

        for (CompanyAttributeDefinition definition : definitions) {

            String value = valueRepository
                    .findByCompanyAndAttributeDefinition(company, definition)
                    .map(CompanyAttributeValue::getAttributeValue)
                    .orElse(null);

            responseList.add(
                    CompanySpecificationResponse.builder()
                            .definitionId(definition.getId())
                            .attributeCode(definition.getAttributeCode())
                            .attributeName(definition.getAttributeName())
                            .attributeLabel(definition.getAttributeLabel())
                            .dataType(definition.getDataType())
                            .controlType(definition.getControlType())
                            .required(definition.getRequired())
                            .displayOrder(definition.getDisplayOrder())
                            .value(value)
                            .build());
        }

        return responseList;
    }

    @Override
    @Transactional
    public void saveOrUpdateSpecifications(
            Long companyId,
            List<CompanySpecificationRequest> requests) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        for (CompanySpecificationRequest request : requests) {

            CompanyAttributeDefinition definition =
                    definitionRepository.findById(request.getDefinitionId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Attribute definition not found"));

            // Optional safety check
            if (!definition.getCompanyType().equals(company.getCompanyType())) {
                throw new RuntimeException(
                        "Attribute does not belong to company type "
                                + company.getCompanyType());
            }

            CompanyAttributeValue attributeValue =
                    valueRepository
                            .findByCompanyAndAttributeDefinition(
                                    company,
                                    definition)
                            .orElseGet(() -> {

                                CompanyAttributeValue newValue =
                                        new CompanyAttributeValue();

                                newValue.setCompany(company);
                                newValue.setAttributeDefinition(definition);

                                return newValue;
                            });

            attributeValue.setAttributeValue(request.getValue());

            valueRepository.save(attributeValue);
        }
    }
}