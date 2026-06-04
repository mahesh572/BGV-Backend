package com.org.bgv.vendor.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.entity.WorkExperience;
import com.org.bgv.candidate.repository.AddressRepository;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.candidate.repository.EducationHistoryRepository;
import com.org.bgv.candidate.repository.WorkExperienceRepository;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.Vendor;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.VendorRepository;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.vendor.dto.EmploymentVerificationData;
import com.org.bgv.vendor.verification.methods.service.VerificationContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationContextUtil {

    private final VerificationCaseCheckRepository checkRepository;
    private final CandidateRepository candidateRepository;
    private final CompanyRepository companyRepository;
    private final VendorRepository vendorRepository;

    private final WorkExperienceRepository employmentRepository;
    private final EducationHistoryRepository educationRepository;
    private final AddressRepository addressRepository;

    public VerificationContext build(
            Long checkId,
            Long objectId,
            String checkType) {

        VerificationCaseCheck check =
                checkRepository.findById(checkId)
                        .orElseThrow(() ->
                                new RuntimeException("Check not found"));

        VerificationCase verificationCase =
                check.getVerificationCase();

        Candidate candidate =
                candidateRepository.findById(
                        verificationCase.getCandidateId())
                        .orElseThrow();

        Company company =
                companyRepository.findById(
                        verificationCase.getCompanyId())
                        .orElseThrow();

        Vendor vendor =
                vendorRepository.findById(
                        check.getVendorId())
                        .orElse(null);

        Object verificationObject =
                loadVerificationObject(
                        objectId,
                        checkType);

        return VerificationContext.builder()
                .verificationCase(verificationCase)
                .verificationCheck(check)
                .candidate(candidate)
                .company(company)
                .vendor(vendor)
                .verificationData(verificationObject)
                .build();
    }

    private Object loadVerificationObject(
            Long objectId,
            String checkType) {

        switch (checkType.toUpperCase()) {

            case "WORK":
                return buildEmploymentVerificationData(objectId);

            case "EDUCATION":
                return educationRepository.findById(objectId)
                        .orElseThrow();

            case "ADDRESS":
                return addressRepository.findById(objectId)
                        .orElseThrow();

            default:
                throw new IllegalArgumentException(
                        "Unsupported check type: " + checkType);
        }
    }
    
    
    private EmploymentVerificationData buildEmploymentVerificationData(
            Long objectId) {

        WorkExperience work = employmentRepository
                .findById(objectId)
                .orElseThrow();

        String employmentPeriod =
                formatDate(work.getStart_date())
                + " - "
                + (Boolean.TRUE.equals(work.getCurrentlyWorking())
                        ? "Present"
                        : formatDate(work.getEnd_date()));

        return EmploymentVerificationData.builder()
                .companyName(work.getCompany_name())
                .employeeId(work.getEmployee_id())
                .designation(work.getPosition())
                .department(null) // add field later if available
                .employmentPeriod(employmentPeriod)
                .hrEmail(work.getHr_email_id())
                .build();
    }
    
    private String formatDate(LocalDate date) {
        return date == null
                ? ""
                : date.toString();
    }
}
