package com.org.bgv.policy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.controller.CandidateController;
import com.org.bgv.policy.enums.PolicyAudience;
import com.org.bgv.policy.request.PolicyConsentRequest;
import com.org.bgv.policy.request.PolicyConsentResponse;
import com.org.bgv.policy.request.PolicyDetailsResponse;
import com.org.bgv.policy.service.PolicyService;
import com.org.bgv.service.CandidateService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
@Slf4j
public class PolicyController {
	
	private final PolicyService policyService;
	
	
	@PostMapping(value="/consent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CustomApiResponse<PolicyConsentResponse>> acceptPolicy(

	        @ModelAttribute @Valid PolicyConsentRequest request,
	        HttpServletRequest servletRequest) {

	    request.setIpAddress(getClientIpAddress(servletRequest));
	    request.setUserAgent(servletRequest.getHeader("User-Agent"));

	    PolicyConsentResponse response =
	            policyService.acceptPolicy(request);

	    return ResponseEntity.ok(
	            CustomApiResponse.success(
	                    "Policy accepted successfully",
	                    response,
	                    HttpStatus.OK));
	}
	
	@GetMapping("/audience")
	public ResponseEntity<CustomApiResponse<PolicyDetailsResponse>> getPolicies(
	        @RequestParam PolicyAudience audience) {

	    PolicyDetailsResponse response = policyService.getPolicy(audience);

	    return ResponseEntity.ok(
	            CustomApiResponse.success(
	                    "Policies fetched successfully.",
	                    response,
	                    HttpStatus.OK));
	}
	
	
	
	 private String getClientIpAddress(HttpServletRequest request) {
	        String xfHeader = request.getHeader("X-Forwarded-For");
	        if (xfHeader != null && !xfHeader.isEmpty()) {
	            return xfHeader.split(",")[0];
	        }
	        return request.getRemoteAddr();
	    }

}
