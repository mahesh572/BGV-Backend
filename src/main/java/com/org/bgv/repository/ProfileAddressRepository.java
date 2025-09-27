package com.org.bgv.repository;

import com.org.bgv.entity.Profile_Address;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileAddressRepository extends JpaRepository<Profile_Address, Long> {
	List<Profile_Address> findByProfile_ProfileId(Long profileId);
	void deleteByProfile_ProfileId(Long profileId); //
}