package com.org.bgv.company.repository;

import com.org.bgv.company.entity.EmployerPackageAllowedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployerPackageAllowedDocumentRepository 
        extends JpaRepository<EmployerPackageAllowedDocument, Long> {

    // 🔹 Get all documents for an employer package
    List<EmployerPackageAllowedDocument> findByEmployerPackage_Id(Long employerPackageId);

    // 🔹 Get documents by employer package + category
    List<EmployerPackageAllowedDocument> 
        findByEmployerPackage_IdAndCheckCategoryId(
            Long employerPackageId,
            Long checkCategoryId
        );

    // 🔹 Find specific document in a category
    Optional<EmployerPackageAllowedDocument> 
        findByEmployerPackage_IdAndCheckCategoryIdAndDocumentType_DocTypeId(
            Long employerPackageId,
            Long checkCategoryId,
            Long documentTypeId
        );

    // 🔹 Check if document exists
    boolean existsByEmployerPackage_IdAndCheckCategoryIdAndDocumentType_DocTypeId(
            Long employerPackageId,
            Long checkCategoryId,
            Long documentTypeId
    );

    // 🔹 Delete all documents for employer package (useful during reconfiguration)
    void deleteByEmployerPackage_Id(Long employerPackageId);
}