package com.org.bgv.service.util;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.company.dto.CompanyType;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.mapper.UserMapper;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.CompanyUserRepository;
import com.org.bgv.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CompanyServiceUtil {
	
	 private final CompanyRepository companyRepository;
	
	 @Transactional(readOnly = true)
	    public Company getDefaultCompany() {

	        return companyRepository
	                .findByCompanyType(CompanyType.DEFAULT)
	                .orElseThrow(() ->
	                        new BusinessException("Default company not configured."));
	    }
	 
	 
	 
	 
	 

}
