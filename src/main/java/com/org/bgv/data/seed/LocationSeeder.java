package com.org.bgv.data.seed;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.org.bgv.common.entity.City;
import com.org.bgv.common.entity.Country;
import com.org.bgv.common.entity.StateRegion;
import com.org.bgv.common.repository.CityRepository;
import com.org.bgv.common.repository.CountryRepository;
import com.org.bgv.common.repository.StateRegionRepository;

import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
public class LocationSeeder {
	
	    @Value("${app.seed.enabled:false}")
	    private boolean seedEnabled;

    private final CountryRepository countryRepository;
    private final StateRegionRepository stateRegionRepository;
    private final CityRepository cityRepository;

    @Bean
    CommandLineRunner seedLocations() {
        return args -> {

           

        	Country india = saveCountry("IN", "India");

            // States
            StateRegion ap = saveState(india, "AP", "Andhra Pradesh");
            
            saveCities(ap,
                    "Visakhapatnam",
                    "Vijayawada",
                    "Guntur",
                    "Tirupati",
                    "Kakinada",
                    "Rajahmundry",
                    "Nellore",
                    "Kurnool",
                    "Anantapur",
                    "Kadapa");
            
            
            saveState(india, "AR", "Arunachal Pradesh");
            saveState(india, "AS", "Assam");
            saveState(india, "BR", "Bihar");
            saveState(india, "CG", "Chhattisgarh");
            saveState(india, "GA", "Goa");
            saveState(india, "GJ", "Gujarat");
            saveState(india, "HR", "Haryana");
            saveState(india, "HP", "Himachal Pradesh");
            saveState(india, "JH", "Jharkhand");
            StateRegion ka = saveState(india, "KA", "Karnataka");
            saveCities(ka,
                    "Bengaluru",
                    "Mysuru",
                    "Mangaluru",
                    "Hubballi",
                    "Belagavi",
                    "Ballari",
                    "Davanagere");
            saveState(india, "KL", "Kerala");
            saveState(india, "MP", "Madhya Pradesh");
            StateRegion mh =  saveState(india, "MH", "Maharashtra");
            saveCities(mh,
                    "Mumbai",
                    "Pune",
                    "Nagpur",
                    "Nashik",
                    "Thane",
                    "Aurangabad",
                    "Kolhapur");
            saveState(india, "MN", "Manipur");
            saveState(india, "ML", "Meghalaya");
            saveState(india, "MZ", "Mizoram");
            saveState(india, "NL", "Nagaland");
            saveState(india, "OD", "Odisha");
            saveState(india, "PB", "Punjab");
            saveState(india, "RJ", "Rajasthan");
            saveState(india, "SK", "Sikkim");
            StateRegion tn =saveState(india, "TN", "Tamil Nadu");
            saveCities(tn,
                    "Chennai",
                    "Coimbatore",
                    "Madurai",
                    "Salem",
                    "Tiruchirappalli",
                    "Tirunelveli");
            StateRegion tg = saveState(india, "TG", "Telangana");
            saveCities(tg,
                    "Hyderabad",
                    "Warangal",
                    "Karimnagar",
                    "Khammam",
                    "Nizamabad",
                    "Mahabubnagar",
                    "Adilabad",
                    "Siddipet");
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

    private StateRegion saveState(Country country, String code, String name) {

    	 StateRegion state = stateRegionRepository
    	            .findByCountryAndCode(country, code)
    	            .orElseGet(StateRegion::new);

    	    state.setCountry(country);
    	    state.setCode(code);
    	    state.setName(name);

    	    return stateRegionRepository.save(state);
    }
    
    private void saveCity(StateRegion state, String code, String name) {

    	 City city = cityRepository
    	            .findByStateAndCode(state, code)
    	            .orElseGet(City::new);

    	    city.setState(state);
    	    city.setCode(code);
    	    city.setName(name);

    	    cityRepository.save(city);
    }
    
    
    
    private void saveCities(StateRegion state, String... cities) {

        for (String cityName : cities) {

        	String code = cityName.substring(0, Math.min(3, cityName.length()))
                    .toUpperCase();

            saveCity(state, code, cityName);
        }
    }
    
    private Country saveCountry(String code, String name) {

        Country country = countryRepository
                .findByCode(code)
                .orElseGet(Country::new);

        country.setCode(code);
        country.setName(name);

        return countryRepository.save(country);
    }
}