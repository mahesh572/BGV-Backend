package com.org.bgv.onboarding.service;

import java.util.List;

import com.org.bgv.company.dto.CompanyType;
import com.org.bgv.onboarding.dto.CompanyDynamicFieldDto;

public interface CompanyDynamicFieldService {

    List<CompanyDynamicFieldDto> getDynamicFields(Long companyId);

	List<CompanyDynamicFieldDto> getDynamicFields(CompanyType companyType);

}
