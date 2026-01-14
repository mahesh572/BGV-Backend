package com.org.bgv.global.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.global.entity.UserWorkExperience;
import com.org.bgv.entity.User;


public interface UserWorkExperienceRepository extends JpaRepository<UserWorkExperience, Long> {

	List<UserWorkExperience> findByUser_UserId(Long userId);

	Optional<UserWorkExperience> findById(Long id);
	
	Optional<UserWorkExperience> findByUser_UserIdAndId(Long userId,Long id);
}
