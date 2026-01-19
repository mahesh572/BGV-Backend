package com.org.bgv.notifications.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.notifications.dto.InAppTemplateDTO;
import com.org.bgv.notifications.service.InAppTemplateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/templates/in-app")
@RequiredArgsConstructor
public class InAppTemplateController {

    private final InAppTemplateService service;

    @GetMapping
    public List<InAppTemplateDTO> list(
            @RequestParam(required = false) Long copanyId
    ) {
        return service.list(copanyId);
    }

    @PutMapping("/{templateCode}")
    public InAppTemplateDTO save(
            @PathVariable String templateCode,
            @RequestParam(required = false) Long copanyId,
            @RequestBody InAppTemplateDTO dto
    ) {
        return service.save(templateCode, copanyId, dto);
    }
}

