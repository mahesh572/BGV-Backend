package com.org.bgv.vendor.verification.methods.service;

import org.springframework.stereotype.Service;

import com.org.bgv.enums.VerificationMethodCode;
import com.org.bgv.vendor.entity.VerificationMethodExecution;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationWorkflowDispatcher {

    private final EmailToHrHandler emailToHrHandler;
    private final PhoneCallHandler phoneCallHandler;
    private final FieldVisitHandler fieldVisitHandler;

    public void dispatch(
            VerificationMethodExecution execution,VerificationContext context) {

    	VerificationMethodCode code =
                execution.getVerificationMethod()
                        .getCode();

        switch (code) {

            case VerificationMethodCode.EMAIL:
                emailToHrHandler.execute(execution,context);
                break;

            case VerificationMethodCode.PHONE_VERIFICATION:
                phoneCallHandler.execute(execution,context);
                break;

            case VerificationMethodCode.FIELD_VISIT:
                fieldVisitHandler.execute(execution,context);
                break;

            default:
                throw new RuntimeException(
                        "Unsupported verification method: "
                                + code);
        }
    }
}