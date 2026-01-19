package com.org.bgv.notifications.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.notifications.dto.SmsTemplateDTO;
import com.org.bgv.notifications.service.SmsTemplateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/templates/sms")
@RequiredArgsConstructor
public class SmsTemplateController {

    private final SmsTemplateService service;

    @GetMapping
    public List<SmsTemplateDTO> list(
            @RequestParam(required = false) Long companyId
    ) {
        return service.list(companyId);
    }

    @PutMapping("/{templateCode}")
    public SmsTemplateDTO save(
            @PathVariable String templateCode,
            @RequestParam(required = false) Long companyId,
            @RequestBody SmsTemplateDTO dto
    ) {
        return service.save(templateCode, companyId, dto);
    }
}
