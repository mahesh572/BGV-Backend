package com.org.bgv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.EducationDocuments;
import com.org.bgv.entity.IdentityDocuments;

public interface IdentityDocumentsRepository extends JpaRepository<IdentityDocuments, Long> {
	List<IdentityDocuments> findByProfile_ProfileId(Long profileId);
	List<IdentityDocuments> findByProfile_ProfileIdAndObjectIdIn(Long profileId, List<Long> proofIds);
	void deleteByProfile_ProfileId(Long profileId); //
}
