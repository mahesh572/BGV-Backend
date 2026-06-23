package com.org.bgv.vendor.builder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.commom.dto.OptionDTO;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.entity.Document;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.enums.ComparisonStatus;
import com.org.bgv.repository.DocumentRepository;
import com.org.bgv.vendor.action.dto.ActionDTO;
import com.org.bgv.vendor.action.dto.VendorActionCatalog;
import com.org.bgv.vendor.dto.ActionType;
import com.org.bgv.vendor.dto.DocumentTypeVerificationDTO;
import com.org.bgv.vendor.dto.ObjectComparisonFieldDTO;
import com.org.bgv.vendor.dto.ObjectFieldDTO;
import com.org.bgv.vendor.dto.VerificationFileDTO;
import com.org.bgv.vendor.entity.VerificationObject;
import com.org.bgv.vendor.repository.VerificationFieldComparisonRepository;
import com.org.bgv.vendor.repository.VerificationObjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class AbstractObjectBuilderStrategyUtil
         {

    protected final VerificationObjectRepository verificationObjectRepository;
    protected final VerificationFieldComparisonRepository verificationFieldComparisonRepository;
    protected final DocumentRepository documentRepository;
    
    
    public List<DocumentTypeVerificationDTO> buildDocumentTypes(Long objectId, VerificationCaseCheck check,List<ObjectFieldDTO> fields

    		) {

    			List<Document> documents = documentRepository
    					.findByCandidate_CandidateIdAndVerificationCaseCheck_CaseCheckIdAndObjectIdAndStatusNot(
    							check.getVerificationCase().getCandidateId(), check.getCaseCheckId(), objectId,
    							DocumentStatus.DELETED);

    			Map<Object, List<Document>> grouped = documents.stream()
    					.collect(Collectors.groupingBy(doc -> doc.getDocTypeId().getDocTypeId()));

    			return grouped.entrySet().stream().map(entry -> {
    				DocumentType docType = entry.getValue().get(0).getDocTypeId();

    				return DocumentTypeVerificationDTO.builder()
    						.documentTypeId(String.valueOf(docType.getDocTypeId()))
    						.type(docType.getLabel())
    						.status(resolveDocumentTypeStatus(entry.getValue()))
    						// .actions(VendorActionCatalog.documentActions())
    						 .fields(fields)
    						 .actions(resolveDocumentActions(resolveDocumentTypeStatus(entry.getValue()),check.getStatus()))
    						.files(buildVerificationFiles(entry.getValue(),check)).build();
    			}).toList();

    		}
    
    
    private List<VerificationFileDTO> buildVerificationFiles(
	        List<Document> documents,
	        VerificationCaseCheck check
	) {

	    return documents.stream()
	            .filter(doc -> doc.getStatus() != DocumentStatus.DELETED)
	            .filter(doc -> !Boolean.FALSE.equals(doc.getActive()))   // only active (true or null)
	            .map(doc -> VerificationFileDTO.builder()
	                    .docId(doc.getDocId())
	                    .fileId(doc.getDocId())
	                    .fileName(doc.getOriginalFileName())
	                    .fileUrl(doc.getFileUrl())
	                    .fileSize(doc.getFileSize())
	                    .fileType(doc.getFileType())
	                    .status(doc.getStatus())
	                    .uploadedBy(doc.getUploadedBy())
	                    .uploadedAt(doc.getUploadedAt())
	                    .verified(doc.isVerified())
	                    .comments(doc.getComments())
	                    .createdAt(doc.getCreatedAt())
	                    .updatedAt(doc.getUpdatedAt())
	                    .fileKey(doc.getAwsDocKey())
	                    .actions(resolveFileActions(doc.getStatus(), check))
	                    .build())
	            .toList();
	}
    
    private List<ActionDTO> resolveFileActions(DocumentStatus status,VerificationCaseCheck check) {

		log.info("resolveFileActions::::::::::::::::::::::::::::::{}",status);
		
		boolean restricted =
		        status == DocumentStatus.REQUEST_INFO ||
		        status == DocumentStatus.INSUFFICIENT ||
		        status == DocumentStatus.REJECTED ||
		        status == DocumentStatus.VERIFIED ||
		        check.getStatus() == CaseCheckStatus.REJECTED ;
		      //  || check.getStatus() == CaseCheckStatus.ACTION_REQUIRED;
		
		
		log.info("resolveFileActions::::::::::::::::::restricted::::::::::::{}",restricted);
	    if (!restricted) {
	        return VendorActionCatalog.documentActions();
	    }

	    // 🔒 Only allow view + download
	    return VendorActionCatalog.documentActions().stream()
	            .map(action -> {
	                if (action.getCode() == ActionType.VIEW ||
	                    action.getCode() == ActionType.DOWNLOAD) {
	                    return action;
	                }

	                return ActionDTO.builder()
	                        .code(action.getCode())
	                        .label(action.getLabel())
	                        .level(action.getLevel())
	                        .enabled(false)
	                        .build();
	            })
	            .toList();
	}
    

    private List<ActionDTO> resolveDocumentActions(String documentTypeStatus,CaseCheckStatus checkStatus) {

		boolean restricted =
		        Set.of("REQUEST_INFO", "INSUFFICIENT").contains(documentTypeStatus)
		        || CaseCheckStatus.ACTION_REQUIRED.equals(checkStatus);
	    
		if (!restricted) {
	        return VendorActionCatalog.documentActions();
	    }

	    // 🔒 Only allow view + download
	    return VendorActionCatalog.documentActions().stream()
	            .map(action -> {
	                if (action.getCode() == ActionType.VIEW ||
	                    action.getCode() == ActionType.DOWNLOAD) {
	                    return action;
	                }

	                return ActionDTO.builder()
	                        .code(action.getCode())
	                        .label(action.getLabel())
	                        .level(action.getLevel())
	                        .enabled(false)
	                        .build();
	            })
	            .toList();
	}
    

    private String resolveDocumentTypeStatus(List<Document> documents) {

	    if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.REJECTED)) {
	        return DocumentStatus.REJECTED.name();
	    }

	    if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.REQUEST_INFO)) {
	        return DocumentStatus.REQUEST_INFO.name();
	    }

	    if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.INSUFFICIENT)) {
	        return DocumentStatus.INSUFFICIENT.name();
	    }

	    if (documents.stream().allMatch(Document::isVerified)) {
	        return DocumentStatus.VERIFIED.name();
	    }

	    return DocumentStatus.PENDING.name();
	}


    public List<ObjectComparisonFieldDTO> buildComparisonFields(VerificationObject object) {

	    return verificationFieldComparisonRepository
	            .findByVerificationObjectOrderById(object)
	            .stream()
	            .map(field ->

	                    ObjectComparisonFieldDTO.builder()
	                            .comparisonId(field.getId())
	                            .fieldName(field.getFieldName())
	                            .displayName(field.getDisplayName())
	                            .candidateValue(field.getCandidateValue())
	                            .sourceValue(field.getSourceValue())
	                            .result(field.getResult().name())
	                            .verified(field.getVerified())
	                            .remarks(field.getRemarks())
	                            .build()

	            )
	            .toList();
	}
    
    public DocumentStatus resolveObjectStatus(List<DocumentTypeVerificationDTO> docTypes) {

	    if (docTypes.stream().anyMatch(d -> d.getStatus().equals("REJECTED"))) {
	        return DocumentStatus.REJECTED;
	    }

	    if (docTypes.stream().anyMatch(d -> d.getStatus().equals("REQUEST_INFO"))) {
	        return DocumentStatus.REQUEST_INFO;
	    }

	    if (docTypes.stream().anyMatch(d -> d.getStatus().equals("INSUFFICIENT"))) {
	        return DocumentStatus.INSUFFICIENT;
	    }

	    if (docTypes.stream().allMatch(d -> d.getStatus().equals("VERIFIED"))) {
	        return DocumentStatus.VERIFIED;
	    }

	    return DocumentStatus.PENDING;
	}
    

    public List<OptionDTO> getComparisonStatuses() {

        return Arrays.stream(ComparisonStatus.values())
                .map(status ->
                        new OptionDTO(
                                status.name(),
                                status.getLabel(),
                                status.getColor()))
                .toList();
    }
    
    
}
