package com.org.bgv.vendor.service;


import java.util.List;

import org.springframework.stereotype.Component;

import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.enums.VerificationExecutionStatus;

@Component
public class FieldAgentActionPolicy implements ActionPolicy {

    @Override
    public List<VerificationExecutionAction> getActions(
            VerificationExecutionStatus status) {

        switch (status) {

            case VISIT_SCHEDULED:
                return List.of(
                        VerificationExecutionAction.START_VISIT,
                        VerificationExecutionAction.ADD_NOTE
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
                        VerificationExecutionAction.ADD_NOTE
                );

            default:
                return List.of();
        }
    }
}
