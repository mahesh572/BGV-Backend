package com.org.bgv.company.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.company.entity.CompanyEmailSettings;

@Repository
public interface CompanyEmailSettingsRepository
        extends JpaRepository<CompanyEmailSettings, Long> {

    Optional<CompanyEmailSettings> findByCompanyId(Long companyId);

    Optional<CompanyEmailSettings> findByCompanyIdAndActiveTrue(Long companyId);

    boolean existsByCompanyId(Long companyId);
}
