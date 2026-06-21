package com.org.bgv.onboarding.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.common.entity.Country;
import com.org.bgv.common.entity.StateRegion;
import com.org.bgv.common.repository.CountryRepository;
import com.org.bgv.common.repository.StateRegionRepository;
import com.org.bgv.onboarding.dto.CompanyAddressRequest;
import com.org.bgv.onboarding.dto.CompanyAddressResponse;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.onboarding.entity.CompanyAddress;
import com.org.bgv.onboarding.repository.CompanyAddressRepository;
import com.org.bgv.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyAddressService {

	private final CompanyRepository companyRepository;
    private final CompanyAddressRepository companyAddressRepository;
    private final StateRegionRepository stateRepository;
    private final CountryRepository countryRepository;
	
	
    public CompanyAddressResponse addAddress(
            Long companyId,
            CompanyAddressRequest request) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        StateRegion state = stateRepository.findById(request.getStateId())
                .orElseThrow(() ->
                        new RuntimeException("State not found"));

        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() ->
                        new RuntimeException("Country not found"));

        CompanyAddress address = CompanyAddress.builder()
                .company(company)
                .addressType(request.getAddressType())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(state)
                .country(country)
                .zipCode(request.getZipCode())
                .primaryAddress(request.isPrimaryAddress())
                .build();

        companyAddressRepository.save(address);

        return mapToResponse(address);
    }
    
    public CompanyAddressResponse updateAddress(
            Long addressId,
            CompanyAddressRequest request) {

        CompanyAddress address =
                companyAddressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new RuntimeException("Address not found"));

        StateRegion state = stateRepository.findById(request.getStateId())
                .orElseThrow(() ->
                        new RuntimeException("State not found"));

        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() ->
                        new RuntimeException("Country not found"));

        address.setAddressType(request.getAddressType());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(state);
        address.setCountry(country);
        address.setZipCode(request.getZipCode());
       // address.setPrimaryAddress(request.isPrimaryAddress());

        companyAddressRepository.save(address);

        return mapToResponse(address);
    }
    
    @Transactional(readOnly = true)
    public List<CompanyAddressResponse> getAddresses(Long companyId) {

        return companyAddressRepository.findByCompanyId(companyId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    public void deleteAddress(Long addressId) {

        CompanyAddress address =
                companyAddressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new RuntimeException("Address not found"));

        companyAddressRepository.delete(address);
    }
    
    private CompanyAddressResponse mapToResponse(
            CompanyAddress address) {

        return CompanyAddressResponse.builder()
                .id(address.getId())
                .addressType(address.getAddressType())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState().getName())
                .country(address.getCountry().getName())
                .zipCode(address.getZipCode())
               // .primaryAddress(address.isPrimaryAddress())
                .build();
    }
}
