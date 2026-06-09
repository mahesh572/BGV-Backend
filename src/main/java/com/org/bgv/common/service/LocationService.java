package com.org.bgv.common.service;

import java.util.List;

import com.org.bgv.commom.dto.CountryDto;
import com.org.bgv.commom.dto.StateRegionDto;
import com.org.bgv.common.entity.Country;
import com.org.bgv.common.entity.StateRegion;

public interface LocationService {

    List<CountryDto> getAllCountries();

    CountryDto getCountry(Long countryId);

    CountryDto getCountryByCode(String countryCode);

    List<StateRegionDto> getStatesByCountry(Long countryId);

    List<StateRegionDto> getStatesByCountryCode(String countryCode);

    StateRegionDto getState(Long stateId);

    StateRegionDto getStateByCode(String countryCode,
                                  String stateCode);
    
    
 // Internal methods (Entities)
    Country findCountryEntity(Long countryId);

    Country findCountryEntityByCode(String countryCode);

    StateRegion findStateEntity(Long stateId);

    StateRegion findStateEntityByCode(String countryCode,
                                      String stateCode);
}
