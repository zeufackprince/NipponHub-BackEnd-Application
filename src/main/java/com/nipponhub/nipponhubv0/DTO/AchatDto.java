package com.nipponhub.nipponhubv0.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AchatDto {

    private Long Id;

    private LocalDate date;

    // private Integer Qte;

    private BigDecimal coutTotal;

    private Integer totalItem;

    private List<AchatItemDetailDto> items;

    private String message;
    
}
