package com.nipponhub.nipponhubv0.Mappers;

import org.springframework.stereotype.Service;

import com.nipponhub.nipponhubv0.DTO.ProductDto;
import com.nipponhub.nipponhubv0.Models.Product;

@Service
public class ProductMapper {

    public ProductDto prodToDto (Product reqProduct) {

        ProductDto res = new ProductDto();

        res.setIdProd(reqProduct.getIdProd());
        res.setCreatedAt(reqProduct.getCreatedAt());
        res.setProdUrl(reqProduct.getProdUrl());
        res.setProdName(reqProduct.getProdName());
        res.setProdQty(reqProduct.getProdQty());
        res.setSoldPrice(reqProduct.getSoldPrice());
        res.setUnitPrice(reqProduct.getUnitPrice());
        

        return res;
    }

    public Product prodToDto (ProductDto reqProduct) {

        Product res = new Product();

        res.setIdProd(reqProduct.getIdProd());
        res.setCreatedAt(reqProduct.getCreatedAt());
        res.setProdUrl(reqProduct.getProdUrl());
        res.setProdName(reqProduct.getProdName());
        res.setProdQty(reqProduct.getProdQty());
        res.setSoldPrice(reqProduct.getSoldPrice());
        res.setUnitPrice(reqProduct.getUnitPrice());
        

        return res;
    }

    
}
