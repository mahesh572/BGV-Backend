package com.org.bgv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.WorkExperience;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
	
	List<WorkExperience> findByProfile_ProfileId(Long profileId);
    void deleteByProfile_ProfileId(Long profileId); // Optional - if you want to replace all experiences
    List<WorkExperience> findByExperienceId(Long experienceId);
}
