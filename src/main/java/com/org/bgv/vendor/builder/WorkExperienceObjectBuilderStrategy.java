package com.org.bgv.vendor.builder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.candidate.entity.WorkExperience;
import com.org.bgv.candidate.repository.WorkExperienceRepository;
import com.org.bgv.commom.dto.OptionDTO;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.Document;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.entity.VerificationCaseSelection;
import com.org.bgv.enums.ComparisonStatus;
import com.org.bgv.repository.DocumentRepository;
import com.org.bgv.repository.VerificationCaseSelectionRepository;
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
public class WorkExperienceObjectBuilderStrategy implements ObjectBuilderStrategy {

    private final VerificationObjectRepository verificationObjectRepository;
    private final VerificationCaseSelectionRepository verificationCaseSelectionRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final VerificationFieldComparisonRepository verificationFieldComparisonRepository;
    private final DocumentRepository documentRepository;
    
    private final AbstractObjectBuilderStrategyUtil abstractObjectBuilderStrategyUtil;

    @Override
    public CheckCategoryEnum supportedCategory() {
        return CheckCategoryEnum.WORK_EXPERIENCE;
    }

    @Override
    public List<ObjectDTO> buildObjects(
            VerificationCaseCheck check) {
    	
    	
    	Long caseId =
	            check.getVerificationCase().getCaseId();
	                 

	    List<VerificationCaseSelection> selections =
	            verificationCaseSelectionRepository
	                    .findByVerificationCase_CaseIdAndType(
	                            caseId,
	                            CheckCategoryEnum.WORK_EXPERIENCE
	                    );

	    List<Long> experienceIds =
	            selections.stream()
	                    .map(VerificationCaseSelection::getReferenceId)
	                    .toList();

	    List<WorkExperience> experiences =
	            workExperienceRepository.findAllById(experienceIds);
	    
	    List<VerificationObject> objects =
	            verificationObjectRepository
	                    .findByVerificationCheckAndObjectType(
	                            check,
	                            CheckCategoryEnum.WORK_EXPERIENCE);
	    
	    
	    
	     return objects.stream()
	            .map(object -> {

	                List<ObjectComparisonFieldDTO> fields = abstractObjectBuilderStrategyUtil.buildComparisonFields(object);
	                
	                List<DocumentTypeVerificationDTO> documentTypes =
	                		abstractObjectBuilderStrategyUtil.buildDocumentTypes(
	                        		object.getSourceId(),
	                                check,
	                                null
	                        );
	                
	                DocumentStatus objectStatus =
	                		abstractObjectBuilderStrategyUtil.resolveObjectStatus(documentTypes);

	                return ObjectDTO.builder()
	                        .objectId(object.getSourceId())
	                        .objectType(object.getObjectType().name())
	                        .displayName(object.getObjectName())
	                        .status(objectStatus.name())
	                        .status(object.getStatus().name())
	                        .fields(fields)
	                        .fieldSatusOptions(abstractObjectBuilderStrategyUtil.getComparisonStatuses())
	                        .documentTypes(documentTypes)
	                        .build();

	            })
	            .toList();
    }
    
   
    
    
    
   
    
    
    
    

    

    
}