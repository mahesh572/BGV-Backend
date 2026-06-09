package com.org.bgv.common.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.common.entity.Country;
import com.org.bgv.common.entity.StateRegion;


@Repository
public interface StateRegionRepository extends JpaRepository<StateRegion, Long> {

    List<StateRegion> findByCountry(Country country);

    List<StateRegion> findByCountryId(Long countryId);

    List<StateRegion> findByCountryCode(String countryCode);

    Optional<StateRegion> findByCode(String code);

    Optional<StateRegion> findByName(String name);

    Optional<StateRegion> findByCountryCodeAndCode(
            String countryCode,
            String code
    );

    boolean existsByCountryCodeAndCode(
            String countryCode,
            String code
    );
}
