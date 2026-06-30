package com.org.bgv.vendor.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.ActivityTimelineDTO;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.s3.S3StorageService;
import com.org.bgv.service.ActivityTimelineService;
import com.org.bgv.vendor.dto.AssignFieldAgentRequest;
import com.org.bgv.vendor.dto.FieldAgentDto;
import com.org.bgv.vendor.dto.StartVerificationMethodRequest;
import com.org.bgv.vendor.dto.UpdateExecutionStatusRequest;
import com.org.bgv.vendor.dto.UpdateVisitLocationRequest;
import com.org.bgv.vendor.dto.VerificationMethodDTO;
import com.org.bgv.vendor.dto.VerificationMethodExecutionDetailsDto;
import com.org.bgv.vendor.evidence.dto.VerificationExecutionEvidenceDto;
import com.org.bgv.vendor.service.VerificationActionService;
import com.org.bgv.vendor.service.VerificationExecutionEvidenceService;
import com.org.bgv.vendor.service.VerificationMethodExecutionService;
import com.org.bgv.vendor.service.VerificationMethodService;
import com.org.bgv.vendor.verification.methods.service.VerificationMethodTrackingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/vendor/verifications")
@RequiredArgsConstructor
@Slf4j
public class VerificationMethodsController {

	private final VerificationMethodService verificationMethodService;
	private final VerificationMethodExecutionService service;
	private final VerificationMethodTrackingService trackingService;
	private final VerificationExecutionEvidenceService evidenceService;
	private final ActivityTimelineService activityTimelineService;
	
	@GetMapping("/checks/{checkType}/verification-methods")
	public ResponseEntity<CustomApiResponse<List<VerificationMethodDTO>>> getMethods(
	        @PathVariable CheckCategoryEnum checkType) {

	    try {
	        List<VerificationMethodDTO> methods =
	                verificationMethodService.getMethodsForCheck(checkType);

	        return ResponseEntity.ok(
	                CustomApiResponse.success(
	                        "Verification methods retrieved successfully",
	                        methods,
	                        HttpStatus.OK
	                )
	        );

	    } catch (RuntimeException e) {

	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(
	                        CustomApiResponse.failure(
	                                e.getMessage(),
	                                HttpStatus.NOT_FOUND
	                        )
	                );

	    } catch (Exception e) {

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(
	                        CustomApiResponse.failure(
	                                "Failed to retrieve verification methods: " + e.getMessage(),
	                                HttpStatus.INTERNAL_SERVER_ERROR
	                        )
	                );
	    }
	}
	
