package com.org.bgv.repository;

import com.org.bgv.constants.CaseStatus;
import com.org.bgv.entity.EmployerPackage;
import com.org.bgv.entity.VerificationCase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationCaseRepository extends JpaRepository<VerificationCase, Long> {
    
    List<VerificationCase> findByCandidateId(Long candidateId);
    
    List<VerificationCase> findByCompanyId(Long companyId);
    
    List<VerificationCase> findByEmployerPackageId(Long employerPackageId);
    
    List<VerificationCase> findByStatus(CaseStatus status);
    
    List<VerificationCase> findByCompanyIdAndStatus(Long companyId, CaseStatus status);


    List<VerificationCase> findByCompanyIdAndStatusIn(Long companyId, List<CaseStatus> statuses);
    
    
    @Query("SELECT cc FROM VerificationCase cc WHERE cc.candidateId = :candidateId AND cc.status IN :statuses")
    List<VerificationCase> findByCandidateIdAndStatusIn(@Param("candidateId") Long candidateId, 
                                                   @Param("statuses") List<CaseStatus> statuses);
    
    Optional<VerificationCase> findByCandidateIdAndEmployerPackageId(Long candidateId, Long employerPackageId);
    
    @Query("SELECT COUNT(cc) FROM VerificationCase cc WHERE cc.employerPackage.id = :employerPackageId")
    Long countByEmployerPackage(@Param("employerPackageId") Long employerPackageId);
    
    @Query("SELECT COUNT(cc) FROM VerificationCase cc WHERE cc.employerPackage = :employerPackage AND cc.status != 'COMPLETED'")
    Long countByEmployerPackageAndStatusNot(@Param("employerPackage") EmployerPackage employerPackage, 
                                          @Param("status") CaseStatus status);
    
}