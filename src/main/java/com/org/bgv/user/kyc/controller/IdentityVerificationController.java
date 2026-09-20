package com.org.bgv.user.kyc.controller;

import java.nio.file.attribute.UserPrincipal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.DocumentType;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.user.kyc.requests.AadharVerificationRequest;
import com.org.bgv.user.kyc.requests.PanVerificationRequest;
import com.org.bgv.user.kyc.requests.PanVerificationResponse;
import com.org.bgv.user.kyc.requests.VerifyDigilockerAccountRequest;
import com.org.bgv.user.kyc.response.CreateDigilockerSessionResponse;
import com.org.bgv.user.kyc.response.DigilockerStatusResponse;
import com.org.bgv.user.kyc.response.UserKycVerificationResponse;
import com.org.bgv.user.kyc.service.DigilockerKycService;
import com.org.bgv.user.kyc.service.IdentityKycVerificationService;
import com.org.bgv.user.kyc.service.PanKycService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/identity-verifications")
@RequiredArgsConstructor
@Slf4j
public class IdentityVerificationController {

	private final PanKycService panKycService;
	private final IdentityKycVerificationService identityKycVerificationService;
	private final DigilockerKycService digilockerKycService;

	@PostMapping("/pan")
	public ResponseEntity<CustomApiResponse<?>> verifyPan(@Valid @RequestBody PanVerificationRequest request) {
		try {
			PanVerificationResponse response = panKycService.verify(request);

			return ResponseEntity.ok(CustomApiResponse.success("PAN verified successfully", "", HttpStatus.OK));
		} catch (BusinessException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomApiResponse
					.failure("Failed to verification Identity: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/aadhaar")
	public ResponseEntity<CustomApiResponse<?>> verifyAadhar(@Valid @RequestBody AadharVerificationRequest request) {
		try {
			//	PanVerificationResponse response = panKycService.verify(request);

			return ResponseEntity.ok(CustomApiResponse.success("PAN verified successfully", "", HttpStatus.OK));
		} catch (BusinessException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomApiResponse
					.failure("Failed to verification Identity: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@PostMapping("/aadhaar/start")
	public ResponseEntity<CustomApiResponse<?>> start(@RequestBody VerifyDigilockerAccountRequest request) {

		log.info("Received request to start DigiLocker Aadhaar verification.");

		CreateDigilockerSessionResponse response = digilockerKycService.startVerification(request);

		log.info("DigiLocker Aadhaar verification initiated successfully. referenceId={}", response.getReferenceId());

		return ResponseEntity.ok(CustomApiResponse.success("Status fetched successfully.", response, HttpStatus.OK));
	}

	@GetMapping("/digilocker/status")
	public ResponseEntity<CustomApiResponse<DigilockerStatusResponse>> checkStatus(
			@RequestParam("verification_id") String verificationId) {
		try {
			DigilockerStatusResponse response = digilockerKycService.checkStatus(verificationId);

			return ResponseEntity
					.ok(CustomApiResponse.success("Status fetched successfully.", response, HttpStatus.OK));
		} catch (BusinessException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomApiResponse
					.failure("Failed to verification Identity: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@GetMapping("/{documentType}/status")
	public ResponseEntity<CustomApiResponse<UserKycVerificationResponse>> getVerificationStatus(
			@PathVariable DocumentType documentType) {

		Long userId = SecurityUtils.getCurrentUserId();

		UserKycVerificationResponse response = identityKycVerificationService.getStatus(userId, documentType);

		return ResponseEntity
				.ok(CustomApiResponse.success("Verification status retrieved successfully.", response, HttpStatus.OK));
	}

}