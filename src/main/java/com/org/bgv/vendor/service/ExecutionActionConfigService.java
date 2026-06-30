package com.org.bgv.vendor.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.org.bgv.config.SecurityUtils;
import com.org.bgv.entity.User;
import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.enums.VerificationMethodCode;
import com.org.bgv.service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ExecutionActionConfigService {
	
	private final UserService userService;
	private final ActionPolicyFactory actionPolicyFactory;

	    public List<VerificationExecutionAction> getActions(
	            VerificationMethodCode methodCode,
	            VerificationExecutionStatus status) {

	        switch (methodCode) {

	            case EMAIL:
	                // return getEmailActions(status);
	            	return getVerificationMethodActions(status,methodCode);
	            case PHONE_VERIFICATION:
	               // return getPhoneActions(status);
	            	return getVerificationMethodActions(status,methodCode);
	            case FIELD_VISIT:
	            	// return getFieldVisitActions(status);
	            	return getVerificationMethodActions(status,methodCode);
/*
	            case DOCUMENT_REVIEW:
	                return getDocumentActions(status);

	            case CANDIDATE_CONFIRMATION:
	                return getCandidateActions(status);
*/
	            default:
	                return List.of();
	                
	        }
	    }
	
/*
    private static final Map<VerificationExecutionStatus, List<VerificationExecutionAction>> ACTION_MAP =
            new EnumMap(VerificationExecutionStatus.class);

    static {
        ACTION_MAP.put(VerificationExecutionStatus.WAITING_FOR_RESPONSE,
                List.of(
                		VerificationExecutionAction.UPLOAD_EVIDENCE,
                		VerificationExecutionAction.SEND_REMINDER,
                		VerificationExecutionAction.MARK_RESPONSE_RECEIVED,
                		VerificationExecutionAction.ADD_NOTE,
                		VerificationExecutionAction.CONTACT_CANDIDATE,
                		VerificationExecutionAction.CANCEL
                ));

        ACTION_MAP.put(VerificationExecutionStatus.RESPONSE_RECEIVED,
                List.of(
                		VerificationExecutionAction.UPLOAD_EVIDENCE,
                		VerificationExecutionAction.ADD_NOTE,
                		VerificationExecutionAction.MARK_UNDER_REVIEW,
                		VerificationExecutionAction.CONTACT_CANDIDATE,
                		VerificationExecutionAction.COMPLETE
                ));

        ACTION_MAP.put(VerificationExecutionStatus.UNDER_REVIEW,
                List.of(
                		VerificationExecutionAction.UPLOAD_EVIDENCE,
                		VerificationExecutionAction.ADD_NOTE,
                		VerificationExecutionAction.COMPLETE,
                		VerificationExecutionAction.CONTACT_CANDIDATE
                ));

        ACTION_MAP.put(VerificationExecutionStatus.COMPLETED, List.of());

        ACTION_MAP.put(VerificationExecutionStatus.CANCELLED, List.of());
    }

    public List<VerificationExecutionAction> getActions(VerificationExecutionStatus status) {
        return ACTION_MAP.getOrDefault(status, List.of());
    }
    */
    
    private List<VerificationExecutionAction>  getEmailActions(VerificationExecutionStatus status) {

        switch (status) {

            case WAITING_FOR_RESPONSE:
                return List.of(
                		VerificationExecutionAction.UPLOAD_EVIDENCE,
                		VerificationExecutionAction.SEND_REMINDER,
                		VerificationExecutionAction.MARK_RESPONSE_RECEIVED,
                		VerificationExecutionAction.ADD_NOTE,
                		VerificationExecutionAction.CONTACT_CANDIDATE,
                		VerificationExecutionAction.CANCEL);

            case RESPONSE_RECEIVED:
                return List.of(
                		VerificationExecutionAction.UPLOAD_EVIDENCE,
                		VerificationExecutionAction.ADD_NOTE,
                		VerificationExecutionAction.MARK_UNDER_REVIEW,
                		VerificationExecutionAction.COMPLETE);
            case UNDER_REVIEW:
            	return List.of(VerificationExecutionAction.UPLOAD_EVIDENCE,
                		VerificationExecutionAction.ADD_NOTE,
                		VerificationExecutionAction.COMPLETE,
                		VerificationExecutionAction.CONTACT_CANDIDATE);
            case COMPLETED:
            	return List.of();
            case CANCELLED:
            	return List.of();
            default:
                return List.of();
        }
    }
    
    private List<VerificationExecutionAction> getPhoneActions(VerificationExecutionStatus status) {

        switch (status) {
        
        case PHONE_CALL_INITIATED:
            return List.of(
                    VerificationExecutionAction.MAKE_PHONE_CALL,
                    VerificationExecutionAction.ADD_NOTE,
                    VerificationExecutionAction.CANCEL);
        case PHONE_CALL_FAILED:
            return List.of(
                    VerificationExecutionAction.MAKE_PHONE_CALL,
                    VerificationExecutionAction.ADD_NOTE,
                    VerificationExecutionAction.CONTACT_CANDIDATE,
                    VerificationExecutionAction.CANCEL
            );

            case WAITING_FOR_RESPONSE:
                return List.of(
                		VerificationExecutionAction.MAKE_PHONE_CALL,
                		VerificationExecutionAction.UPLOAD_EVIDENCE,
                		VerificationExecutionAction.MARK_RESPONSE_RECEIVED,
                		VerificationExecutionAction.ADD_NOTE,
                		VerificationExecutionAction.CONTACT_CANDIDATE,
                		VerificationExecutionAction.CANCEL);

            case RESPONSE_RECEIVED:
                return List.of(
                		VerificationExecutionAction.UPLOAD_EVIDENCE,
                		VerificationExecutionAction.ADD_NOTE,
                		VerificationExecutionAction.MARK_UNDER_REVIEW,
                		VerificationExecutionAction.CONTACT_CANDIDATE
                		);
            case UNDER_REVIEW:
            	return List.of(
            			VerificationExecutionAction.UPLOAD_EVIDENCE,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.MARK_VERIFIED,
                        VerificationExecutionAction.MARK_DISCREPANCY_FOUND,
                        VerificationExecutionAction.MARK_UNABLE_TO_VERIFY);
            case COMPLETED:
            	return List.of();

            default:
                return List.of();
        }
    }
    /*
    public List<VerificationExecutionAction> getFieldVisitActions(
            VerificationExecutionStatus status) {
    	
    	 Long userId = SecurityUtils.getCurrentUserId();
    	 User fieldAgent = userService.getUserById(userId);
    	
    	 User currentUser = userService.getUserById(SecurityUtils.getCurrentUserId());

    	 ActionPolicy policy = actionPolicyFactory.getPolicy(currentUser);

    	 List<VerificationExecutionAction> actions =
    	         policy.getActions(status);
    	 
    	 return actions;

    	
    }
    
    */
    
    public List<VerificationExecutionAction> getVerificationMethodActions(
            VerificationExecutionStatus status,VerificationMethodCode methodCode) {
    	
    	 Long userId = SecurityUtils.getCurrentUserId();
    	 User fieldAgent = userService.getUserById(userId);
    	
    	 User currentUser = userService.getUserById(SecurityUtils.getCurrentUserId());

    	 ActionPolicy policy = actionPolicyFactory.getPolicy(currentUser);

    	 List<VerificationExecutionAction> actions =
    	         policy.getActions(status,methodCode);
    	 
    	 return actions;

    	
    }
}
