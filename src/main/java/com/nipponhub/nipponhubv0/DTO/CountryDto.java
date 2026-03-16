package com.nipponhub.nipponhubv0.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CountryDto {
    
    private Long idCountry;
    private String countryName;
    private String countryCode;
    private String message;
}
