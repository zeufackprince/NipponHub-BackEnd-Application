package com.nipponhub.nipponhubv0.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VenteDto {

    private Long id;

    private LocalDate date;

    private BigDecimal coutTotal;

    private BigDecimal prixVendu;

    private BigDecimal gain;

    private Integer totalItem;

    private BigDecimal prixUnitaire;
 
    private List<VenteItemDetailDto> items;
    
    private String message;

}
