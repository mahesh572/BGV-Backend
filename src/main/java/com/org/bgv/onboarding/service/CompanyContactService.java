package com.org.bgv.onboarding.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.onboarding.dto.CompanyContactRequest;
import com.org.bgv.onboarding.dto.CompanyContactResponse;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.onboarding.entity.CompanyContact;
import com.org.bgv.onboarding.repository.CompanyContactRepository;
import com.org.bgv.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyContactService {

	private final CompanyRepository companyRepository;
    private final CompanyContactRepository companyContactRepository;
	
    public CompanyContactResponse addContact(
            Long companyId,
            CompanyContactRequest request) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        if (request.isPrimaryContact()) {

            companyContactRepository.findByCompanyId(companyId)
                    .forEach(contact -> contact.setPrimaryContact(false));
        }

        CompanyContact contact = CompanyContact.builder()
                .company(company)
                .contactType(request.getContactType())
                .name(request.getName())
                .title(request.getTitle())
                .email(request.getEmail())
                .phone(request.getPhone())
                .primaryContact(request.isPrimaryContact())
                .build();

        companyContactRepository.save(contact);

        return mapToResponse(contact);
    }
    
    
    public CompanyContactResponse updateContact(
            Long contactId,
            CompanyContactRequest request) {

        CompanyContact contact =
                companyContactRepository.findById(contactId)
                        .orElseThrow(() ->
                                new RuntimeException("Contact not found"));

        if (request.isPrimaryContact()) {

            companyContactRepository.findByCompanyId(
                    contact.getCompany().getId())
                    .forEach(c -> c.setPrimaryContact(false));
        }

        contact.setContactType(request.getContactType());
        contact.setName(request.getName());
        contact.setTitle(request.getTitle());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setPrimaryContact(request.isPrimaryContact());

        companyContactRepository.save(contact);

        return mapToResponse(contact);
    }
    
    
    
    @Transactional(readOnly = true)
    public List<CompanyContactResponse> getContacts(Long companyId) {

        return companyContactRepository.findByCompanyId(companyId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    
    @Transactional(readOnly = true)
    public CompanyContactResponse getPrimaryContact(Long companyId) {

        CompanyContact contact =
                companyContactRepository
                        .findByCompanyIdAndPrimaryContactTrue(companyId)
                        .orElseThrow(() ->
                                new RuntimeException("Primary contact not found"));

        return mapToResponse(contact);
    }
    
    public void deleteContact(Long contactId) {

        CompanyContact contact =
                companyContactRepository.findById(contactId)
                        .orElseThrow(() ->
                                new RuntimeException("Contact not found"));

        companyContactRepository.delete(contact);
    }
    
    private CompanyContactResponse mapToResponse(
            CompanyContact contact) {

        return CompanyContactResponse.builder()
                .id(contact.getId())
                .contactType(contact.getContactType())
                .name(contact.getName())
                .title(contact.getTitle())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .primaryContact(contact.isPrimaryContact())
                .build();
    }
    
}
