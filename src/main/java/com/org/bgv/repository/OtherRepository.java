package com.org.bgv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.Other;


@Repository
public interface OtherRepository extends JpaRepository<Other, Long> {
    // You can define custom queries here if needed
	List<Other> findByProfile_ProfileId(Long profileId);
}