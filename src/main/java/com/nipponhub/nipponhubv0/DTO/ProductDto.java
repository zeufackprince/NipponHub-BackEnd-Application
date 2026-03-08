package com.nipponhub.nipponhubv0.DTO;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ProductDto {

    private Long IdProd;

    private String ProdName;

    private String Unitprice;

    private String SoldPrice;

    private Integer ProdQty;
    
    private List<String> ProdUrl;

    private Date createdAt;
    
}
