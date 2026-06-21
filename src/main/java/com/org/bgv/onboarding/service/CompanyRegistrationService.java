package com.org.bgv.onboarding.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.common.repository.CountryRepository;
import com.org.bgv.common.repository.StateRegionRepository;
import com.org.bgv.onboarding.dto.CreateCompanyRequest;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.onboarding.repository.CompanyAddressRepository;
import com.org.bgv.onboarding.repository.CompanyContactRepository;
import com.org.bgv.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyRegistrationService {
	
	private final CompanyRepository companyRepository;
    private final CompanyContactRepository companyContactRepository;
    private final CompanyAddressRepository companyAddressRepository;
    private final CountryRepository countryRepository;
    private final StateRegionRepository stateRegionRepository;
    
    
    @Transactional
    public Long createCompany(CreateCompanyRequest request) {

        Company company = new Company();

        company.setCompanyName(request.getCompanyName());
        company.setCompanyType(request.getCompanyType());
        company.setLegalType(request.getLegalType());
        company.setRegistrationNumber(request.getRegistrationNumber());
        company.setTaxId(request.getTaxId());
        company.setIncorporationDate(request.getIncorporationDate());
        company.setIndustry(request.getIndustry());
        company.setCompanySize(request.getCompanySize());
        company.setTanNumber(request.getTanNumber());
        company.setWebsite(request.getWebsite());
        company.setLinkedinProfile(request.getLinkedinProfile());

        company.setStatus("DRAFT");

        companyRepository.save(company);

        return company.getId();
    }
    
    @Transactional
    public void submitRegistration(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (companyContactRepository.findByCompanyId(companyId).isEmpty()) {
            throw new RuntimeException("At least one contact is required");
        }

        if (companyAddressRepository.findByCompanyId(companyId).isEmpty()) {
            throw new RuntimeException("At least one address is required");
        }

        company.setStatus("PENDING_APPROVAL");

        companyRepository.save(company);
    }
    
    
    @Transactional
    public void approveCompany(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        company.setStatus("ACTIVE");

        companyRepository.save(company);
    }
    
    @Transactional
    public void rejectCompany(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        company.setStatus("REJECTED");

        companyRepository.save(company);
    }
    
}
