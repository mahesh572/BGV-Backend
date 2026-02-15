package com.org.bgv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.PlatformConfig;

@Repository
public interface PlatformConfigRepository extends JpaRepository<PlatformConfig, Long> {

    // Always fetch the single platform config (id = 1)
}