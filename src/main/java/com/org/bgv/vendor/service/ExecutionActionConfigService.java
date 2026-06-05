package com.org.bgv.vendor.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.enums.VerificationMethodCode;

@Service
public class ExecutionActionConfigService {

	    public List<VerificationExecutionAction> getActions(
	            VerificationMethodCode methodCode,
	            VerificationExecutionStatus status) {

	        switch (methodCode) {

	            case EMAIL:
	                return getEmailActions(status);

	            case PHONE_VERIFICATION:
	                return getPhoneActions(status);
	            case FIELD_VISIT:
	            	return getFieldVisitActions(status);
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
    
    private List<VerificationExecutionAction> getFieldVisitActions(
            VerificationExecutionStatus status) {

        switch (status) {

            case INITIATED:
                return List.of(
                        VerificationExecutionAction.ASSIGN_FIELD_AGENT,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.CANCEL
                );

            case VISIT_ASSIGNED:
                return List.of(
                      //  VerificationExecutionAction.SCHEDULE_VISIT,
                		VerificationExecutionAction.RESCHEDULE_VISIT,
                        VerificationExecutionAction.CHANGE_FIELD_AGENT,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.CANCEL
                );

            case VISIT_SCHEDULED:
                return List.of(
                        VerificationExecutionAction.START_VISIT,
                        VerificationExecutionAction.RESCHEDULE_VISIT,
                        VerificationExecutionAction.CHANGE_FIELD_AGENT,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.CANCEL
                );

            case VISIT_IN_PROGRESS:
                return List.of(
                        VerificationExecutionAction.UPLOAD_EVIDENCE,
                        VerificationExecutionAction.CAPTURE_LOCATION,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.MARK_VISIT_COMPLETED,
                        VerificationExecutionAction.MARK_ADDRESS_NOT_FOUND,
                        VerificationExecutionAction.MARK_CANDIDATE_NOT_AVAILABLE
                );

            case VISIT_COMPLETED:
                return List.of(
                       // VerificationExecutionAction.UPLOAD_EVIDENCE,
                        VerificationExecutionAction.ADD_NOTE
                       // VerificationExecutionAction.MARK_UNDER_REVIEW
                );

            case UNDER_REVIEW:
                return List.of(
                        VerificationExecutionAction.UPLOAD_EVIDENCE,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.MARK_VERIFIED,
                        VerificationExecutionAction.MARK_DISCREPANCY_FOUND,
                        VerificationExecutionAction.MARK_UNABLE_TO_VERIFY
                );

            case VERIFIED:
            case DISCREPANCY_FOUND:
            case UNABLE_TO_VERIFY:
                return List.of(
                        VerificationExecutionAction.COMPLETE
                );

            case COMPLETED:
            case CANCELLED:
                return List.of();

            default:
                return List.of();
        }
    }
}
