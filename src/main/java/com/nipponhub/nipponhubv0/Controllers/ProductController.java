package com.nipponhub.nipponhubv0.Controllers;

import com.nipponhub.nipponhubv0.DTO.ProductDto;
import com.nipponhub.nipponhubv0.Services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v0/product")
@RequiredArgsConstructor  
public class ProductController {

    private final ProductService productServices;

    // ─── CREATE ──────────────────────────────────────────────────────────────────

    /**
     * POST /api/v0/product/newProduct
     *
     * FIX: Returns ResponseEntity<ProductDto> with HTTP 201 CREATED instead of raw DTO.
     * FIX: Added throws IOException — no longer silently swallowed.
     * FIX: Removed unnecessary null initialization of reqProductDto.
     * FIX: Added error handling for IllegalArgumentException (validation errors).
     */
    @PostMapping("/newProduct")
    public ResponseEntity<?> newProduct(
        @RequestParam(required = false, name = "ProdName")    String ProdName,
        @RequestParam(required = false, name = "UnitPrice")   BigDecimal UnitPrice,
        @RequestParam(required = false, name = "SoldPrice")   BigDecimal SoldPrice,
        @RequestParam(required = false, name = "ProdQty")     Integer ProdQty,
        @RequestParam(required = false, name = "ProdUrl")     List<MultipartFile> ProdUrl,
        @RequestParam(required = false, name = "Country")     List<String> Country,
        @RequestParam(required = false, name = "category")    String category,
        @RequestParam(required = false, name = "prodDescription")    String prodDescription
    ) throws IOException {

        try {
            ProductDto result = productServices.createProduct(
                ProdName, UnitPrice, SoldPrice, ProdQty, ProdUrl, Country, category, prodDescription
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────────

    /**
     * POST /api/v0/product/updateProduct/{idProd}
     *
     * FIX: Added throws IOException — previously swallowed file upload errors silently.
     * FIX: Removed unnecessary null initialization of reqProductDto.
     * FIX: Returns ResponseEntity with proper HTTP status.
     */
    @PutMapping("/updateProduct/{idProd}")
    public ResponseEntity<?> updateProduct(
        @PathVariable Long idProd,
        @RequestParam(required = false, name = "ProdName")    String ProdName,
        @RequestParam(required = false, name = "UnitPrice")   BigDecimal UnitPrice,
        @RequestParam(required = false, name = "SoldPrice")   BigDecimal SoldPrice,
        @RequestParam(required = false, name = "ProdQty")     Integer ProdQty,
        @RequestParam(required = false, name = "ProdUrl")     List<MultipartFile> ProdUrl,
        @RequestParam(required = false, name = "Country")     List<String> Country,
        @RequestParam(required = false, name = "category")    String category,
        @RequestParam(required = false, name = "prodDescription")    String prodDescription

    ) throws IOException {

        ProductDto result = productServices.updateProduct(
            idProd, ProdName, UnitPrice, SoldPrice, ProdQty, ProdUrl, Country, category, prodDescription
        );

        // If service set a message, something went wrong
        if (result.getMessage() != null) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
        }

        return ResponseEntity.ok(result);
    }

    // ─── READ ────────────────────────────────────────────────────────────────────

    /**
     * GET /api/v0/product/all
     * Returns all products.
     */
    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productServices.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/v0/product/{idProd}
     * Returns a product by its MySQL id.
     */
    @GetMapping("/{idProd}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long idProd) {
        ProductDto product = productServices.getProductById(idProd);

        if (product.getMessage() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(product);
        }
        return ResponseEntity.ok(product);
    }

    /**
     * GET /api/v0/product/search?prodName=...
     * Returns a product by name (case-insensitive).
     */
    @GetMapping("/searchByName")
    public ResponseEntity<ProductDto> getProductByName(@RequestParam String prodName) {
        ProductDto product = productServices.getProductByName(prodName);

        if (product.getMessage() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(product);
        }
        return ResponseEntity.ok(product);
    }


    // @GetMapping("/searchByCategory")
    // public ResponseEntity<List<ProductDto>> getProductsByCategory(@RequestParam String category) {
    //     List<ProductDto> products = productServices.getProductsByCategory(category);
    //     return ResponseEntity.ok(products);
    // }

    @GetMapping("/searchByCountry")
    public ResponseEntity<List<ProductDto>> getProductsByCountry(@RequestParam String country) {
        List<ProductDto> products = productServices.getProductByCountry(country);
        return ResponseEntity.ok(products);
    }
    // ─── HEALTH CHECK ────────────────────────────────────────────────────────────

    @GetMapping("/greetings")
    public ResponseEntity<String> greeting() {
        return ResponseEntity.ok("Hello prince, I'm a visible EndPoint");
    }
}