	@PostMapping("/start")
    public ResponseEntity<CustomApiResponse<Long>> startVerification(
            @RequestBody StartVerificationMethodRequest request) {

        try {

            Long executionId = service.startVerification(request);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Verification method started successfully",
                            executionId,
                            HttpStatus.OK
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            CustomApiResponse.failure(
                                    e.getMessage(),
                                    HttpStatus.BAD_REQUEST
                            )
                    );

        } catch (Exception e) {

            log.error("Error starting verification method", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            CustomApiResponse.failure(
                                    "Failed to start verification method: " + e.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR
                            )
                    );
        }
    }
	@GetMapping("/check/{checkId}")
	public ResponseEntity<
	        CustomApiResponse<List<VerificationMethodExecutionDetailsDto>>>
	getExecutions(
	        @PathVariable Long checkId) {

	    try {

	        List<VerificationMethodExecutionDetailsDto> executions =
	                trackingService.getExecutions(checkId);

	        return ResponseEntity.ok(
	                CustomApiResponse.success(
	                        "Verification executions retrieved successfully",
	                        executions,
	                        HttpStatus.OK
	                )
	        );

	    } catch (RuntimeException e) {

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(
	                        CustomApiResponse.failure(
	                                e.getMessage(),
	                                HttpStatus.BAD_REQUEST
	                        )
	                );

	    } catch (Exception e) {

	        log.error(
	                "Error retrieving verification executions for checkId={}",
	                checkId,
	                e
	        );

	        return ResponseEntity.status(
	                HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(
	                        CustomApiResponse.failure(
	                                "Failed to retrieve verification executions: "
	                                        + e.getMessage(),
	                                HttpStatus.INTERNAL_SERVER_ERROR
	                        )
	                );
	    }
	}
	
	@GetMapping("/execution/{executionId}")
	public ResponseEntity<
	        CustomApiResponse<VerificationMethodExecutionDetailsDto>>
	getExecution(
	        @PathVariable Long executionId) {

	    try {

	        VerificationMethodExecutionDetailsDto execution =
	                trackingService.getExecution(executionId);

	        return ResponseEntity.ok(
	                CustomApiResponse.success(
	                        "Verification execution retrieved successfully",
	                        execution,
	                        HttpStatus.OK
	                )
	        );

	    } catch (RuntimeException e) {

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(
	                        CustomApiResponse.failure(
	                                e.getMessage(),
	                                HttpStatus.BAD_REQUEST
	                        )
	                );

	    } catch (Exception e) {

	        log.error(
	                "Error retrieving verification execution {}",
	                executionId,
	                e
	        );

	        return ResponseEntity.status(
	                HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(
	                        CustomApiResponse.failure(
	                                "Failed to retrieve verification execution: "
	                                        + e.getMessage(),
	                                HttpStatus.INTERNAL_SERVER_ERROR
	                        )
	                );
	    }
	}
	
	@GetMapping(
		    "/checks/{checkId}/objects/{objectId}/executions")
		public ResponseEntity<
		        CustomApiResponse<
		                List<VerificationMethodExecutionDetailsDto>>>
		getExecutions(
		        @PathVariable Long checkId,
		        @PathVariable Long objectId) {

		    try {

		        List<VerificationMethodExecutionDetailsDto> executions =
		                trackingService.getExecutions(
		                        checkId,
		                        objectId);

		        return ResponseEntity.ok(
		                CustomApiResponse.success(
		                        "Verification executions retrieved successfully",
		                        executions,
		                        HttpStatus.OK
		                )
		        );

		    } catch (Exception e) {

		        log.error(
		                "Error retrieving executions for checkId={} objectId={}",
		                checkId,
		                objectId,
		                e);

		        return ResponseEntity.status(
		                HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(
		                        CustomApiResponse.failure(
		                                e.getMessage(),
		                                HttpStatus.INTERNAL_SERVER_ERROR
		                        )
		                );
		    }
		}
	
	@PostMapping(
	        value = "/executions/{executionId}/evidence",
	        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	    )
	    public ResponseEntity<CustomApiResponse<List<VerificationExecutionEvidenceDto>>> uploadEvidence(
	            @PathVariable Long executionId,
	            @RequestParam Long checkId,
	            @RequestPart("files") List<MultipartFile> files,
	            @RequestParam(required = false) String notes) {

	        List<VerificationExecutionEvidenceDto> evidence =
	                evidenceService.uploadEvidence(
	                        executionId,
	                        checkId,
	                        files,
	                        notes
	                );

	        return ResponseEntity.ok(
	                CustomApiResponse.success(
	                        "Evidence uploaded successfully",
	                        evidence,
	                        HttpStatus.OK
	                )
	        );
	    }
	
	@GetMapping("/executions/{executionId}/evidence")
    public ResponseEntity<CustomApiResponse<List<VerificationExecutionEvidenceDto>>> getExecutionEvidence(
            @PathVariable Long executionId) {

        List<VerificationExecutionEvidenceDto> evidence =
                evidenceService.getExecutionEvidence(executionId);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Evidence fetched successfully",
                        evidence,
                        HttpStatus.OK
                )
        );
    }
	
	@PatchMapping("/executions/{executionId}/status")
	public ResponseEntity<CustomApiResponse<Void>> updateStatus(
	        @PathVariable Long executionId,
	        @RequestBody UpdateExecutionStatusRequest request
	) {
		 try {
		service.updateStatus(executionId, request);

	    return ResponseEntity.ok(
	            CustomApiResponse.success(
	                    "Execution status updated successfully",
	                    null,
	                    HttpStatus.OK
	            )
	    );
		 } catch (BusinessException ex) {

		        return ResponseEntity.badRequest().body(
		                CustomApiResponse.failure(
		                        ex.getMessage(),
		                        HttpStatus.BAD_REQUEST
		                )
		        );

		    } catch (Exception ex) {
		    	
		    	log.error("Failed to delete evidence {}",  ex);

		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
		                CustomApiResponse.failure(
		                        "Something went wrong",
		                        HttpStatus.INTERNAL_SERVER_ERROR
		                )
		        );
		    }
	}
	
	@DeleteMapping("/evidence/{evidenceId}")
	public ResponseEntity<CustomApiResponse<Void>> deleteEvidence(
	        @PathVariable Long evidenceId
	) {
	    try {
	        evidenceService.deleteEvidence(evidenceId);

	        return ResponseEntity.ok(
	                CustomApiResponse.success(
	                        "Evidence deleted successfully",
	                        null,
	                        HttpStatus.OK
	                )
	        );

	    } catch (BusinessException ex) {

	        return ResponseEntity.badRequest().body(
	                CustomApiResponse.failure(
	                        ex.getMessage(),
	                        HttpStatus.BAD_REQUEST
	                )
	        );

	    } catch (Exception ex) {
	    	
	    	log.error("Failed to delete evidence {}", evidenceId, ex);

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	                CustomApiResponse.failure(
	                        "Something went wrong",
	                        HttpStatus.INTERNAL_SERVER_ERROR
	                )
	        );
	    }
	}
	@GetMapping("/field-agents")
	public ResponseEntity<CustomApiResponse<List<FieldAgentDto>>> getFieldAgents() {

	    return ResponseEntity.ok(
	            CustomApiResponse.success(
	            		"",
	            		service.getFieldAgents(),
	            		 HttpStatus.OK
	            )
	    );
	}
	
	@PostMapping("/{executionId}/assign-field-agent")
	public ResponseEntity<CustomApiResponse<String>> assignFieldAgent(
	        @PathVariable Long executionId,
	        @RequestBody AssignFieldAgentRequest request) {

		service.assignToFieldAgent(executionId, request);

	    return ResponseEntity.ok(
	            CustomApiResponse.success(
	                    "Field agent assigned successfully",
	                    null,
	                    HttpStatus.OK
	            )
	    );
	}
	
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
	
	
	@GetMapping("/timeline")
	public ResponseEntity<CustomApiResponse<List<ActivityTimelineDTO>>> getTimeline(
	        @RequestParam Long checkId,
	        @RequestParam Long objectId) {
		
		log.info("timeline::::::::::::::::::::::::::::::::{}{}",checkId,objectId);

	    List<ActivityTimelineDTO> timeline =
	            activityTimelineService.getTimeline(
	                    checkId,
	                    objectId);

	    return ResponseEntity.ok(
	            CustomApiResponse.success(
	                    "Activity timeline fetched successfully",
	                    timeline,
	                    HttpStatus.OK
	            )
	    );
	}
}
