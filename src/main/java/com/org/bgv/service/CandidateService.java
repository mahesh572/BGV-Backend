package com.org.bgv.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.org.bgv.common.CandidateDTO;
import com.org.bgv.common.RoleConstants;
import com.org.bgv.constants.Constants;
import com.org.bgv.controller.CompanyController;
import com.org.bgv.entity.Candidate;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.CompanyUser;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.repository.CandidateRepository;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.CompanyUserRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CandidateService {
	
	private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyUserRepository companyUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final CandidateRepository candidateRepository;
    
    private static final Logger log = LoggerFactory.getLogger(CandidateService.class);
    
	public Boolean addCandidate(CandidateDTO candidateDTO) {

		// source type - university,individual,company
		try {
			User user = User.builder().firstName(candidateDTO.getFirstName()).lastName(candidateDTO.getLastName())
					.phoneNumber(candidateDTO.getMobileNo()).email(candidateDTO.getEmail())
					.gender(candidateDTO.getGender())
					// .password(UUID.randomUUID().toString())
					.password(passwordEncoder.encode("123456")).userType(Constants.USER_TYPE_CANDIDATE)
					.status(Constants.CANDIDATE_STATUS_CREATED).build();

			// Save user first (if User is a new entity)
			userRepository.save(user);

			// Find Role
			Role companyRole = roleRepository.findByName(RoleConstants.ROLE_CANDIDATE)
					.orElseThrow(() -> new RuntimeException(RoleConstants.ROLE_CANDIDATE + " not found"));

			// Create UserRole mapping
			UserRole userRole = UserRole.builder().user(user).role(companyRole).build();

			userRoleRepository.save(userRole);

			// Find Company
			Company company = companyRepository.findById(candidateDTO.getCompanyId()).orElseThrow(
					() -> new RuntimeException("Company not found with ID: " + candidateDTO.getCompanyId()));

			// Create CompanyUser mapping
			CompanyUser companyUser = new CompanyUser();
			companyUser.setCompany(company);
			companyUser.setUser(user);

			companyUserRepository.save(companyUser);
			
			

	        Profile profile =  Profile.builder()
            //  .profileId(dto.getBasicDetails().getProfileId())
              .firstName(candidateDTO.getFirstName())
              .lastName(candidateDTO.getLastName())
              .emailAddress(candidateDTO.getEmail())
              .phoneNumber(candidateDTO.getMobileNo())
             // .dateOfBirth(employeeDTO.getDateOfBirth())
              .gender(candidateDTO.getGender())
             // .userId(dto.getUser_id())
              .user(user)
              .status(Constants.CANDIDATE_STATUS_CREATED)
              .build();
			
	        profileRepository.save(profile);
	        
	        Candidate candidate = Candidate.builder()
	        .company(company)
	        .createdAt(LocalDateTime.now())
	        .isActive(Boolean.TRUE)
	        .isVerified(Boolean.FALSE)
	        .sourceType(Constants.CANDIDATE_SOURCE_EMPLOYER)
	        .verificationStatus(Constants.CANDIDATE_STATUS_CREATED)
	        .user(user)
	        .build();  
	        
	        candidateRepository.save(candidate);
			
			return Boolean.TRUE;
		} catch (Exception e) {
			log.error("Exception while creating Candidate::::::{}", e.getMessage());
		}
		return Boolean.FALSE;
	}

}
