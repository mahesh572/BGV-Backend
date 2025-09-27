package com.org.bgv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.Document;
import com.org.bgv.entity.EducationDocuments;
import com.org.bgv.entity.ProfessionalDocuments;

public interface EducationDocumentsRepository extends JpaRepository<EducationDocuments, Long> {
	List<EducationDocuments> findByProfile_ProfileId(Long profileId);
	 List<EducationDocuments> findByProfile_ProfileIdAndObjectIdIn(Long profileId, List<Long> objectIds);
	 void deleteByProfile_ProfileId(Long profileId); //
}
