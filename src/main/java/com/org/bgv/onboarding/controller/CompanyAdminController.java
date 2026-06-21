package com.org.bgv.onboarding.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.onboarding.dto.CompanyAdminResponse;
import com.org.bgv.onboarding.dto.CreateCompanyAdminRequest;
import com.org.bgv.onboarding.service.CompanyAdminService;
import com.org.bgv.role.dto.RoleDetailDto;
import com.org.bgv.service.RoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/companies/{companyId}/admin-account")
@RequiredArgsConstructor
@Slf4j
public class CompanyAdminController {

    private final CompanyAdminService companyAdminService;
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<CompanyAdminResponse>>
    createAdminUser(
            @PathVariable Long companyId,
            @RequestBody CreateCompanyAdminRequest request) {

        try {

            log.info(
                    "createAdminUser companyId={} request={}",
                    companyId,
                    request);

            CompanyAdminResponse response =
                    companyAdminService.createAdminUser(
                            companyId,
                            request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            CustomApiResponse.success(
                                    "Admin account created successfully",
                                    response,
                                    HttpStatus.CREATED));

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(
                            CustomApiResponse.failure(
                                    e.getMessage(),
                                    HttpStatus.BAD_REQUEST));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(
                            CustomApiResponse.failure(
                                    "Failed to create admin account",
                                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/roles")
    public ResponseEntity<CustomApiResponse<List<RoleDetailDto>>> getRolesByCompany(
            @PathVariable Long companyId) {

        try {

            List<RoleDetailDto> roles =
            		roleService.getRolesByCompany(companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Roles fetched successfully",
                            roles,
                            HttpStatus.OK));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to fetch roles",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
