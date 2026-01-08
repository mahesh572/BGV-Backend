package com.org.bgv.repository;

import com.org.bgv.constants.CaseStatus;
import com.org.bgv.entity.EmployerPackage;
import com.org.bgv.entity.VerificationCase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationCaseRepository extends JpaRepository<VerificationCase, Long> {
    
    List<VerificationCase> findByCandidateId(Long candidateId);
    List<VerificationCase> findByCandidateId(Long candidateId,Pageable page);
    
    List<VerificationCase> findByCompanyId(Long companyId);
    
    List<VerificationCase> findByEmployerPackageId(Long employerPackageId);
    
    List<VerificationCase> findByStatus(CaseStatus status);
    
    List<VerificationCase> findByCompanyIdAndStatus(Long companyId, CaseStatus status);
    
  //  VerificationCase findByCompanyIdAndCandidateIdAndStatus(Long companyId,Long candidateId,CaseStatus status);
    Optional<VerificationCase>
    findFirstByCompanyIdAndCandidateIdAndStatusOrderByCreatedAtDesc(
        Long companyId,
        Long candidateId,
        CaseStatus status
    );

    List<VerificationCase> findByCompanyIdAndStatusIn(Long companyId, List<CaseStatus> statuses);
    
    
    // candidateId,companyId,category checkId,document typeId
    
    
    @Query("SELECT cc FROM VerificationCase cc WHERE cc.candidateId = :candidateId AND cc.status IN :statuses")
    List<VerificationCase> findByCandidateIdAndStatusIn(@Param("candidateId") Long candidateId, 
                                                   @Param("statuses") List<CaseStatus> statuses);
    
    Optional<VerificationCase> findByCandidateIdAndEmployerPackageIdAndCompanyId(Long candidateId, Long employerPackageId,Long companyId);
    
    @Query("SELECT COUNT(cc) FROM VerificationCase cc WHERE cc.employerPackage.id = :employerPackageId")
    Long countByEmployerPackage(@Param("employerPackageId") Long employerPackageId);
    
    @Query("SELECT COUNT(cc) FROM VerificationCase cc WHERE cc.employerPackage = :employerPackage AND cc.status != 'COMPLETED'")
    Long countByEmployerPackageAndStatusNot(@Param("employerPackage") EmployerPackage employerPackage, 
                                          @Param("status") CaseStatus status);
    /*
    @Query("SELECT vc FROM VerificationCase vc " +
            "WHERE vc.vendorId = :vendorId OR vc.status IN :statuses")
     List<VerificationCase> findByVendorIdOrStatusIn(
             @Param("vendorId") Long vendorId,
             @Param("statuses") List<CaseStatus> statuses);
    */
    Optional<VerificationCase> 
    findByCompanyIdAndCandidateIdAndCompletedAtIsNull(
            Long companyId,
            Long candidateId
           
    );
    
    
 // Fixed query without LocalDateTime IS NULL checks for PostgreSQL
    @Query("""
        SELECT vc FROM VerificationCase vc 
        LEFT JOIN Company c ON vc.companyId = c.id 
        LEFT JOIN EmployerPackage ep ON vc.employerPackage = ep 
        WHERE vc.candidateId = :candidateId 
        AND (:status IS NULL OR vc.status = :status) 
        AND (:searchTerm IS NULL OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) 
             OR CAST(vc.caseId AS string) LIKE CONCAT('%', :searchTerm, '%')) 
       
        """)
    Page<VerificationCase> findByCandidateIdWithFilters(
        @Param("candidateId") Long candidateId,
        @Param("status") CaseStatus status,
        @Param("searchTerm") String searchTerm,
        Pageable pageable
    );

    
    Optional<VerificationCase> findByCaseIdAndCandidateId(Long caseId, Long candidateId);
    
 // Count methods for statistics
    Long countByCandidateId(Long candidateId);
    Long countByCandidateIdAndStatus(Long candidateId, CaseStatus status);
    
}