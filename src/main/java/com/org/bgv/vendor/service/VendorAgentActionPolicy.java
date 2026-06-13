package com.org.bgv.vendor.service;


import java.util.List;

import org.springframework.stereotype.Component;

import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.enums.VerificationExecutionStatus;

@Component
public class VendorAgentActionPolicy implements ActionPolicy {

    @Override
    public List<VerificationExecutionAction> getActions(
            VerificationExecutionStatus status) {

        switch (status) {

            case INITIATED:
                return List.of(
                        VerificationExecutionAction.ASSIGN_FIELD_AGENT,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.CANCEL
                );

            case VISIT_ASSIGNED:
            case VISIT_SCHEDULED:
                return List.of(
                        VerificationExecutionAction.RESCHEDULE_VISIT,
                        VerificationExecutionAction.CHANGE_FIELD_AGENT,
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.CANCEL
                );

            case VISIT_IN_PROGRESS:
                return List.of(
                        VerificationExecutionAction.ADD_NOTE
                );

            case VISIT_COMPLETED:
                return List.of(
                        VerificationExecutionAction.ADD_NOTE,
                        VerificationExecutionAction.MARK_UNDER_REVIEW
                );

            case UNDER_REVIEW:
                return List.of(
                        VerificationExecutionAction.MARK_VERIFIED,
                        VerificationExecutionAction.MARK_DISCREPANCY_FOUND,
                        VerificationExecutionAction.MARK_UNABLE_TO_VERIFY
                );

            case VERIFIED:
            case DISCREPANCY_FOUND:
            case UNABLE_TO_VERIFY:
                return List.of(
                        // VerificationExecutionAction.COMPLETE
                );

            case COMPLETED:
            case CANCELLED:
                return List.of();

            default:
                return List.of();
        }
    }
}