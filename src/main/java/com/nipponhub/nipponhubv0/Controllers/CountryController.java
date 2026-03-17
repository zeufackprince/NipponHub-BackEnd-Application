package com.nipponhub.nipponhubv0.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nipponhub.nipponhubv0.DTO.CountryDto;
import com.nipponhub.nipponhubv0.Models.Country;
import com.nipponhub.nipponhubv0.Services.CountryServices;

import lombok.Data;


@Data
@RestController
@RequestMapping("/api/v0/country")
public class CountryController {


    private final CountryServices countryServices;

    @PostMapping("/newCountry")
    public ResponseEntity<CountryDto> createCountry(@RequestBody CountryDto countryDto) {
        CountryDto res = this.countryServices.createCountry(countryDto);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/deleteCountry/{idCountry}")
    public ResponseEntity<String> deleteCountry(@RequestParam Long idCountry) {
        String res = this.countryServices.deleteCountry(idCountry);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/getAllCountries")
    public ResponseEntity<List<Country>> getAllCountries() {
        List<Country> res = this.countryServices.getAllCountries();
        return ResponseEntity.ok(res);
    }
    
}
