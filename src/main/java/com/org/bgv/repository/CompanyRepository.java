package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    
    Optional<Company> findByCompanyName(String companyName);
    
 // Count companies by status
    long countByStatus(String status);
    
    List<Company> findByIndustry(String industry);
    
    List<Company> findByCountry(String country);
    
    @Query("SELECT c FROM Company c WHERE c.companyName LIKE %:name%")
    List<Company> findByCompanyNameContaining(@Param("name") String name);
    
    @Query("SELECT c FROM Company c WHERE c.contactEmail = :email")
    Optional<Company> findByContactEmail(@Param("email") String email);
    
    boolean existsByCompanyName(String companyName);
    
    boolean existsByRegistrationNumber(String registrationNumber);
    
    boolean existsByTaxId(String taxId);
    
    Optional<Company> findByRegistrationNumber(String registrationNumber);
    Optional<Company> findByTaxId(String taxId);
    
    
 // Find active companies
    List<Company> findByStatus(String status);
    
    // Find companies by industry and status
    List<Company> findByIndustryAndStatus(String industry, String status);
    
    // Search companies by name containing (case insensitive)
    List<Company> findByCompanyNameContainingIgnoreCase(String companyName);
    
    // Search companies with pagination
    Page<Company> findByCompanyNameContainingIgnoreCase(String companyName, Pageable pageable);
   
}