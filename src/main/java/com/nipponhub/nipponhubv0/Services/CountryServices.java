package com.nipponhub.nipponhubv0.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nipponhub.nipponhubv0.Models.Country;
import com.nipponhub.nipponhubv0.Repositories.CountryRepository;

import lombok.Data;

@Service
@Data
public class CountryServices {

    private final CountryRepository countryRepository;

    public String createCountry(String countryName, String countryCode) {
        String res = "";
        try {
            Country country = new Country();
            country.setCountryName(countryName);
            country.setCountryCode(countryCode);

            this.countryRepository.save(country);
            res = "Country Created Successfully...";
        } catch (Exception e) {
            res = "Error Creating Country..." + e;
        }
        return res;
    }
    
    @SuppressWarnings("null")
    public String deleteCountry(Long idCountry) {
        String res = "";
        try {
            this.countryRepository.deleteById(idCountry);
            res = "Country Deleted Successfully...";
        } catch (Exception e) {
            res = "Error Deleting Country..." + e;
        }
        return res;
    }

    public List<Country> getAllCountries() {
        return this.countryRepository.findAll();
    }
}
