package com.org.bgv.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.org.bgv.commom.dto.CountryDto;
import com.org.bgv.commom.dto.StateRegionDto;
import com.org.bgv.common.entity.Country;
import com.org.bgv.common.entity.StateRegion;
import com.org.bgv.common.repository.CountryRepository;
import com.org.bgv.common.repository.LocationMapper;
import com.org.bgv.common.repository.StateRegionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {

    private final CountryRepository countryRepository;
    private final StateRegionRepository stateRegionRepository;
    private final LocationMapper mapper;

    @Override
    public List<CountryDto> getAllCountries() {

        return countryRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public CountryDto getCountry(Long countryId) {

        return mapper.toDto(
                findCountryEntity(countryId));
    }

    @Override
    public CountryDto getCountryByCode(String countryCode) {

        return mapper.toDto(
                findCountryEntityByCode(countryCode));
    }

    @Override
    public List<StateRegionDto> getStatesByCountry(Long countryId) {

        return stateRegionRepository.findByCountryId(countryId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<StateRegionDto> getStatesByCountryCode(String countryCode) {

        return stateRegionRepository.findByCountryCode(countryCode)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public StateRegionDto getState(Long stateId) {

        return mapper.toDto(
                findStateEntity(stateId));
    }

    @Override
    public StateRegionDto getStateByCode(String countryCode,
                                         String stateCode) {

        return mapper.toDto(
                findStateEntityByCode(countryCode, stateCode));
    }

    // ========= Internal Entity Methods =========

    @Override
    public Country findCountryEntity(Long countryId) {

        return countryRepository.findById(countryId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Country not found with id : " + countryId));
    }

    @Override
    public Country findCountryEntityByCode(String countryCode) {

        return countryRepository.findByCode(countryCode)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Country not found with code : " + countryCode));
    }

    @Override
    public StateRegion findStateEntity(Long stateId) {

        return stateRegionRepository.findById(stateId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "State not found with id : " + stateId));
    }

    @Override
    public StateRegion findStateEntityByCode(String countryCode,
                                             String stateCode) {

        return stateRegionRepository
                .findByCountryCodeAndCode(countryCode, stateCode)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "State not found"));
    }
}
