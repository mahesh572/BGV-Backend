package com.org.bgv.vendor.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.enums.VerificationExecutionStatus;

@Service
public class ExecutionActionConfigService {

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
}
