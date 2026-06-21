package com.org.bgv.onboarding.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.enums.CompanyAddressType;
import com.org.bgv.onboarding.entity.CompanyAddress;

@Repository
public interface CompanyAddressRepository
        extends JpaRepository<CompanyAddress, Long> {

    List<CompanyAddress> findByCompanyId(Long companyId);

    Optional<CompanyAddress> findByCompanyIdAndAddressType(
            Long companyId,
            CompanyAddressType addressType);

}
