package com.nipponhub.nipponhubv0.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nipponhub.nipponhubv0.DTO.CountryDto;
import com.nipponhub.nipponhubv0.Models.Country;
import com.nipponhub.nipponhubv0.Repositories.mysql.CountryRepository;

import lombok.Data;

@Service
@Data
public class CountryServices {

    private final CountryRepository countryRepository;

    public CountryDto createCountry(CountryDto countryDto) {
        CountryDto res = new CountryDto();
        try {
            Country country = new Country();
            country.setCountryName(countryDto.getCountryName());
            country.setCountryCode(countryDto.getCountryCode());

            Country savedCountry = this.countryRepository.save(country);
            res.setIdCountry(savedCountry.getIdCountry());
            res.setCountryName(savedCountry.getCountryName());
            res.setCountryCode(savedCountry.getCountryCode());

            res.setMessage("Country Created Successfully...");
        } catch (Exception e) {
            res.setMessage("Error Creating Country..." + e);
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
