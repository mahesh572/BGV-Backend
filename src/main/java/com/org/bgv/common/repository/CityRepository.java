package com.org.bgv.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.common.entity.City;
import com.org.bgv.common.entity.StateRegion;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByStateIdOrderByName(Long stateId);

    boolean existsByStateAndName(StateRegion state, String name);
    
    Optional<City> findByStateAndCode(StateRegion state, String code);
}
