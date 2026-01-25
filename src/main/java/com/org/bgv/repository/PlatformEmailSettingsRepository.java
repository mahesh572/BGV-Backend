package com.org.bgv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.PlatformEmailSettings;


@Repository
public interface PlatformEmailSettingsRepository
        extends JpaRepository<PlatformEmailSettings, Long> {

    @Query("SELECT p FROM PlatformEmailSettings p WHERE p.active = true")
    Optional<PlatformEmailSettings> findActive();
}
