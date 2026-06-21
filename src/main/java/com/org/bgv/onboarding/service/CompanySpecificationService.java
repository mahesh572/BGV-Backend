package com.org.bgv.onboarding.service;

import java.util.List;

import com.org.bgv.onboarding.dto.CompanySpecificationRequest;
import com.org.bgv.onboarding.dto.CompanySpecificationResponse;

public interface CompanySpecificationService {

    List<CompanySpecificationResponse> getSpecifications(Long companyId);

    void saveOrUpdateSpecifications(
            Long companyId,
            List<CompanySpecificationRequest> request);

}