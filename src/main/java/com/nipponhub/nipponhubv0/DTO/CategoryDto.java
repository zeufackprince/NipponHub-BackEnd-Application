package com.nipponhub.nipponhubv0.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a category.
 *
 * Why a DTO instead of two @RequestBody params?
 * Spring's HttpMessageConverter can only read the request body ONCE.
 * Using @RequestBody twice means the second parameter is always null.
 * A single DTO wrapping all fields solves this completely.
 */
@Data
@NoArgsConstructor
public class CategoryDto {

    private Long idProd;
    private String catProdName;
    private String catProdDes;
    private String message;
}
