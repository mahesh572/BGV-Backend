package com.org.bgv.vendor.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.enums.VerificationMethodCode;

@Component
public class VendorAdminActionPolicy implements ActionPolicy{

	
	 @Override
	    public List<VerificationExecutionAction> getActions(
	            VerificationExecutionStatus status,VerificationMethodCode methodCode) {

	        switch (status) {
	        
		        case UNDER_REVIEW:
	                return List.of(
	                        VerificationExecutionAction.APPROVE,
	                        VerificationExecutionAction.REWORK,
	                        VerificationExecutionAction.ESCALATE,
	                        VerificationExecutionAction.ADD_NOTE
	                );
	            case PENDING_REVIEW:
	                return List.of(
	                        VerificationExecutionAction.MARK_UNDER_REVIEW,
	                        VerificationExecutionAction.ADD_NOTE
	                );

	            case COMPLETED:
	                return List.of(
	                        VerificationExecutionAction.ADD_NOTE
	                );

	            default:
	                return List.of();
	        }
	    }
}
