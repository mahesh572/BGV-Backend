package com.org.bgv.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.common.entity.City;
import com.org.bgv.common.entity.StateRegion;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByStateIdOrderByName(Long stateId);

    boolean existsByStateAndName(StateRegion state, String name);
}
