package com.nipponhub.nipponhubv0.Repositories.mysql;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nipponhub.nipponhubv0.Models.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {

    // Find single country by name
    List<Country> findByCountryName(String countryName);

    // ✅ Add this — finds multiple countries from a list of names
    List<Country> findByCountryNameIn(List<String> countryNames);

    // Optional: find by country code (useful later)
    Optional<Country> findByCountryCode(String countryCode);

    Optional<Country> getByCountryName(String country);

    // List<Country> findByNameIn(List<String> countryNames);
}
