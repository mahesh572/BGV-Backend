package com.org.bgv.vendor.controller;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.entity.User;
import com.org.bgv.vendor.dto.FieldAssignmentDTO;
import com.org.bgv.vendor.dto.UpdateVisitLocationRequest;
import com.org.bgv.vendor.service.FieldAgentService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/field-agent")
@RequiredArgsConstructor
@Slf4j
public class FieldAgentController {

    private final FieldAgentService service;

    /**
     * Dashboard summary
     */
    @GetMapping("/dashboard")
    public ResponseEntity<CustomApiResponse<Map<String, Object>>> getDashboard(
            ) {

        Map<String, Object> dashboard =
                service.getDashboardSummary();

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Dashboard fetched successfully",
                        dashboard,
                        HttpStatus.OK
                )
        );
    }

    /**
     * My assignments
     */
    @GetMapping("/assignments")
    public ResponseEntity<CustomApiResponse<List<FieldAssignmentDTO>>> getAssignments(
            ) {

        List<FieldAssignmentDTO> assignments =
                service.getMyAssignments();

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Assignments fetched successfully",
                        assignments,
                        HttpStatus.OK
                )
        );
    }

    
    /**
     * Assignment details
     */
    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<CustomApiResponse<FieldAssignmentDTO>> getAssignmentDetails(
            @PathVariable Long assignmentId){

        FieldAssignmentDTO dto =
                service.getAssignmentDetails(assignmentId);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Assignment details fetched successfully",
                        dto,
                        HttpStatus.OK
                )
        );
    }

    /**
     * Start visit
     */
    
    /*
    @PostMapping("/executions/{executionId}/start")
    public ResponseEntity<CustomApiResponse<String>> startVisit(
            @PathVariable Long executionId,
            @RequestBody StartVisitRequest request) {

        service.startVisit(
                executionId,
                request);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Visit started successfully",
                        null,
                        HttpStatus.OK
                )
        );
    }
*/
    /**
     * Complete visit
     */
    
    /*
    @PostMapping("/executions/{executionId}/complete")
    public ResponseEntity<CustomApiResponse<String>> completeVisit(
            @PathVariable Long executionId,
            @RequestBody CompleteVisitRequest request) {

        service.completeVisit(
                executionId,
                request);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Visit completed successfully",
                        null,
                        HttpStatus.OK
                )
        );
    }
*/
    /**
     * Update location
     */
    
    /*
    @PostMapping("/executions/{executionId}/location")
    public ResponseEntity<CustomApiResponse<String>> updateLocation(
            @PathVariable Long executionId,
            @RequestBody UpdateVisitLocationRequest request) {

        service.updateLocation(
                executionId,
                request);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Location updated successfully",
                        null,
                        HttpStatus.OK
                )
        );
    }
    
    */
}