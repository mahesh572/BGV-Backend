package com.org.bgv.vendor.builder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.candidate.entity.IdentityProof;
import com.org.bgv.candidate.repository.IdentityProofRepository;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.Document;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.repository.DocumentRepository;
import com.org.bgv.vendor.action.dto.ActionDTO;
import com.org.bgv.vendor.action.dto.VendorActionCatalog;
import com.org.bgv.vendor.dto.ActionType;
import com.org.bgv.vendor.dto.DocumentTypeVerificationDTO;
import com.org.bgv.vendor.dto.ObjectComparisonFieldDTO;
import com.org.bgv.vendor.dto.ObjectDTO;
import com.org.bgv.vendor.dto.ObjectFieldDTO;
import com.org.bgv.vendor.dto.VerificationFileDTO;
import com.org.bgv.vendor.entity.VerificationObject;
import com.org.bgv.vendor.repository.VerificationFieldComparisonRepository;
import com.org.bgv.vendor.repository.VerificationObjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityObjectBuilderStrategy
        implements ObjectBuilderStrategy {

    private final VerificationObjectRepository verificationObjectRepository;
    private final IdentityProofRepository identityProofRepository;
    private final AbstractObjectBuilderStrategyUtil abstractObjectBuilderStrategyUtil;

    @Override
    public CheckCategoryEnum supportedCategory() {
        return CheckCategoryEnum.IDENTITY;
    }

    @Override
    public List<ObjectDTO> buildObjects(
            VerificationCaseCheck check) {

    	List<IdentityProof> identities = identityProofRepository
	            .findByVerificationCaseCheckCaseCheckId(check.getCaseCheckId());
    	
    	log.info("IdentityObjectBuilderStrategy::::::::::::::::::::::::::::::::::::::::::::::");
	    
	    List<VerificationObject> objects =
	            verificationObjectRepository
	                    .findByVerificationCheckAndObjectType(
	                            check,
	                            CheckCategoryEnum.IDENTITY);
	    
	    return objects.stream()
	            .map(object -> {
	            	List<ObjectComparisonFieldDTO> fields = abstractObjectBuilderStrategyUtil.buildComparisonFields(object);
	            	 List<DocumentTypeVerificationDTO> documentTypes = abstractObjectBuilderStrategyUtil.buildDocumentTypes(object.getSourceId(), check,null);
	            	 
	            	 // Compute object status from document types
		                DocumentStatus objectStatus = abstractObjectBuilderStrategyUtil.resolveObjectStatus(documentTypes);
		                
		                return ObjectDTO.builder()
		                        .objectId(object.getSourceId())
		                        .objectType(CheckCategoryEnum.IDENTITY.getName())
		                        .displayName(object.getObjectName())
		                       // .data(buildIdentityData(identity))
		                        .status(objectStatus.name()) // store as string if DTO expects string
		                        .documentTypes(documentTypes)
		                        .evidence(Collections.emptyList())
		                        .actions(VendorActionCatalog.objectActions()) // optionally pass objectStatus to restrict actions
		                        .fields(fields)
		                        .build();
	            	 
	            })
	            .toList();
    }

   
}