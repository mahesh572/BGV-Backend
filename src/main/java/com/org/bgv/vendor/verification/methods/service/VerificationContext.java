package com.org.bgv.vendor.verification.methods.service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.entity.Vendor;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.vendor.entity.VerificationMethodExecution;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerificationContext {

    private VerificationCase verificationCase;

    private VerificationCaseCheck verificationCheck;

    private Candidate candidate;

    private Company company;

    private Vendor vendor;

    private VerificationMethodExecution execution;

    private Object verificationData;
}