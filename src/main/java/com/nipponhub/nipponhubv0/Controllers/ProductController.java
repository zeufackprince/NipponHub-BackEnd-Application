package com.nipponhub.nipponhubv0.Controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nipponhub.nipponhubv0.DTO.ProductDto;
import com.nipponhub.nipponhubv0.Services.ProductService;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v0/product")
@Data
public class ProductController {
    
    private final ProductService productServices;

    @PostMapping("/newProduct")
    public ProductDto newProduct(
        @RequestParam(required = false, name = "ProdName") String ProdName,
        @RequestParam(required = false, name = "UnitPrice") BigDecimal UnitPrice,
        @RequestParam(required = false, name = "SoldPrice") BigDecimal SoldPrice,
        @RequestParam(required = false, name = "ProdQty") Integer ProdQty,
        @RequestParam(required = false ,name = "ProdUrl") List<MultipartFile> ProdUrl,
        @RequestParam(required = false, name = "Country") List<String> Country,
        @RequestParam(required = false, name = "category") String category
    ){

        ProductDto reqProductDto = null;

            reqProductDto = this.productServices.createProduct(ProdName, UnitPrice, SoldPrice, ProdQty, ProdUrl, Country, category);

        return reqProductDto;
    }

    @PostMapping("/updateProduct/{idProd}")
    public ProductDto updateProduct(
        @PathVariable Long idProd,
        @RequestParam(required = false, name = "ProdName") String ProdName,
        @RequestParam(required = false, name = "UnitPrice") BigDecimal UnitPrice,
        @RequestParam(required = false, name = "SoldPrice") BigDecimal SoldPrice,
        @RequestParam(required = false, name = "ProdQty") Integer ProdQty,
        @RequestParam(required = false ,name = "ProdUrl") List<MultipartFile> ProdUrl,
        @RequestParam(required = false, name = "Country") List<String> Country,
        @RequestParam(required = false, name = "category") String category
    ) {
        ProductDto reqProductDto = null;
        
            reqProductDto = this.productServices.updateProduct(idProd, ProdName, UnitPrice, SoldPrice, ProdQty, ProdUrl, Country, category);

        return reqProductDto;
    }

    // ─── GET ALL PRODUCTS ───────────────────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productServices.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // ─── GET PRODUCT BY ID ──────────────────────────────────────────
    @GetMapping("/{idProd}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long idProd) {
        ProductDto product = productServices.getProductById(idProd);

        // If the message field is set, something went wrong
        if (product.getMessage() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(product);
        }
        return ResponseEntity.ok(product);
    }

    // ─── GET PRODUCT BY NAME ────────────────────────────────────────
    @GetMapping("/search")
    public ResponseEntity<ProductDto> getProductByName(
        @RequestParam String prodName
    ) {
        ProductDto product = productServices.getProductByName(prodName);

        if (product.getMessage() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(product);
        }
        return ResponseEntity.ok(product);
    }

    // @GetMapping("/all")
    // public ResponseEntity<Page<ProductDto>> getAllProducts(
    //     @RequestParam(defaultValue = "0")  int page,
    //     @RequestParam(defaultValue = "10") int size
    // )    {
    //     return ResponseEntity.ok(productServices.getAllProducts(page, size));
    // }

    @GetMapping("/greatings")
    public String greating() {
        return("Hello prince, I'm a visible EndPoint");
    }


}
