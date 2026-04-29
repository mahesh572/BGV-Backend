package com.org.bgv.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.candidate.dto.AddressResponse;
import com.org.bgv.candidate.service.AddressService;
import com.org.bgv.dto.AddressDTO;
import com.org.bgv.dto.ProfileAddressDTO;
import com.org.bgv.service.ProfileAddressService;
import com.org.bgv.service.WorkExperienceService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/candidate/{candidateId}/case/{caseId}/address")
@RequiredArgsConstructor
@Slf4j
public class AddressController {
	
	 private final ProfileAddressService profileAddressService;
	 private final AddressService addressService;

	    @PostMapping
	    public ResponseEntity<CustomApiResponse<List<AddressDTO>>> saveCandidateAddresses(
	            @PathVariable Long candidateId,
	            @PathVariable Long caseId,
	            @RequestBody List<AddressDTO> profileAddressDTOs) {
		    log.info("AddressController::::::::::::::::::::::::::::{}",profileAddressDTOs);
	        try {
	            List<AddressDTO> savedAddresses = addressService.saveAddresses(profileAddressDTOs, candidateId,caseId);
	            return ResponseEntity.status(HttpStatus.CREATED)
	                    .body(CustomApiResponse.success("Addresses saved successfully", savedAddresses, HttpStatus.CREATED));
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(CustomApiResponse.failure("Failed to save addresses: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
	        }
	    }

	    @GetMapping
	    public ResponseEntity<CustomApiResponse<?>> getCandidateAddresses(@PathVariable Long candidateId,@PathVariable Long caseId) {
	        try {
	        	AddressResponse addresses = addressService.getAddressesByCandidate(candidateId,caseId);
	            return ResponseEntity.ok(CustomApiResponse.success("Addresses retrieved successfully", addresses, HttpStatus.OK));
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(CustomApiResponse.failure("Failed to retrieve addresses: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
	        }
	    }

	    
	    @PutMapping
	    public ResponseEntity<CustomApiResponse<List<AddressDTO>>> updateCandidateAddresses(
	            @PathVariable Long candidateId,
	            @PathVariable Long caseId,
	            @RequestBody List<AddressDTO> profileAddressDTOs) {
	    	 log.info("AddressController::::::updateProfileAddresses::::::::::::::::::::::{}",profileAddressDTOs);
	        try {
	            List<AddressDTO> updatedAddresses = addressService.updateAddresses(profileAddressDTOs, candidateId,caseId);
	            return ResponseEntity.ok()
	                    .body(CustomApiResponse.success("Addresses updated successfully", updatedAddresses, HttpStatus.OK));
	        } catch (EntityNotFoundException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
	        } catch (Exception e) {
	           // log.error("Error updating addresses for profileId: {}", profileId, e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(CustomApiResponse.failure("Failed to update addresses", HttpStatus.INTERNAL_SERVER_ERROR));
	        }
	    }
	    
	    @DeleteMapping("/{addressId}")
	    public ResponseEntity<CustomApiResponse<String>> deleteProfileAddress(
	            @PathVariable Long candidateId,
	            @PathVariable Long addressId) {
	        try {
	        	addressService.deleteAddress(addressId, candidateId);
	            return ResponseEntity.ok()
	                    .body(CustomApiResponse.success("Address deleted successfully", "Address with ID: " + addressId + " has been deleted", HttpStatus.OK));
	        } catch (EntityNotFoundException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
	        } catch (Exception e) {
	            // log.error("Error deleting address with ID: {} for profileId: {}", addressId, profileId, e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(CustomApiResponse.failure("Failed to delete address", HttpStatus.INTERNAL_SERVER_ERROR));
	        }
	    }
	
}
