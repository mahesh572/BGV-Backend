package com.org.bgv.vendor.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.vendor.dto.VerificationMethodDTO;
import com.org.bgv.vendor.dto.VerificationMethodFieldDTO;
import com.org.bgv.vendor.entity.VerificationMethod;
import com.org.bgv.vendor.repository.CheckVerificationMethodRepository;
import com.org.bgv.vendor.repository.VerificationMethodFieldRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationMethodService {

    private final CheckVerificationMethodRepository repository;
    private final VerificationMethodFieldRepository fieldRepository;

    public List<VerificationMethodDTO> getMethodsForCheck(
            CheckCategoryEnum checkType) {

        return repository.findByCheckType(checkType)
                .stream()
                .map(mapping -> {

                    VerificationMethod method =
                            mapping.getVerificationMethod();

                    List<VerificationMethodFieldDTO> fields =
                            fieldRepository
                                    .findByVerificationMethodMethodIdOrderByDisplayOrderAsc(
                                            method.getMethodId())
                                    .stream()
                                    .map(field -> VerificationMethodFieldDTO.builder()
                                            .fieldId(field.getId())
                                            .fieldName(field.getFieldName())
                                            .fieldLabel(field.getFieldLabel())
                                            .fieldType(field.getFieldType())
                                            .requiredField(field.getRequiredField())
                                            .displayOrder(field.getDisplayOrder())
                                            .placeholder(field.getPlaceholder())
                                            .build())
                                    .toList();

                    return VerificationMethodDTO.builder()
                            .methodId(method.getMethodId())
                            .code(method.getCode())
                            .name(method.getName())
                            .description(method.getDescription())
                            .mandatory(mapping.getMandatory())
                            .fields(fields)
                            .build();
                })
                .toList();
    }
}