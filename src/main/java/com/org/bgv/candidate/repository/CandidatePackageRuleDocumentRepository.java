package com.org.bgv.candidate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.candidate.entity.CandidatePackageRule;
import com.org.bgv.candidate.entity.CandidatePackageRuleDocument;

@Repository
public interface CandidatePackageRuleDocumentRepository
        extends JpaRepository<CandidatePackageRuleDocument, Long> {

    List<CandidatePackageRuleDocument> findByRule_Id(Long ruleId);
    List<CandidatePackageRuleDocument> findByRule(CandidatePackageRule rule);

    List<CandidatePackageRuleDocument> findByRule_VerificationCase_CaseId(Long caseId);
    
   

List<CandidatePackageRuleDocument> 
    findByVerificationCase_CaseIdAndSelectedTrue(Long caseId);

List<CandidatePackageRuleDocument> 
    findByVerificationCase_CaseIdAndCategoryId(Long caseId, Long categoryId);

void deleteByVerificationCaseCaseId(Long caseId);


List<CandidatePackageRuleDocument> findByVerificationCase_CaseId(Long caseId);

}
