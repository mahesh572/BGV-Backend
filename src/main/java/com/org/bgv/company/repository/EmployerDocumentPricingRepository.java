package com.org.bgv.company.repository;

import com.org.bgv.company.entity.EmployerDocumentPricing;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployerDocumentPricingRepository
        extends JpaRepository<EmployerDocumentPricing, Long> {

    // ===============================
    // 1️⃣ Get all pricing for a company
    // ===============================
    List<EmployerDocumentPricing> findByCompany(Company company);

    // ===============================
    // 2️⃣ Get pricing by company + category
    // ===============================
    List<EmployerDocumentPricing> findByCompanyAndCheckCategory(
            Company company,
            CheckCategory checkCategory
    );

    // ===============================
    // 3️⃣ Get specific pricing by company + category + document
    // ===============================
    Optional<EmployerDocumentPricing> findByCompanyAndCheckCategoryAndDocumentType(
            Company company,
            CheckCategory checkCategory,
            DocumentType documentType
    );

    // ===============================
    // 4️⃣ Delete pricing for company + category
    // ===============================
    void deleteByCompanyAndCheckCategory(
            Company company,
            CheckCategory checkCategory
    );

    // ===============================
    // 5️⃣ Get only active pricing
    // ===============================
    List<EmployerDocumentPricing> findByCompanyAndActiveTrue(
            Company company
    );

    List<EmployerDocumentPricing> findByCompanyAndCheckCategoryAndActiveTrue(
            Company company,
            CheckCategory checkCategory
    );
    
    Optional<EmployerDocumentPricing>
    findByCompany_IdAndCheckCategory_CategoryIdAndDocumentType_docTypeId(
            Long companyId,
            Long categoryId,
            Long documentTypeId
    );

    List<EmployerDocumentPricing>
    findByCompany_IdAndCheckCategory_CategoryIdAndActiveTrue(
            Long companyId,
            Long categoryId
    );
    
    
    
}