package com.nipponhub.nipponhubv0.Controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nipponhub.nipponhubv0.DTO.ProductDto;
import com.nipponhub.nipponhubv0.Services.ProductServices;

import lombok.Data;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v0/product")
@Data
public class ProductController {
    
    private final ProductServices productServices;

    public ProductDto newProduct(
        @RequestParam(required = false, name = "ProdName") String ProdName,
        @RequestParam(required = false, name = "Unitprice") String Unitprice,
        @RequestParam(required = false, name = "SoldPrice") String SoldPrice,
        @RequestParam(required = false, name = "ProdQty") Integer ProdQty,
        @RequestParam(required = false ,name = "ProdUrl") List<MultipartFile> ProdUrl
    ) throws IOException {

        ProductDto reqProductDto = this.productServices.createProduct(ProdName, Unitprice, SoldPrice, ProdQty, ProdUrl);

        return reqProductDto;
    }


}
