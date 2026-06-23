package com.org.bgv.vendor.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.vendor.dto.EvidenceResponseDTO;
import com.org.bgv.vendor.service.EvidenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/vendor/evidence")
@RequiredArgsConstructor
@Slf4j
public class EvidenceController {

    private final EvidenceService evidenceService;

    // ==================== LEVEL 1: CHECK LEVEL ====================
    /**
     * Get ALL evidences (action + method) for a specific check
     * GET /api/vendor/evidence/check/{checkId}
     */
    @GetMapping("/check/{checkId}")
    public ResponseEntity<CustomApiResponse<List<EvidenceResponseDTO>>> getEvidenceForCheck(
            @PathVariable Long checkId) {
        try {
            log.info("Fetching evidence for check ID: {}", checkId);
            List<EvidenceResponseDTO> evidence = evidenceService.getEvidenceForCheck(checkId);
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Evidence retrieved successfully",
                            evidence,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Error fetching evidence for check ID: {}", checkId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve evidence: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ==================== LEVEL 2: ACTION LEVEL ====================
    /**
     * Get evidences for a specific action
     * GET /api/vendor/evidence/action/{actionId}
     */
    @GetMapping("/action/{actionId}")
    public ResponseEntity<CustomApiResponse<List<EvidenceResponseDTO>>> getEvidenceForAction(
            @PathVariable Long actionId) {
        try {
            log.info("Fetching evidence for action ID: {}", actionId);
            List<EvidenceResponseDTO> evidence = evidenceService.getEvidenceForAction(actionId);
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Evidence retrieved successfully",
                            evidence,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Error fetching evidence for action ID: {}", actionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve evidence: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ==================== LEVEL 3: EXECUTION LEVEL ====================
    /**
     * Get evidences for a specific method execution
     * GET /api/vendor/evidence/execution/{executionId}
     */
    @GetMapping("/execution/{executionId}")
    public ResponseEntity<CustomApiResponse<List<EvidenceResponseDTO>>> getEvidenceForExecution(
            @PathVariable Long executionId) {
        try {
            log.info("Fetching evidence for execution ID: {}", executionId);
            List<EvidenceResponseDTO> evidence = evidenceService.getEvidenceForExecution(executionId);
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Evidence retrieved successfully",
                            evidence,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Error fetching evidence for execution ID: {}", executionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve evidence: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ==================== LEVEL 4: OBJECT LEVEL ====================
    /**
     * Get evidences by object (e.g., EMAIL, PORTAL, HR_CONFIRMATION)
     * GET /api/vendor/evidence/object?objectType={objectType}&objectId={objectId}
     */
    @GetMapping("/object")
    public ResponseEntity<CustomApiResponse<List<EvidenceResponseDTO>>> getEvidenceByObject(
            @RequestParam String objectType,
            @RequestParam Long objectId) {
        try {
            log.info("Fetching evidence for object - Type: {}, ID: {}", objectType, objectId);
            List<EvidenceResponseDTO> evidence = evidenceService.getEvidenceByObject(objectType, objectId);
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Evidence retrieved successfully",
                            evidence,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Error fetching evidence for object - Type: {}, ID: {}", objectType, objectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve evidence: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ==================== LEVEL 5: CHECK + OBJECT LEVEL ====================
    /**
     * Get evidences by check and object
     * GET /api/vendor/evidence/check/{checkId}/object?objectType={objectType}&objectId={objectId}
     */
    @GetMapping("/check/{checkId}/object")
    public ResponseEntity<CustomApiResponse<List<EvidenceResponseDTO>>> getEvidenceByCheckAndObject(
            @PathVariable Long checkId,
            @RequestParam String objectType,
            @RequestParam Long objectId) {
        try {
            log.info("Fetching evidence for check ID: {}, object - Type: {}, ID: {}", checkId, objectType, objectId);
            List<EvidenceResponseDTO> evidence = evidenceService.getEvidenceByCheckAndObject(checkId, objectType, objectId);
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Evidence retrieved successfully",
                            evidence,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Error fetching evidence for check ID: {}, object - Type: {}, ID: {}", checkId, objectType, objectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve evidence: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ==================== LEVEL 6: CHECK + ACTION LEVEL ====================
    /**
     * Get evidences by check and action
     * GET /api/vendor/evidence/check/{checkId}/action/{actionId}
     */
    @GetMapping("/check/{checkId}/action/{actionId}")
    public ResponseEntity<CustomApiResponse<List<EvidenceResponseDTO>>> getEvidenceByCheckAndAction(
            @PathVariable Long checkId,
            @PathVariable Long actionId) {
        try {
            log.info("Fetching evidence for check ID: {}, action ID: {}", checkId, actionId);
            List<EvidenceResponseDTO> evidence = evidenceService.getEvidenceByCheckAndAction(checkId, actionId);
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Evidence retrieved successfully",
                            evidence,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Error fetching evidence for check ID: {}, action ID: {}", checkId, actionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve evidence: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ==================== LEVEL 7: DOCUMENT LEVEL ====================
    /**
     * Get evidences by document ID
     * GET /api/vendor/evidence/document/{documentId}
     */
    @GetMapping("/document/{documentId}")
    public ResponseEntity<CustomApiResponse<List<EvidenceResponseDTO>>> getEvidenceByDocumentId(
            @PathVariable Long documentId) {
        try {
            log.info("Fetching evidence for document ID: {}", documentId);
            List<EvidenceResponseDTO> evidence = evidenceService.getEvidenceByDocumentId(documentId);
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Evidence retrieved successfully",
                            evidence,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Error fetching evidence for document ID: {}", documentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve evidence: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ==================== LEVEL 8: SOURCE LEVEL ====================
    /**
     * Get evidences by source (CANDIDATE_DOCUMENT / VENDOR_UPLOAD)
     * GET /api/vendor/evidence/source/{source}
     */
    @GetMapping("/source/{source}")
    public ResponseEntity<CustomApiResponse<List<EvidenceResponseDTO>>> getEvidenceBySource(
            @PathVariable String source) {
        try {
            log.info("Fetching evidence for source: {}", source);
            List<EvidenceResponseDTO> evidence = evidenceService.getEvidenceBySource(source);
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Evidence retrieved successfully",
                            evidence,
                            HttpStatus.OK
                    )
            );
        } catch (IllegalArgumentException e) {
            log.error("Invalid source value: {}", source, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            "Invalid source value. Allowed values: CANDIDATE_DOCUMENT, VENDOR_UPLOAD",
                            HttpStatus.BAD_REQUEST
                    ));
        } catch (Exception e) {
            log.error("Error fetching evidence for source: {}", source, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve evidence: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ==================== LEVEL 9: CHECK + SOURCE ====================
    /**
     * Get evidences by check and source
     * GET /api/vendor/evidence/check/{checkId}/source/{source}
     */
    @GetMapping("/check/{checkId}/source/{source}")
    public ResponseEntity<CustomApiResponse<List<EvidenceResponseDTO>>> getEvidenceByCheckAndSource(
            @PathVariable Long checkId,
            @PathVariable String source) {
        try {
            log.info("Fetching evidence for check ID: {}, source: {}", checkId, source);
            List<EvidenceResponseDTO> evidence = evidenceService.getEvidenceByCheckAndSource(checkId, source);
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Evidence retrieved successfully",
                            evidence,
                            HttpStatus.OK
                    )
            );
        } catch (IllegalArgumentException e) {
            log.error("Invalid source value: {}", source, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            "Invalid source value. Allowed values: CANDIDATE_DOCUMENT, VENDOR_UPLOAD",
                            HttpStatus.BAD_REQUEST
                    ));
        } catch (Exception e) {
            log.error("Error fetching evidence for check ID: {}, source: {}", checkId, source, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve evidence: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ==================== LEVEL 10: ALL EVIDENCE FOR OBJECT ====================
    /**
     * Get all evidence (action + method) for a specific object
     * GET /api/vendor/evidence/all/object?objectType={objectType}&objectId={objectId}
     */
    @GetMapping("/all/object")
    public ResponseEntity<CustomApiResponse<List<EvidenceResponseDTO>>> getAllEvidenceForObject(
            @RequestParam String objectType,
            @RequestParam Long objectId) {
        try {
            log.info("Fetching all evidence for object - Type: {}, ID: {}", objectType, objectId);
            List<EvidenceResponseDTO> evidence = evidenceService.getAllEvidenceForObject(objectType, objectId);
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Evidence retrieved successfully",
                            evidence,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Error fetching all evidence for object - Type: {}, ID: {}", objectType, objectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve evidence: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ==================== RECENT EVIDENCE ====================
    /**
     * Get recent evidences (for dashboard quick view)
     * GET /api/vendor/evidence/recent?limit={limit}
     */
    @GetMapping("/recent")
    public ResponseEntity<CustomApiResponse<List<EvidenceResponseDTO>>> getRecentEvidences(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("Fetching recent {} evidences", limit);
            List<EvidenceResponseDTO> evidence = evidenceService.getRecentEvidences(limit);
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Recent evidence retrieved successfully",
                            evidence,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Error fetching recent evidences", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve recent evidence: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ==================== BULK EVIDENCE ====================
    /**
     * Get evidences for multiple checks (for dashboard)
     * POST /api/vendor/evidence/bulk
     */
    @PostMapping("/bulk")
    public ResponseEntity<CustomApiResponse<List<EvidenceResponseDTO>>> getEvidenceForChecks(
            @RequestBody List<Long> checkIds) {
        try {
            log.info("Fetching evidence for {} checks", checkIds != null ? checkIds.size() : 0);
            if (checkIds == null || checkIds.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(CustomApiResponse.failure(
                                "Check IDs list cannot be empty",
                                HttpStatus.BAD_REQUEST
                        ));
            }
            List<EvidenceResponseDTO> evidence = evidenceService.getEvidenceForChecks(checkIds);
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Evidence retrieved successfully",
                            evidence,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Error fetching bulk evidence", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve evidence: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // ==================== EVIDENCE COUNT ====================
    /**
     * Get evidence count for a check
     * GET /api/vendor/evidence/check/{checkId}/count
     */
    @GetMapping("/check/{checkId}/count")
    public ResponseEntity<CustomApiResponse<Long>> getEvidenceCountForCheck(
            @PathVariable Long checkId) {
        try {
            log.info("Fetching evidence count for check ID: {}", checkId);
            List<EvidenceResponseDTO> evidence = evidenceService.getEvidenceForCheck(checkId);
            long count = evidence != null ? evidence.size() : 0;
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Evidence count retrieved successfully",
                            count,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Error fetching evidence count for check ID: {}", checkId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve evidence count: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
}