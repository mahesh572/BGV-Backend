package com.org.bgv.vendor.service;


import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.entity.User;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.exceptions.ResourceNotFoundException;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.VerificationCaseCheckRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorActionService {
	
	

   private final VerificationCaseCheckRepository verificationCaseCheckRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;


    

    public void assignVendorAgent(Long companyId,
                                  Long checkId,
                                  Long vendorAgentUserId) {

        // ✅ Fetch Company
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Company not found with id: " + companyId));

        // ✅ Fetch Case Check
        VerificationCaseCheck caseCheck =
                verificationCaseCheckRepository.findById(checkId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Verification check not found with id: " + checkId));

        // ✅ Fetch Vendor Agent User
        User vendorUser = userRepository.findById(vendorAgentUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + vendorAgentUserId));

        // ✅ Validate user belongs to same company (optional but recommended)
        if (caseCheck.getVendorCompany() != null &&
                !caseCheck.getVendorCompany().getId().equals(companyId)) {

            throw new IllegalStateException("Vendor company mismatch for this check");
        }

        // ✅ Assign values
        caseCheck.setVendorCompany(company);
        caseCheck.setAssignedVendorUser(vendorUser);
        caseCheck.setStatus(CaseCheckStatus.AGENT_ASSIGNED);
        caseCheck.setAssignedAt(LocalDateTime.now());

        // ✅ Save
        verificationCaseCheckRepository.save(caseCheck);
    }

    
}
