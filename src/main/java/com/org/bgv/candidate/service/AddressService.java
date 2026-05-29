package com.org.bgv.candidate.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.candidate.dto.AddressResponse;
import com.org.bgv.candidate.dto.CandidateActionCatalog;
import com.org.bgv.candidate.dto.VerificationSectionDTO;
import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.repository.AddressRepository;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.constants.SectionStatus;
import com.org.bgv.dto.AddressDTO;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.Address;
import com.org.bgv.entity.AddressType;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.repository.VerificationCaseRepository;
import com.org.bgv.vendor.action.dto.ActionDTO;
import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    
    private final CandidateRepository candidateRepository;
    private final AddressRepository addressRepository;
    private final VerificationCaseRepository verificationCaseRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final VerificationCaseCheckRepository verificationCaseCheckRepository;
    private final VerificationHelperService verificationHelperService;
    
    /*
    public AddressDTO createAddress(AddressDTO addressDTO, Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + candidateId));
        
        Address address = mapToEntity(addressDTO, candidate);
        Address savedAddress = addressRepository.save(address);
        return mapToDTO(savedAddress);
    }
*/
    public AddressResponse getAddressesByCandidate(Long candidateId,Long caseId) {
        List<Address> addresses = addressRepository.findByCandidateIdAndVerificationCaseCaseId(candidateId,caseId);
        
        List<AddressDTO> addressesDto = addresses.stream()
        									.map(this::mapToDTO)
        											.collect(Collectors.toList());
        
        
       
        final String CATEGORY_NAME = CheckCategoryEnum.ADDRESS.getName();

        // Fetch category
        CheckCategory category = checkCategoryRepository
                .findByNameIgnoreCase(CATEGORY_NAME)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Fetch case
        VerificationCase verificationCase = verificationCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Verification case not found"));

        // Fetch case check
        VerificationCaseCheck addressCheck = verificationCaseCheckRepository
                .findByVerificationCaseAndCategory(verificationCase, category)
                .orElseThrow(() -> new RuntimeException("Identity check not found"));
        
        VerificationSectionDTO verificationSectionDTO = verificationHelperService.getSectionStatusByCaseAndCandidate(candidateId,caseId,CATEGORY_NAME);
        
        SectionStatus sectionStatus = SectionStatus.fromString(
                verificationSectionDTO.getStatus().name()
        );
        
        return AddressResponse.builder()
				        .caseId(caseId)
				        .checkId(addressCheck.getCaseCheckId())
				        .categoryId(category.getCategoryId())
				        .addresses(addressesDto)
				        .status(verificationSectionDTO.getStatus().name())
				        .actions(VerificationHelperService.resolveSectionActions(sectionStatus)) 
				        .build();
        
       
    }

    public AddressDTO getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
        return mapToDTO(address);
    }

    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AddressDTO updateAddress(AddressDTO addressDTO, Long candidateId,Long caseId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + candidateId));
        
        
        final String CATEGORY_NAME = CheckCategoryEnum.ADDRESS.getName();

        // Fetch category
        CheckCategory category = checkCategoryRepository
                .findByNameIgnoreCase(CATEGORY_NAME)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Fetch case
        VerificationCase verificationCase = verificationCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Verification case not found"));

        // Fetch case check
        VerificationCaseCheck addressCheck = verificationCaseCheckRepository
                .findByVerificationCaseAndCategory(verificationCase, category)
                .orElseThrow(() -> new RuntimeException("Identity check not found"));
        
        
        
        
        Address existingAddress;
        if (addressDTO.getId() == null || addressDTO.getId() == 0) {
            existingAddress = new Address();
        } else {
            existingAddress = addressRepository.findById(addressDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressDTO.getId()));
        }
        
        // Set basic address fields
        existingAddress.setCandidateId(candidateId);
        existingAddress.setAddressLine1(addressDTO.getAddressLine1());
        existingAddress.setAddressLine2(addressDTO.getAddressLine2());
        existingAddress.setCity(addressDTO.getCity());
        existingAddress.setState(addressDTO.getState());
        existingAddress.setCountry(addressDTO.getCountry());
        existingAddress.setZipCode(addressDTO.getZipCode());
        existingAddress.setDefault(addressDTO.isDefault());
        
        // Set address type
        if (addressDTO.getAddressType() != null) {
            existingAddress.setAddressType(addressDTO.getAddressType());
        }
        
        // Set currently residing fields
        existingAddress.setCurrentlyResidingFrom(addressDTO.getCurrentlyResidingFrom());
        
        // Determine if this is the current residence
        if (addressDTO.getCurrentlyResidingAtThisAddress() != null) {
            existingAddress.setCurrentlyResidingAtThisAddress(addressDTO.getCurrentlyResidingAtThisAddress());
        }
        
        // If this is set as default, unset other defaults for this candidate
        if (addressDTO.isDefault()) {
            List<Address> candidateAddresses = addressRepository.findByCandidateId(candidateId);
            candidateAddresses.forEach(addr -> {
                if (!addr.getId().equals(existingAddress.getId())) {
                    addr.setDefault(false);
                }
            });
            addressRepository.saveAll(candidateAddresses);
        }
        existingAddress.setIsMyPermanentAddress(addressDTO.getIsMyPermanentAddress());
        
        existingAddress.setVerificationCase(verificationCase);

        Address updatedAddress = addressRepository.save(existingAddress);
        return mapToDTO(updatedAddress);
    }
    
    @Transactional
    public List<AddressDTO> updateAddresses(List<AddressDTO> addressDTOs, Long candidateId,Long caseId) {
        List<AddressDTO> updatedAddresses = new ArrayList<>();
        
        for (AddressDTO addressDTO : addressDTOs) {
            AddressDTO updatedAddress = updateAddress(addressDTO, candidateId,caseId);
            updatedAddresses.add(updatedAddress);
        }
        
        return updatedAddresses;
    }

    public void deleteAddress(Long id, Long candidateId) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
        
        // Verify the address belongs to the candidate
        if (!address.getCandidateId().equals(candidateId)) {
            throw new RuntimeException("Address does not belong to the specified candidate");
        }
        
        addressRepository.delete(address);
    }

    @Transactional
    public List<AddressDTO> saveAddresses(List<AddressDTO> addressDTOs, Long candidateId,Long caseId) {
        
    	log.info("Adress service :::::::::::::{}",addressDTOs);
    	
    	Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + candidateId));
    	
    	VerificationCase verificationCase = verificationCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Verification case not found"));

        List<Address> addresses = addressDTOs.stream()
                .map(dto -> mapToEntity(dto, candidate,verificationCase))
                .collect(Collectors.toList());

        List<Address> savedAddresses = addressRepository.saveAll(addresses);
        return savedAddresses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AddressDTO> getCurrentAddress(Long candidateId) {
        return addressRepository.findByCandidateIdAndCurrentlyResidingAtThisAddress(candidateId, true)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AddressDTO> getPermanentAddress(Long candidateId) {
        return addressRepository.findByCandidateIdAndIsMyPermanentAddress(candidateId, true)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AddressDTO> getAddressesByType(Long candidateId, AddressType addressType) {
        return addressRepository.findByCandidateIdAndAddressType(candidateId, addressType.name())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void setCurrentAddress(Long candidateId, Long addressId) {
        // First, unset current address for all addresses of this candidate
        List<Address> candidateAddresses = addressRepository.findByCandidateId(candidateId);
        candidateAddresses.forEach(address -> 
            address.setCurrentlyResidingAtThisAddress(false));
        
        // Set the new current address
        Address newCurrentAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found: " + addressId));
        
        if (!newCurrentAddress.getCandidateId().equals(candidateId)) {
            throw new RuntimeException("Address does not belong to the candidate");
        }
        
        newCurrentAddress.setCurrentlyResidingAtThisAddress(true);
        
        addressRepository.saveAll(candidateAddresses);
        addressRepository.save(newCurrentAddress);
    }

    private Address mapToEntity(AddressDTO dto, Candidate candidate,VerificationCase verificationCase) {
       
    	Address address = new Address();
        address.setCandidateId(candidate.getCandidateId());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setZipCode(dto.getZipCode());
        address.setDefault(dto.isDefault());
        address.setAddressType(dto.getAddressType());
        address.setCurrentlyResidingFrom(dto.getCurrentlyResidingFrom());
        
        // Set default value for currently residing if not provided
        if (dto.getCurrentlyResidingAtThisAddress() != null) {
            address.setCurrentlyResidingAtThisAddress(dto.getCurrentlyResidingAtThisAddress());
        } else {
            address.setCurrentlyResidingAtThisAddress(false);
        }
        
        // Set default values for new fields
        address.setStatus("active");
        address.setVerificationStatus("pending");
        address.setVerified(false);
        address.setIsMyPermanentAddress(dto.getIsMyPermanentAddress()); // This can be set separately
        
        // Calculate duration if currently residing from is set
        if (dto.getCurrentlyResidingFrom() != null && 
            dto.getCurrentlyResidingAtThisAddress() != null && 
            dto.getCurrentlyResidingAtThisAddress()) {
            address.setDurationOfStayMonths(address.calculateDurationOfStay());
        }
        address.setVerificationCase(verificationCase);
        
        
        log.info("address DTO:::::::::::::::::{}",dto);
        return address;
    }

    private AddressDTO mapToDTO(Address entity) {
        AddressDTO dto = new AddressDTO();
        dto.setId(entity.getId());
        dto.setCandidateId(entity.getCandidateId());
        dto.setAddressLine1(entity.getAddressLine1());
        dto.setAddressLine2(entity.getAddressLine2());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setCountry(entity.getCountry());
        dto.setZipCode(entity.getZipCode());
        dto.setDefault(entity.isDefault());
        dto.setAddressType(entity.getAddressType());
        dto.setCurrentlyResidingFrom(entity.getCurrentlyResidingFrom());
        dto.setCurrentlyResidingAtThisAddress(entity.getCurrentlyResidingAtThisAddress());
        dto.setVerified(entity.isVerified());
        dto.setVerificationStatus(entity.getVerificationStatus());
        dto.setIsMyPermanentAddress(entity.getIsMyPermanentAddress());
        
        return dto;
    }

    public void deleteAddressesByCandidateId(Long candidateId) {
        List<Address> addresses = addressRepository.findByCandidateId(candidateId);
        addressRepository.deleteAll(addresses);
    }

    public long countActiveAddresses(Long candidateId) {
        return addressRepository.countActiveAddressesByCandidateId(candidateId);
    }

    public List<AddressDTO> getAddressesNeedingVerification(Long candidateId) {
        return addressRepository.findPendingVerificationAddresses(candidateId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
   
}