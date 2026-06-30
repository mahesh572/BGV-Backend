package com.org.bgv.vendor.service;


import java.util.List;

import org.springframework.stereotype.Component;

import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.enums.VerificationMethodCode;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class VendorAgentActionPolicy implements ActionPolicy {

    @Override
    public List<VerificationExecutionAction> getActions(
            VerificationExecutionStatus status,VerificationMethodCode methodCode) {
    	
    	log.info("VendorAgentActionPolicy::::::::::::::::::::::{}",status);
    	
    	
    	/*

        switch (status) {

            case INITIATED:
                return List.of(
                        VerificationExecutionAction.ASSIGN_FIELD_AGENT,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.CANCEL
                );

            case VISIT_ASSIGNED:   // FIELD_VISIT
            case VISIT_SCHEDULED: // FIELD_VISIT
                return List.of(
                        VerificationExecutionAction.RESCHEDULE_VISIT,
                        VerificationExecutionAction.CHANGE_FIELD_AGENT,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.CANCEL
                );

            case VISIT_IN_PROGRESS:   // FIELD_VISIT
                return List.of(
                        VerificationExecutionAction.ADD_NOTE
                );

            case VISIT_COMPLETED:  // FIELD_VISIT
                return List.of(
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.MARK_UNDER_REVIEW
                );

            case UNDER_REVIEW:
                return List.of(
                        VerificationExecutionAction.MARK_VERIFIED,
                        VerificationExecutionAction.MARK_DISCREPANCY_FOUND,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.MARK_UNABLE_TO_VERIFY,
                        VerificationExecutionAction.UPLOAD_EVIDENCE
                );

            case VERIFIED:
            case DISCREPANCY_FOUND:
            case UNABLE_TO_VERIFY:
                return List.of(
                        // VerificationExecutionAction.COMPLETE
                );
            case PHONE_CALL_INITIATED:     // PHONE_VERIFICATION
                return List.of(
                        VerificationExecutionAction.MAKE_PHONE_CALL,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.CANCEL);
            case PHONE_CALL_IN_PROGRESS:   //PHONE_VERIFICATION
                return List.of(
                        VerificationExecutionAction.MAKE_PHONE_CALL,
                        VerificationExecutionAction.MARK_PHONE_CALL_COMPLETED,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.CANCEL);   
            case PHONE_CALL_FAILED: // PHONE_VERIFICATION
                return List.of(
                        VerificationExecutionAction.MAKE_PHONE_CALL,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.CONTACT_CANDIDATE,
                        VerificationExecutionAction.CANCEL
                );

                case WAITING_FOR_RESPONSE:  // PHONE_VERIFICATION
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

            case COMPLETED:
            	 return List.of(
                         VerificationExecutionAction.ADD_NOTE,
                         VerificationExecutionAction.MARK_UNDER_REVIEW
                 );
            case CANCELLED:
                return List.of();

            default:
                return List.of();
        }
    }
    
    
    */
    	switch (status) {

        case INITIATED:
        	return getInitiatedActions(methodCode);

        case IN_PROGRESS:
            return getInProgressActions(methodCode);

        case PENDING_REVIEW:
            return List.of(
                   // VerificationExecutionAction.APPROVE,
                   // VerificationExecutionAction.REWORK,
                    VerificationExecutionAction.ADD_NOTE
            );
            
        case WAITING_FOR_RESPONSE:
        	return List.of(
                    VerificationExecutionAction.MARK_RESPONSE_RECEIVED,
                    VerificationExecutionAction.UPLOAD_EVIDENCE,
                    VerificationExecutionAction.RESEND_EMAIL,
                    VerificationExecutionAction.ADD_NOTE,
                    VerificationExecutionAction.CANCEL
            );
        	
        case VISIT_SCHEDULED: // FIELD_VISIT
            return List.of(
                    VerificationExecutionAction.RESCHEDULE_VISIT,
                    VerificationExecutionAction.CHANGE_FIELD_AGENT,
                    VerificationExecutionAction.ADD_NOTE,
                    VerificationExecutionAction.CANCEL
            );

        case COMPLETED:
            return List.of(
                    VerificationExecutionAction.ADD_NOTE
            );

        case CANCELLED:
            return List.of();

        default:
            return List.of();
    }
    }
    
    
    private List<VerificationExecutionAction> getInitiatedActions(VerificationMethodCode methodCode){
    	
    	log.info("getInitiatedActions::::::::::::::::::::::::::::::::::{}",methodCode);
    	
    	 switch (methodCode) {
    	 
	    	 case FIELD_VISIT:
	    		 log.info("getInitiatedActions:::::::::::FIELD_VISIT:::::::");
	    		 return List.of(
	                     VerificationExecutionAction.ASSIGN_FIELD_AGENT,
	                     VerificationExecutionAction.ADD_NOTE,
	                     VerificationExecutionAction.CANCEL
	             );
	    		 
	    	 case PHONE_VERIFICATION:
	                return List.of(
	                        VerificationExecutionAction.MAKE_PHONE_CALL,
	                        VerificationExecutionAction.MARK_PHONE_CALL_COMPLETED,
	                        VerificationExecutionAction.ADD_NOTE,
	                        VerificationExecutionAction.CANCEL
	                );
	    	 case EMAIL:
	    		 return List.of(
	    				    VerificationExecutionAction.SEND_EMAIL,
	    				    VerificationExecutionAction.UPLOAD_EVIDENCE,
	                        VerificationExecutionAction.ADD_NOTE,
	                        VerificationExecutionAction.CANCEL
	                        );
	    	 default:
	             return List.of();
    	 
    	 }
    	
    }
    	
    
    private List<VerificationExecutionAction> getInProgressActions(
            VerificationMethodCode methodCode) {

        switch (methodCode) {

            case PHONE_VERIFICATION:
                return List.of(
                        VerificationExecutionAction.MAKE_PHONE_CALL,
                        VerificationExecutionAction.MARK_PHONE_CALL_COMPLETED,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.CANCEL
                );

            case EMAIL:
                return List.of(
                		VerificationExecutionAction.UPLOAD_EVIDENCE,
                        VerificationExecutionAction.MARK_EMAIL_VERIFICATION_COMPLETED,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.CANCEL
                );

            case PORTAL:
                return List.of(
                     //   VerificationExecutionAction.LOGIN_PORTAL,
                        VerificationExecutionAction.UPLOAD_EVIDENCE,
                     //   VerificationExecutionAction.MARK_PORTAL_COMPLETED,
                        VerificationExecutionAction.ADD_NOTE
                );

            case FIELD_VISIT:
                return List.of(
                        VerificationExecutionAction.START_VISIT,
                      //  VerificationExecutionAction.COMPLETE_VISIT,
                        VerificationExecutionAction.ADD_NOTE
                );

            case DATABASE:
                return List.of(
                     //   VerificationExecutionAction.RUN_DATABASE_CHECK,
                     //   VerificationExecutionAction.MARK_DATABASE_COMPLETED,
                        VerificationExecutionAction.ADD_NOTE
                );

            default:
                return List.of(
                        VerificationExecutionAction.ADD_NOTE
                );
        }
    }
}