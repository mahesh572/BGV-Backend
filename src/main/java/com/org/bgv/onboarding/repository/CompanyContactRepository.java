package com.org.bgv.onboarding.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.onboarding.dto.ContactType;
import com.org.bgv.onboarding.entity.CompanyContact;

@Repository
public interface CompanyContactRepository
        extends JpaRepository<CompanyContact, Long> {

    List<CompanyContact> findByCompanyId(Long companyId);

    Optional<CompanyContact> findByCompanyIdAndContactType(
            Long companyId,
            ContactType contactType);
    
    Optional<CompanyContact> findByCompanyIdAndPrimaryContactTrue(
            Long companyId);
}
