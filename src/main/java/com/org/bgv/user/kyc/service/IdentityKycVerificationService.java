package com.org.bgv.user.kyc.service;

import org.springframework.stereotype.Service;

import com.org.bgv.constants.DocumentType;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.user.kyc.entity.UserKycVerification;
import com.org.bgv.user.kyc.repository.UserKycVerificationRepository;
import com.org.bgv.user.kyc.response.UserKycVerificationResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdentityKycVerificationService {
	
	private final UserKycVerificationRepository userKycVerificationRepository;
	private final UserKycVerificationMapper mapper;
	
	public UserKycVerificationResponse getStatus(
	        Long userId,
	        DocumentType documentType) {

	    UserKycVerification verification =
	    		userKycVerificationRepository.findByUserUserIdAndDocumentType(userId, documentType)
	                    .orElseThrow(() ->
	                            new BusinessException("Verification record not found"));

	    return mapper.toResponse(verification);
	}

}
