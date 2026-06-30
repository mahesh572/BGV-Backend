package com.org.bgv.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.user.kyc.requests.PanVerificationRequest;
import com.org.bgv.user.kyc.requests.PanVerificationResponse;
import com.org.bgv.user.kyc.service.PanKycService;

@RestController
@RequestMapping("/api/kyc/pan")
public class PanKycController {

    private final PanKycService service;

    public PanKycController(PanKycService service) {
        this.service = service;
    }

    @PostMapping("/verify")
    public PanVerificationResponse verify(@RequestBody PanVerificationRequest request) {
        return service.verify(request);
    }
}