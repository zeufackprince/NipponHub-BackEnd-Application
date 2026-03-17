package com.nipponhub.nipponhubv0.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nipponhub.nipponhubv0.DTO.ProductDto;
import com.nipponhub.nipponhubv0.Models.Country;
import com.nipponhub.nipponhubv0.Models.Product;

@Component
public class ProductMapper {

    public ProductDto prodToDto(Product product) {
        if (product == null) return null;

        ProductDto dto = new ProductDto();

        // ── Scalar fields — always safe ───────────────────────────────────────
        dto.setIdProd(product.getIdProd());
        dto.setProdName(product.getProdName());
        dto.setUnitPrice(product.getUnitPrice());
        dto.setSoldPrice(product.getSoldPrice());
        dto.setProdQty(product.getProdQty());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setProdDescription(product.getProdDescription());

        // ── Image IDs list — safe, initialized to ArrayList in entity ─────────
        dto.setProdUrl(product.getProdUrl() != null
            ? product.getProdUrl()
            : Collections.emptyList());

        // ── Category — null-safe lazy access ─────────────────────────────────
        if (product.getCategoriesProd() != null) {
            dto.setCategoryId(product.getCategoriesProd().getIdCatProd());
            dto.setCategoryName(product.getCategoriesProd().getCatProdName());
        }

        // ── Franchise — null-safe lazy access ────────────────────────────────
        if (product.getFranchiseProd() != null) {
            dto.setFranchiseId(product.getFranchiseProd().getIdFranchiseProd());
            dto.setFranchiseName(product.getFranchiseProd().getFranchiseProdName());
        }

        // ── Countries — null-safe lazy collection ─────────────────────────────
        if (product.getCountries() != null && !product.getCountries().isEmpty()) {
            List<String> countryNames = product.getCountries()
                .stream()
                .map(Country::getCountryName)
                .collect(Collectors.toList());
            dto.setCountries(countryNames);  // ✅ was setCountryName() — wrong field name
        } else {
            dto.setCountries(Collections.emptyList());
        }

        return dto;
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
        res.setProdDescription(reqProduct.getProdDescription());
        

        return res;
    }

    
}
