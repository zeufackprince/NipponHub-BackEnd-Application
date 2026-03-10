package com.nipponhub.nipponhubv0.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nipponhub.nipponhubv0.Models.Country;
import com.nipponhub.nipponhubv0.Services.CountryServices;

import lombok.Data;


@Data
@RestController
@RequestMapping("/api/v0/country")
public class CountryController {


    private final CountryServices countryServices;

    @PostMapping("/newCountry")
    public String createCountry(String countryName, String countryCode) {
        return this.countryServices.createCountry(countryName, countryCode);
    }

    @PostMapping("/deleteCountry")
    public String deleteCountry(Long idCountry) {
        return this.countryServices.deleteCountry(idCountry);
    }

    @PostMapping("/getAllCountries")
    public List<Country> getAllCountries() {
        return this.countryServices.getAllCountries();
    }
    
}
