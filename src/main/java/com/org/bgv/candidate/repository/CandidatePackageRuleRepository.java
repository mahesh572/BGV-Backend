package com.org.bgv.candidate.repository;

import com.org.bgv.candidate.entity.CandidatePackageRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatePackageRuleRepository extends JpaRepository<CandidatePackageRule, Long> {

    // 🔹 Find all rules by candidate
   // List<CandidatePackageRule> findByCandidateId(Long candidateId);

    // 🔹 Find by candidate + case
  //  List<CandidatePackageRule> findByCandidateIdAndVerificationCase_Id(Long candidateId, Long caseId);

    // 🔹 Find by company
   // List<CandidatePackageRule> findByCompanyId(Long companyId);

    // 🔹 Find by employer package
  //  List<CandidatePackageRule> findByEmployerPackageId_Id(Long employerPackageId);

    // 🔹 Find required rules for a candidate
  //  List<CandidatePackageRule> findByCandidateIdAndRequiredTrue(Long candidateId);

    // 🔹 Find addons
  //  List<CandidatePackageRule> findByCandidateIdAndAddonTrue(Long candidateId);

    // 🔹 Find included in package
  //  List<CandidatePackageRule> findByCandidateIdAndIncludedInPackageTrue(Long candidateId);

    // 🔹 Find by category + rule type
  //  List<CandidatePackageRule> findByCheckCategoryIdAndRuleTypeId(Long checkCategoryId, Long ruleTypeId);

    // 🔹 Find by candidate + category
  //  List<CandidatePackageRule> findByCandidateIdAndCheckCategoryId(Long candidateId, Long checkCategoryId);

    // 🔹 Delete rules for a candidate (useful when re-creating package)
  //  void deleteByCandidateId(Long candidateId);
	
	void deleteByVerificationCaseCaseId(Long caseId);
	
	 List<CandidatePackageRule> findByVerificationCase_CaseId(Long caseId);

}