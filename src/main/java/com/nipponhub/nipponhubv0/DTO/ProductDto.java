package com.nipponhub.nipponhubv0.DTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ProductDto {

    private Long IdProd;

    private String ProdName;

    private BigDecimal UnitPrice;

    private BigDecimal SoldPrice;

    private Integer ProdQty;
    
    private String Message = "Success";

    private String countryName;

    private String categorieName;

    private List<String> ProdUrl;

    private Date createdAt;
    
}
