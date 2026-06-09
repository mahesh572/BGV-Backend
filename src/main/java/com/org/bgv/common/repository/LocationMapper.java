package com.org.bgv.common.repository;

import org.springframework.stereotype.Component;

import com.org.bgv.commom.dto.CountryDto;
import com.org.bgv.commom.dto.StateRegionDto;
import com.org.bgv.common.entity.Country;
import com.org.bgv.common.entity.StateRegion;

@Component
public class LocationMapper {

    public CountryDto toDto(Country country) {

        return CountryDto.builder()
                .id(country.getId())
                .code(country.getCode())
                .name(country.getName())
              //  .phoneCode(country.getPhoneCode())
                .build();
    }

    public StateRegionDto toDto(StateRegion state) {

        return StateRegionDto.builder()
                .id(state.getId())
                .code(state.getCode())
                .name(state.getName())
                .countryId(state.getCountry().getId())
                .build();
    }
}
