package com.org.bgv.global.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.entity.WorkExperience;
import com.org.bgv.dto.WorkExperienceDTO;
import com.org.bgv.entity.User;
import com.org.bgv.global.entity.UserWorkExperience;
import com.org.bgv.global.repository.UserWorkExperienceRepository;
import com.org.bgv.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserWorkExperienceService {

	private final UserWorkExperienceRepository userWorkExperienceRepository;
	private final UserRepository userRepository;

	public List<WorkExperienceDTO> getUserWorkExperiences(Long userId) {

		List<UserWorkExperience> experiences;

		experiences = userWorkExperienceRepository.findByUser_UserId(userId);

		return experiences.stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	@Transactional
	public List<WorkExperienceDTO> saveUserWorkExperiences(Long userId, List<WorkExperienceDTO> workExperienceDTOs

	) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found: " + userId));

		List<UserWorkExperience> entities = workExperienceDTOs.stream().map(dto -> mapToEntity(dto, user))
				.collect(Collectors.toList());

		List<UserWorkExperience> saved = userWorkExperienceRepository.saveAll(entities);

		return saved.stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	private UserWorkExperience mapToEntity(WorkExperienceDTO dto, User user) {

		return UserWorkExperience.builder().user(user)
				// .profile(optional if you use profiles)

				.companyName(dto.getCompanyName()).position(dto.getPosition())

				.startDate(dto.getStartDate()).endDate(dto.getEndDate()).currentlyWorking(dto.getCurrentlyWorking())

				.employmentType(dto.getEmploymentType()).noticePeriod(dto.getNoticePeriod())

				.reason(dto.getReasonForLeaving()).employeeId(dto.getEmployeeId())

				.managerEmailId(dto.getManagerEmail()).hrEmailId(dto.getHrEmail())

				.address(dto.getCompanyAddress()).city(dto.getCity()).state(dto.getState()).country(dto.getCountry())

				.build();
	}

	private WorkExperienceDTO mapToDTO(UserWorkExperience entity) {

		return WorkExperienceDTO.builder().id(entity.getId())

				.companyName(entity.getCompanyName()).position(entity.getPosition())

				.startDate(entity.getStartDate()).endDate(entity.getEndDate())
				.currentlyWorking(entity.getCurrentlyWorking())

				.employmentType(entity.getEmploymentType()).noticePeriod(entity.getNoticePeriod())

				.reasonForLeaving(entity.getReason()).employeeId(entity.getEmployeeId())

				.managerEmail(entity.getManagerEmailId()).hrEmail(entity.getHrEmailId())

				.companyAddress(entity.getAddress()).city(entity.getCity()).state(entity.getState())
				.country(entity.getCountry())

				.durationInMonths(entity.getDurationInMonths()).durationInYears(entity.getDurationInYears())

				.build();
	}

	@Transactional
	public List<WorkExperienceDTO> updateUserWorkExperiences(Long userId,List<WorkExperienceDTO> workExperienceDTOs) {

		if (workExperienceDTOs == null || workExperienceDTOs.isEmpty()) {
			throw new IllegalArgumentException("Work experiences list cannot be empty");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found: " + userId));

		List<WorkExperienceDTO> result = new ArrayList();

		for (WorkExperienceDTO dto : workExperienceDTOs) {

			UserWorkExperience experience;

			/*
			 * =============================== UPDATE ===============================
			 */
			if (dto.getId() != null) {

				experience = userWorkExperienceRepository.findByUser_UserIdAndId(userId, dto.getId())
						.orElseThrow(() -> new RuntimeException("Work experience not found for user"));

			}
			/*
			 * =============================== CREATE ===============================
			 */
			else {
				experience = new UserWorkExperience();
				experience.setUser(user);
			}

			/*
			 * =============================== MAP FIELDS ===============================
			 */
			experience.setCompanyName(dto.getCompanyName());
			experience.setEmployeeId(dto.getEmployeeId());
			experience.setPosition(dto.getPosition());
			experience.setStartDate(dto.getStartDate());
			experience.setEndDate(dto.getEndDate());
			experience.setReason(dto.getReasonForLeaving());
			experience.setHrEmailId(dto.getHrEmail());
			experience.setManagerEmailId(dto.getManagerEmail());
			experience.setAddress(dto.getCompanyAddress());
			experience.setEmploymentType(dto.getEmploymentType());
			experience.setCurrentlyWorking(dto.getCurrentlyWorking());
			experience.setNoticePeriod(dto.getNoticePeriod());
			experience.setCity(dto.getCity());
			experience.setState(dto.getState());
			experience.setCountry(dto.getCountry());

			UserWorkExperience saved = userWorkExperienceRepository.save(experience);

			result.add(mapToDTO(saved));
		}

		return result;
	}

	public void deleteWorkExperience(Long userId, Long experienceId) {
		userWorkExperienceRepository.findByUser_UserIdAndId(userId, experienceId)
				.ifPresentOrElse(userWorkExperienceRepository::delete, () -> {
					throw new EntityNotFoundException("Work experience not found for profileId: " + userId
							+ " and experienceId: " + experienceId);
				});
	}

}
