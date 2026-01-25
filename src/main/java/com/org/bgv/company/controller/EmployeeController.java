package com.org.bgv.company.controller;


import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.Status;
import com.org.bgv.company.dto.CreateEmployeeRequest;
import com.org.bgv.company.dto.UpdateEmployeeRequest;
import com.org.bgv.company.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/company/{companyId}/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // ============================
    // ADD EMPLOYEE
    // ============================
    @PostMapping
    public ResponseEntity<CustomApiResponse<?>> addEmployee(
            @PathVariable Long companyId,
            @RequestBody CreateEmployeeRequest employeeDTO) {

        log.info("Add employee request | companyId={} | email={}",
                companyId, employeeDTO.getEmailAddress());

        try {
             employeeService.createEmployee(
                    companyId,
                    employeeDTO
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success(
                            "Employee added successfully",
                            "",
                            HttpStatus.CREATED
                    ));

        } catch (IllegalArgumentException e) {
            log.warn("Validation error while adding employee: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));

        } catch (RuntimeException e) {
            log.error("Business error while adding employee: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.CONFLICT));

        } catch (Exception e) {
            log.error("Unexpected error while adding employee", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Internal server error while adding employee",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ============================
    // UPDATE EMPLOYEE
    // ============================
    @PutMapping("/{employeeId}")
    public ResponseEntity<CustomApiResponse<?>> updateEmployee(
            @PathVariable Long companyId,
            @PathVariable Long employeeId,
            @RequestBody UpdateEmployeeRequest employeeDTO) {

        log.info("Update employee request | companyId={} | employeeId={}",
                companyId, employeeId);

        try {
             employeeService.updateEmployee(
                    companyId,
                    employeeId,
                    employeeDTO
            );

            return ResponseEntity.ok(CustomApiResponse.success(
                    "Employee updated successfully",
                    "",
                    HttpStatus.OK
            ));

        } catch (IllegalArgumentException e) {
            log.warn("Validation error while updating employee: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));

        } catch (RuntimeException e) {
            log.error("Business error while updating employee: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.CONFLICT));

        } catch (Exception e) {
            log.error("Unexpected error while updating employee", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Internal server error while updating employee",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ============================
    // REMOVE / DEACTIVATE EMPLOYEE
    // ============================
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<CustomApiResponse<?>> removeEmployee(
            @PathVariable Long companyId,
            @PathVariable Long employeeId) {

        log.info("Remove employee request | companyId={} | employeeId={}",
                companyId, employeeId);

        try {
             employeeService.deactivateEmployee(companyId, employeeId);

            return ResponseEntity.ok(CustomApiResponse.success(
                    "Employee removed successfully",
                    "",
                    HttpStatus.OK
            ));

        } catch (IllegalArgumentException e) {
            log.warn("Validation error while removing employee: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));

        } catch (RuntimeException e) {
            log.error("Business error while removing employee: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.CONFLICT));

        } catch (Exception e) {
            log.error("Unexpected error while removing employee", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Internal server error while removing employee",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
}
