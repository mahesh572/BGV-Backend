package com.org.bgv.vendor.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.vendor.dto.VerificationMethodDTO;
import com.org.bgv.vendor.repository.CheckVerificationMethodRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationMethodService {

    private final CheckVerificationMethodRepository repository;

    public List<VerificationMethodDTO> getMethodsForCheck(
            CheckCategoryEnum checkType) {

        return repository.findByCheckType(checkType)
                .stream()
                .map(mapping -> VerificationMethodDTO.builder()
                        .methodId(mapping.getVerificationMethod().getMethodId())
                        .code(mapping.getVerificationMethod().getCode())
                        .name(mapping.getVerificationMethod().getName())
                        .build())
                .toList();
    }
}