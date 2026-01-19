package com.org.bgv.notifications.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.dto.NotificationPolicyDTO;
import com.org.bgv.notifications.service.NotificationPolicyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/notification-policies")
@RequiredArgsConstructor
public class NotificationPolicyController {

    private final NotificationPolicyService policyService;

    // 1️⃣ Get all policies (platform or employer)
    @GetMapping
    public List<NotificationPolicyDTO> getPolicies(
            @RequestParam(required = false) Long companyId
    ) {
        return policyService.getPolicies(companyId);
    }

    // 2️⃣ Get single policy by event
    @GetMapping("/{event}")
    public NotificationPolicyDTO getPolicy(
            @PathVariable NotificationEvent event,
            @RequestParam(required = false) Long companyId
    ) {
        return policyService.getPolicy(event, companyId);
    }

    // 3️⃣ Save / Update policy (override or default)
    @PutMapping("/{event}")
    public NotificationPolicyDTO savePolicy(
            @PathVariable NotificationEvent event,
            @RequestParam(required = false) Long companyId,
            @RequestBody NotificationPolicyDTO dto
    ) {
        return policyService.savePolicy(event, companyId, dto);
    }

    // 4️⃣ Reset employer override to platform default
    @DeleteMapping("/{event}/override")
    public void resetOverride(
            @PathVariable NotificationEvent event,
            @RequestParam Long companyId
    ) {
        policyService.resetToPlatformDefault(event, companyId);
    }
}

