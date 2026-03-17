package com.nipponhub.nipponhubv0.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Product responses.
 *
 * @JsonInclude(NON_NULL) ensures null fields are omitted from JSON output,
 * keeping API responses clean — especially for error cases where only
 * "message" is set.
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {

    private Long IdProd;

    private String ProdName;

    private BigDecimal UnitPrice;

    private BigDecimal SoldPrice;

    private Integer ProdQty;
    
    private String Message = "Success";

    private String countryName;

    private String categoryName;

    private Long categoryId;

    private String franchiseName;

    private String prodDescription;

    private Long franchiseId;

    private List<String> countries;

    private List<String> ProdUrl;

    private LocalDateTime createdAt;


}
