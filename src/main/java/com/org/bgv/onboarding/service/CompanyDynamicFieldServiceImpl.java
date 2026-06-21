package com.org.bgv.onboarding.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.company.dto.CompanyType;
import com.org.bgv.onboarding.dto.CompanyDynamicFieldDto;
import com.org.bgv.onboarding.dto.CompanyDynamicFieldOptionDto;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.onboarding.entity.CompanyAttributeDefinition;
import com.org.bgv.onboarding.entity.CompanyAttributeValue;
import com.org.bgv.onboarding.repository.CompanyAttributeDefinitionRepository;
import com.org.bgv.onboarding.repository.CompanyAttributeOptionRepository;
import com.org.bgv.onboarding.repository.CompanyAttributeValueRepository;
import com.org.bgv.repository.CompanyRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyDynamicFieldServiceImpl implements CompanyDynamicFieldService {

    private final CompanyRepository companyRepository;

    private final CompanyAttributeDefinitionRepository
            definitionRepository;

    private final CompanyAttributeOptionRepository
            optionRepository;

    private final CompanyAttributeValueRepository
            valueRepository;
    
    @Override
    public List<CompanyDynamicFieldDto> getDynamicFields(
            CompanyType companyType) {

        List<CompanyAttributeDefinition> definitions =
                definitionRepository
                        .findByCompanyTypeOrderByDisplayOrderAsc(companyType);

        return definitions.stream()
                .map(definition -> {

                    List<CompanyDynamicFieldOptionDto> options =
                            optionRepository
                                    .findByAttributeDefinitionOrderByDisplayOrderAsc(definition)
                                    .stream()
                                    .map(option ->
                                            CompanyDynamicFieldOptionDto.builder()
                                                    .code(option.getOptionCode())
                                                    .label(option.getOptionLabel())
                                                    .build())
                                    .toList();

                    return CompanyDynamicFieldDto.builder()
                            .definitionId(definition.getId())
                            .attributeCode(definition.getAttributeCode())
                            .attributeLabel(definition.getAttributeLabel())
                            .controlType(definition.getControlType())
                            .dataType(definition.getDataType())
                            .required(definition.getRequired())
                            .displayOrder(definition.getDisplayOrder())
                            .value(null)
                            .options(options)
                            .build();

                })
                .toList();
    }

    @Override
    public List<CompanyDynamicFieldDto> getDynamicFields(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Company not found"));

        List<CompanyAttributeDefinition> definitions =
                definitionRepository
                        .findByCompanyTypeOrderByDisplayOrderAsc(
                                company.getCompanyType());

        List<CompanyAttributeValue> values =
                valueRepository
                        .findByCompanyOrderByAttributeDefinition_DisplayOrderAsc(
                                company);

        Map<Long, String> valueMap =
                values.stream()
                        .collect(Collectors.toMap(
                                v -> v.getAttributeDefinition().getId(),
                                CompanyAttributeValue::getAttributeValue));

        return definitions.stream()
                .map(definition -> {

                    List<CompanyDynamicFieldOptionDto> options =
                            optionRepository
                                    .findByAttributeDefinitionOrderByDisplayOrderAsc(
                                            definition)
                                    .stream()
                                    .map(option ->
                                            CompanyDynamicFieldOptionDto.builder()
                                                    .code(option.getOptionCode())
                                                    .label(option.getOptionLabel())
                                                    .build())
                                    .toList();

                    return CompanyDynamicFieldDto.builder()
                            .definitionId(definition.getId())
                            .attributeCode(definition.getAttributeCode())
                            .attributeLabel(definition.getAttributeLabel())
                            .controlType(definition.getControlType())
                            .dataType(definition.getDataType())
                            .required(definition.getRequired())
                            .displayOrder(definition.getDisplayOrder())
                            .value(valueMap.get(definition.getId()))
                            .options(options)
                            .build();

                })
                .toList();
    }
}