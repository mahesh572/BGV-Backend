package com.org.bgv.user.service;

import org.springframework.stereotype.Service;

import com.org.bgv.entity.CompanyUser;
import com.org.bgv.entity.User;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.repository.CompanyUserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyMembershipService {

    private final CompanyUserRepository companyUserRepository;

    @Transactional
    public CompanyUser assignUserToCompany(User user, Company company) {

        companyUserRepository
                .findByCompanyIdAndUserId(
                        company.getId(),
                        user.getUserId())
                .ifPresent(cu -> {
                    throw new BusinessException(
                            "User already belongs to the company");
                });

        CompanyUser companyUser = CompanyUser.builder()
                .company(company)
                .user(user)
                .build();

        return companyUserRepository.save(companyUser);
    }
}
