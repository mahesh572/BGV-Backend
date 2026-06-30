package com.org.bgv.vendor.service;

import java.util.List;

import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.enums.VerificationMethodCode;

public interface ActionPolicy {

    List<VerificationExecutionAction> getActions(
            VerificationExecutionStatus status,VerificationMethodCode methodCode);

}
