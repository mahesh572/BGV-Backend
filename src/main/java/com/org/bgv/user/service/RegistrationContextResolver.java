package com.org.bgv.user.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.org.bgv.common.RoleConstants;
import com.org.bgv.entity.User;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.service.CompanyService;
import com.org.bgv.user.enums.CandidateSource;
import com.org.bgv.user.enums.UserRegistrationSource;
import com.org.bgv.user.requests.RegistrationContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationContextResolver {
	
	private final CompanyService companyService;

    public RegistrationContext resolve(User currentUser) {

        if (currentUser == null) {

            return selfRegistration();
        }

        Company company =
        		companyService.getCurrentCompany(currentUser);

        switch (company.getCompanyType()) {

            case EMPLOYER:

                return employer(company,currentUser);

            case VENDOR:

                return vendor(company,currentUser);

            case UNIVERSITY:

                return university(company,currentUser);

            default:

                return admin(company,currentUser);
        }
    }
    
    private RegistrationContext selfRegistration() {

    	Company defaultCompany = companyService.getDefaultCompany();

        return RegistrationContext.builder()
                .company(defaultCompany)
                .source(UserRegistrationSource.SELF)
                .roles(Set.of(RoleConstants.ROLE_USER))
                .createdBy(null)
                .createCompanyUser(true)
                .createCandidate(false)
                .sendInvitation(true)
                .build();
    }
    
    private RegistrationContext companyRegistration(User currentUser) {

        Company company = companyService.getCurrentCompany(currentUser);

        UserRegistrationSource source;

        switch (company.getCompanyType()) {

            case EMPLOYER:
                source = UserRegistrationSource.EMPLOYER;
                break;

            case UNIVERSITY:
                source = UserRegistrationSource.UNIVERSITY;
                break;

            case VENDOR:
                source = UserRegistrationSource.VENDOR;
                break;

            default:
                source = UserRegistrationSource.ADMIN;
        }

        return RegistrationContext.builder()
                .company(company)
                .source(source)
                .createdBy(currentUser)
                .build();
    }
    
    private RegistrationContext employer(Company company, User currentUser) {

        return RegistrationContext.builder()
                .company(company)
                .source(UserRegistrationSource.EMPLOYER)
                .createdBy(currentUser)
                .build();
    }
    
    private RegistrationContext university(Company company, User currentUser) {

        return RegistrationContext.builder()
                .company(company)
                .source(UserRegistrationSource.UNIVERSITY)
                .createdBy(currentUser)
                .build();
    }
    
    private RegistrationContext vendor(Company company, User currentUser) {

        return RegistrationContext.builder()
                .company(company)
                .source(UserRegistrationSource.VENDOR)
                .createdBy(currentUser)
                .build();
    }
    
    private RegistrationContext admin(Company company, User currentUser) {

        return RegistrationContext.builder()
                .company(company)
                .source(UserRegistrationSource.ADMIN)
                .createdBy(currentUser)
                .build();
    }
}
