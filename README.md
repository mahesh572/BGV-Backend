UserServiceUtil.java
-------------------------

private final CompanyRepository companyRepository;
	private final CompanyUserRepository companyUserRepository;

public List<UserDto> getVendorAgentUsersToAssign(Long companyId) {

		    Company company = companyRepository.findById(companyId)
		            .orElseThrow(() ->
		                    new ResourceNotFoundException(
		                            "Company not found with id: " + companyId));

		    List<User> vendorAgentUsersList =
		            companyUserRepository.findUsersByCompanyIdAndRole(
		                    companyId, RoleConstants.ROLE_VENDOR_AGENT);

		    return vendorAgentUsersList.stream()
		            .map(userMapper::toDto)
		            .toList(); 
		}
		
VendorUserService.java
--------------------------------		
		
		
		public List<UserDto> getVendorAgentUsersToAssign(Long companyId){
    	
    	return userServiceUtil.getVendorAgentUsersToAssign(companyId);
    }
		
VendorActionController.java
-------------------------------------------

 private final VendorUserService vendorUserService;
		
		 @GetMapping("/{companyId}/vendor-agents")
    public ResponseEntity<CustomApiResponse<?>> getVendorAgentUsers(
            @PathVariable Long companyId) {

        try {

            log.info("GET_VENDOR_AGENTS | companyId={}", companyId);

            List<UserDto> vendorAgents =
            		vendorUserService.getVendorAgentUsersToAssign(companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Vendor agent users fetched successfully",
                            vendorAgents,
                            HttpStatus.OK
                    )
            );

        } catch (ResourceNotFoundException e) {

            log.error("Company not found | companyId={}", companyId, e);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                            CustomApiResponse.failure(
                                    e.getMessage(),
                                    HttpStatus.NOT_FOUND
                            )
                    );

        } catch (Exception e) {

            log.error("Failed to fetch vendor agents | companyId={}", companyId, e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            CustomApiResponse.failure(
                                    "Failed to fetch vendor agents: " + e.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR
                            )
                    );
        }
    }
	
	
	
	 @PutMapping("/assign-vendor-agent")
    public ResponseEntity<CustomApiResponse<?>> assignVendorAgent(
            @RequestParam Long companyId,
            @RequestParam Long checkId,
            @RequestParam Long vendorAgentUserId) {

        try {

            log.info("ASSIGN_VENDOR_AGENT | companyId={} | checkId={} | vendorAgentUserId={}",
                    companyId, checkId, vendorAgentUserId);

            vendorActionService.assignVendorAgent(
                    companyId, checkId, vendorAgentUserId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Vendor agent assigned successfully",
                            null,
                            HttpStatus.OK
                    )
            );

        } catch (ResourceNotFoundException e) {

            log.error("Resource not found | companyId={} | checkId={} | vendorAgentUserId={}",
                    companyId, checkId, vendorAgentUserId, e);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.NOT_FOUND
                    ));

        } catch (IllegalStateException e) {

            log.warn("Assignment validation failed | {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (Exception e) {

            log.error("Failed to assign vendor agent", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to assign vendor agent: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    


package com.org.bgv.vendor.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.org.bgv.entity.User;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.exceptions.ResourceNotFoundException;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.VerificationCaseCheckRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorActionService {
	
	

   private final VerificationCaseCheckRepository verificationCaseCheckRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;


    

    public void assignVendorAgent(Long companyId,
                                  Long checkId,
                                  Long vendorAgentUserId) {

        // ✅ Fetch Company
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Company not found with id: " + companyId));

        // ✅ Fetch Case Check
        VerificationCaseCheck caseCheck =
                verificationCaseCheckRepository.findById(checkId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Verification check not found with id: " + checkId));

        // ✅ Fetch Vendor Agent User
        User vendorUser = userRepository.findById(vendorAgentUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + vendorAgentUserId));

        // ✅ Validate user belongs to same company (optional but recommended)
        if (caseCheck.getVendorCompany() != null &&
                !caseCheck.getVendorCompany().getId().equals(companyId)) {

            throw new IllegalStateException("Vendor company mismatch for this check");
        }

        // ✅ Assign values
        caseCheck.setVendorCompany(company);
        caseCheck.setAssignedVendorUser(vendorUser);
        caseCheck.setAssignedAt(LocalDateTime.now());

        // ✅ Save
        verificationCaseCheckRepository.save(caseCheck);
    }

    
}
