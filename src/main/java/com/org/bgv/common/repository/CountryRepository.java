package com.org.bgv.common.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.common.entity.Country;


@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    boolean existsByCode(String code);

    Optional<Country> findByCode(String code);

    Optional<Country> findByName(String name);

}
