package com.org.bgv.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.common.DocumentStatus;
import com.org.bgv.entity.Document;
import com.org.bgv.entity.VerificationCase;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // 1. Find documents by profileId
    List<Document> findByProfile_ProfileId(Long profileId);

    // 2. Find documents by docTypeId (fixed field name)
    List<Document> findByDocTypeId_DocTypeId(Long docTypeId);

    // 3. Find documents by categoryId
    List<Document> findByCategory_CategoryId(Long categoryId);

    // 4. Find documents by object_id
    List<Document> findByObjectId(Long objectId);

    // 5. Find documents by profileId and categoryId
    List<Document> findByProfile_ProfileIdAndCategory_CategoryId(Long profileId, Long categoryId);

    // 6. Find documents by profileId and docTypeId (fixed field name)
    List<Document> findByProfile_ProfileIdAndDocTypeId_DocTypeId(Long profileId, Long docTypeId);

    // 7. Find documents by profileId, categoryId, and objectId
    List<Document> findByProfile_ProfileIdAndCategory_CategoryIdAndObjectId(
            Long profileId, Long categoryId, Long objectId);

    // 8. Find documents by profileId, categoryId, and docTypeId (fixed field name)
    List<Document> findByProfile_ProfileIdAndCategory_CategoryIdAndDocTypeId_DocTypeId(
            Long profileId, Long categoryId, Long docTypeId);
    
    List<Document> findByCandidate_CandidateIdAndCategory_CategoryIdAndDocTypeId_DocTypeId(
            Long candidateId, Long categoryId, Long docTypeId);

    // 9. Find documents by profileId and object_id
    List<Document> findByProfile_ProfileIdAndObjectId(Long profileId, Long objectId);

    // 10. Find documents by categoryId and docTypeId (fixed field name)
    List<Document> findByCategory_CategoryIdAndDocTypeId_DocTypeId(Long categoryId, Long docTypeId);

    // 11. Find a specific document by all four criteria (fixed field name)
    /*
    List<Document> findByProfile_ProfileIdAndCategory_CategoryIdAndDocTypeId_DocTypeIdAndObjectId(
            Long profileId, Long categoryId, Long docTypeId, Long objectId); */
    
    List<Document> findByCandidate_CandidateIdAndCategory_CategoryIdAndDocTypeId_DocTypeIdAndObjectId(
            Long candidateId, Long categoryId, Long docTypeId, Long objectId);

    // 12. Find documents by profileId and status
    List<Document> findByProfile_ProfileIdAndStatus(Long profileId, String status);

    // 13. Find documents by categoryId and status
    List<Document> findByCategory_CategoryIdAndStatus(Long categoryId, String status);

    // 14. Find documents by multiple object_ids
    List<Document> findByObjectIdIn(List<Long> objectIds);

    // 15. Find documents by profileId and multiple categoryIds
    List<Document> findByProfile_ProfileIdAndCategory_CategoryIdIn(Long profileId, List<Long> categoryIds);
    
    

    // 16. Custom query for complex searches (fixed field name)
    @Query("SELECT d FROM Document d WHERE " +
           "d.profile.profileId = :profileId AND " +
           "(:categoryId IS NULL OR d.category.categoryId = :categoryId) AND " +
           "(:docTypeId IS NULL OR d.docTypeId.docTypeId = :docTypeId) AND " +
           "(:objectId IS NULL OR d.objectId = :objectId) AND " +
           "(:status IS NULL OR d.status = :status)")
    List<Document> findDocumentsByCriteria(
            @Param("profileId") Long profileId,
            @Param("categoryId") Long categoryId,
            @Param("docTypeId") Long docTypeId,
            @Param("objectId") Long objectId,
            @Param("status") String status);

    // 17. Check if document exists for the given criteria (fixed field name)
    boolean existsByProfile_ProfileIdAndCategory_CategoryIdAndDocTypeId_DocTypeIdAndObjectId(
            Long profileId, Long categoryId, Long docTypeId, Long objectId);

    // 18. Find documents with category and type details (eager loading) (fixed field name)
    @Query("SELECT d FROM Document d JOIN FETCH d.category JOIN FETCH d.docTypeId WHERE d.profile.profileId = :profileId")
    List<Document> findByProfileIdWithCategoryAndType(@Param("profileId") Long profileId);

    // 19. Count documents by profileId and status
    Long countByProfile_ProfileIdAndStatus(Long profileId, String status);

    // 20. Count documents by profileId and categoryId
    Long countByProfile_ProfileIdAndCategory_CategoryId(Long profileId, Long categoryId);

    // 21. Find latest documents by profileId ordered by uploadedAt
    List<Document> findByProfile_ProfileIdOrderByUploadedAtDesc(Long profileId);
    
    List<Document> findByCategory_CategoryIdAndDocTypeId_DocTypeIdAndCandidate_CandidateId(
            Long categoryId,
            Long docTypeId,
            Long candidateId
    );
    
    // for verification part
    
    List<Document> findByCandidateCandidateId(Long candidateId);
    
    List<Document> findByCandidateCandidateIdAndCategoryCategoryId(Long candidateId, Long categoryId);
    
    List<Document> findByCandidateCandidateIdAndDocTypeIdDocTypeId(Long candidateId, Long documentTypeId);
    
    List<Document> findByCandidateCandidateIdAndObjectId(Long candidateId, Long objectId);
    
    List<Document> findByCandidateCandidateIdAndCategoryCategoryIdAndObjectId(
            Long candidateId,
            Long categoryId,
            Long objectId
    );
    
    @Query("""
    	    SELECT d FROM Document d
    	    WHERE d.candidate.candidateId = :candidateId
    	    AND d.verified = true
    	""")
    	List<Document> findVerifiedDocumentsByCandidateId(@Param("candidateId") Long candidateId);
    
    Long countByCandidateCandidateId(Long candidateId);
    
    Long countByCandidateCandidateIdAndVerified(Long candidateId, boolean verified);
    
    @Query("SELECT d FROM Document d " +
            "JOIN VerificationCaseDocumentLink link ON link.document = d " +
            "JOIN VerificationCaseDocument vcd ON link.caseDocument = vcd " +
            "WHERE vcd.verificationCase = :verificationCase " +
            "AND vcd.checkCategory.code = :checkType")
     List<Document> findByVerificationCaseAndCheckType(
             @Param("verificationCase") VerificationCase verificationCase,
             @Param("checkType") String checkType);
    
    
    List<Document> findByCandidate_CandidateIdAndCategory_CategoryIdAndDocTypeId_DocTypeIdAndStatusNot(
            Long candidateId, Long categoryId, Long docTypeId, String status);
        
        // And similar for other methods
        List<Document> findByCandidate_CandidateIdAndCategory_CategoryIdAndDocTypeId_DocTypeIdAndObjectIdAndStatusNot(
            Long candidateId, Long categoryId, Long docTypeId, Long objectId, String status);
        
        
        List<Document> findByCandidate_CandidateIdAndDocIdInAndStatusNot(
                Long candidateId, Collection<Long> documentIds, String status);
        
        List<Document> findByObjectIdAndCategory_CategoryIdAndStatusNot(
                Long objectId,
                Long categoryId,
                DocumentStatus status
        );
        
}
