package com.org.bgv.data.seed;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.org.bgv.common.entity.Country;
import com.org.bgv.common.entity.StateRegion;
import com.org.bgv.common.repository.CountryRepository;
import com.org.bgv.common.repository.StateRegionRepository;

import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
public class LocationSeeder {

    private final CountryRepository countryRepository;
    private final StateRegionRepository stateRegionRepository;

    @Bean
    CommandLineRunner seedLocations() {
        return args -> {

            // Skip if India already exists
            if (countryRepository.existsByCode("IN")) {
                return;
            }

            Country india = new Country();
            india.setCode("IN");
            india.setName("India");

            countryRepository.save(india);

            // States
            saveState(india, "AP", "Andhra Pradesh");
            saveState(india, "AR", "Arunachal Pradesh");
            saveState(india, "AS", "Assam");
            saveState(india, "BR", "Bihar");
            saveState(india, "CG", "Chhattisgarh");
            saveState(india, "GA", "Goa");
            saveState(india, "GJ", "Gujarat");
            saveState(india, "HR", "Haryana");
            saveState(india, "HP", "Himachal Pradesh");
            saveState(india, "JH", "Jharkhand");
            saveState(india, "KA", "Karnataka");
            saveState(india, "KL", "Kerala");
            saveState(india, "MP", "Madhya Pradesh");
            saveState(india, "MH", "Maharashtra");
            saveState(india, "MN", "Manipur");
            saveState(india, "ML", "Meghalaya");
            saveState(india, "MZ", "Mizoram");
            saveState(india, "NL", "Nagaland");
            saveState(india, "OD", "Odisha");
            saveState(india, "PB", "Punjab");
            saveState(india, "RJ", "Rajasthan");
            saveState(india, "SK", "Sikkim");
            saveState(india, "TN", "Tamil Nadu");
            saveState(india, "TG", "Telangana");
            saveState(india, "TR", "Tripura");
            saveState(india, "UP", "Uttar Pradesh");
            saveState(india, "UK", "Uttarakhand");
            saveState(india, "WB", "West Bengal");

            // Union Territories
            saveState(india, "AN", "Andaman and Nicobar Islands");
            saveState(india, "CH", "Chandigarh");
            saveState(india, "DH", "Dadra and Nagar Haveli and Daman and Diu");
            saveState(india, "DL", "Delhi");
            saveState(india, "JK", "Jammu and Kashmir");
            saveState(india, "LA", "Ladakh");
            saveState(india, "LD", "Lakshadweep");
            saveState(india, "PY", "Puducherry");

            System.out.println("India states and union territories seeded successfully.");
        };
    }

    private void saveState(Country country, String code, String name) {

        StateRegion state = new StateRegion();
        state.setCountry(country);
        state.setCode(code);
        state.setName(name);

        stateRegionRepository.save(state);
    }
}