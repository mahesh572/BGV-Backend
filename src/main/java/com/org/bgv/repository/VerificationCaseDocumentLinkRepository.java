package com.org.bgv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.entity.VerificationCaseDocumentLink;

@Repository
public interface VerificationCaseDocumentLinkRepository extends JpaRepository<VerificationCaseDocumentLink, Long> {

}
