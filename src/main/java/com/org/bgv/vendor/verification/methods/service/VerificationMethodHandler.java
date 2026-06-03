package com.org.bgv.vendor.verification.methods.service;

import com.org.bgv.vendor.entity.VerificationMethodExecution;

public interface VerificationMethodHandler {

    void execute(
            VerificationMethodExecution execution,VerificationContext context);

}
