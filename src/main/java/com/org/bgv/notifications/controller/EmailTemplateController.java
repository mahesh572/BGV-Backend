package com.org.bgv.notifications.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.notifications.dto.EmailTemplateDTO;
import com.org.bgv.notifications.service.EmailTemplateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/templates/email")
@RequiredArgsConstructor
public class EmailTemplateController {

    private final EmailTemplateService service;

    @GetMapping
    public List<EmailTemplateDTO> list(
            @RequestParam(required = false) Long companyId
    ) {
        return service.list(companyId);
    }

    @GetMapping("/{templateCode}")
    public EmailTemplateDTO get(
            @PathVariable String templateCode,
            @RequestParam(required = false) Long companyId
    ) {
        return service.get(templateCode, companyId);
    }

    @PutMapping("/{templateCode}")
    public EmailTemplateDTO save(
            @PathVariable String templateCode,
            @RequestParam(required = false) Long companyId,
            @RequestBody EmailTemplateDTO dto
    ) {
        return service.save(templateCode, companyId, dto);
    }

    @PostMapping("/{templateCode}/preview")
    public String preview(
            @PathVariable String templateCode,
            @RequestBody Map<String, Object> variables
    ) {
        return service.preview(templateCode, variables);
    }
}

