package com.nipponhub.nipponhubv0.Services;

import com.nipponhub.nipponhubv0.DTO.ProductDto;
import com.nipponhub.nipponhubv0.Mappers.ProductMapper;
import com.nipponhub.nipponhubv0.Models.CategoriesProd;
import com.nipponhub.nipponhubv0.Models.Country;
import com.nipponhub.nipponhubv0.Models.Product;
import com.nipponhub.nipponhubv0.Repositories.mysql.CategoriesRepository;
import com.nipponhub.nipponhubv0.Repositories.mysql.CountryRepository;
import com.nipponhub.nipponhubv0.Repositories.mysql.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper prodMapper;
    private final CountryRepository countryRepository;
    private final CategoriesRepository categoriesRepository;
    private final FileStorageService fileStorageService;

    // ─── CREATE ──────────────────────────────────────────────────────────────────

    @Transactional
    public ProductDto createProduct(
        String prodName,
        BigDecimal unitPrice,
        BigDecimal soldPrice,
        Integer prodQty,
        List<MultipartFile> imageFiles,
        List<String> countryNames,
        String categoryName,
        String prodDescription
    ) throws IOException {

        validateProductInputs(prodName, unitPrice, soldPrice, prodQty, countryNames, categoryName, prodDescription);

        List<String> fileIds = new ArrayList<>();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            fileIds = fileStorageService.uploadFiles(imageFiles);
        }

        try {
            CategoriesProd category = categoriesRepository.findByCatProdName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryName));

            List<Country> countries = countryRepository.findByCountryNameIn(countryNames);
            if (countries.isEmpty()) {
                throw new RuntimeException("No countries found for: " + countryNames);
            }

            Product prod = new Product();

            prod.setProdName(prodName);
            prod.setUnitPrice(unitPrice);
            prod.setSoldPrice(soldPrice);
            prod.setProdQty(prodQty);
            prod.setProdUrl(fileIds);
            prod.setCategoriesProd(category);
            prod.setCountries(countries);
            prod.setProdDescription(prodDescription);

            Product saved = productRepository.save(prod);

            log.info("Product created — id: {}, name: {}", saved.getIdProd(), prodName);
            return prodMapper.prodToDto(saved);

        } catch (Exception e) {
            if (!fileIds.isEmpty()) {
                log.warn("MySQL save failed — rolling back {} GridFS file(s)", fileIds.size());
                fileStorageService.deleteFiles(fileIds);
            }
            throw e;
        }
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────────

    @Transactional
    public ProductDto updateProduct(
        Long idProd,
        String prodName,
        BigDecimal unitPrice,
        BigDecimal soldPrice,
        Integer prodQty,
        List<MultipartFile> newImageFiles,
        List<String> countryNames,
        String categoryName,
        String prodDescription
    ) throws IOException {

        ProductDto res = new ProductDto();
        List<String> newFileIds  = new ArrayList<>();
        List<String> oldFileIds  = new ArrayList<>();

        try {
            // ✅ fetch join — all relations loaded, no proxy needed
            Product prod = productRepository.findByIdWithRelations(idProd)
                .orElseThrow(() -> new RuntimeException("Product not found: " + idProd));

            if (newImageFiles != null && !newImageFiles.isEmpty()) {
                oldFileIds = new ArrayList<>(prod.getProdUrl());
                newFileIds = fileStorageService.uploadFiles(newImageFiles, idProd);
                prod.setProdUrl(newFileIds);
            }

            if (prodName  != null) prod.setProdName(prodName);
            if (unitPrice != null) prod.setUnitPrice(unitPrice);
            if (soldPrice != null) prod.setSoldPrice(soldPrice);
            if (prodQty   != null) prod.setProdQty(prodQty);
            if (prodDescription   != null) prod.setProdDescription(prodDescription);;

            if (categoryName != null) {
                categoriesRepository.findByCatProdName(categoryName)
                    .ifPresent(prod::setCategoriesProd);
            }

            if (countryNames != null && !countryNames.isEmpty()) {
                prod.setCountries(countryRepository.findByCountryNameIn(countryNames));
            }

            Product updated = productRepository.save(prod);

            if (!oldFileIds.isEmpty()) {
                fileStorageService.deleteFiles(oldFileIds);
                log.info("Replaced {} old image(s) for product id: {}", oldFileIds.size(), idProd);
            }

            res = prodMapper.prodToDto(updated);
            log.info("Product updated — id: {}", idProd);

        } catch (Exception e) {
            if (!newFileIds.isEmpty()) {
                log.warn("Update failed — rolling back {} GridFS file(s)", newFileIds.size());
                fileStorageService.deleteFiles(newFileIds);
            }
            log.error("Error updating product {}: {}", idProd, e.getMessage(), e);
            res.setMessage("Error updating product: " + e.getMessage());
        }

        return res;
    }

    // ─── READ ────────────────────────────────────────────────────────────────────

    /**
     * Uses findAllWithRelations() — a JPQL JOIN FETCH query that loads
     * categoriesProd, franchiseProd, and countries in one SQL statement.
     * No lazy proxy, no session dependency, no @Transactional needed for reads.
     * @Transactional kept as an extra safety net.
     */

    @Transactional
    public List<ProductDto> getAllProducts() {
        try {
            return productRepository.findAllWithRelations() 
                .stream()
                .map(prodMapper::prodToDto)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all products: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Transactional
    public ProductDto getProductById(Long idProd) {
        ProductDto res = new ProductDto();
        try {
            Product product = productRepository.findByIdWithRelations(idProd)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + idProd));
            res = prodMapper.prodToDto(product);
        } catch (Exception e) {
            log.error("Error fetching product by id {}: {}", idProd, e.getMessage(), e);
            res.setMessage("Error: " + e.getMessage());
        }
        return res;
    }

    @Transactional
    public ProductDto getProductByName(String prodName) {
        ProductDto res = new ProductDto();
        try {
            Product product = productRepository
                .findByProdNameIgnoreCaseWithRelations(prodName)               // ✅ fetch join
                .orElseThrow(() -> new RuntimeException("Product not found with name: " + prodName));
            res = prodMapper.prodToDto(product);
        } catch (Exception e) {
            log.error("Error fetching product by name {}: {}", prodName, e.getMessage(), e);
            res.setMessage("Error: " + e.getMessage());
        }
        return res;
    }


    @Transactional
    public List<ProductDto> getProductByCountry(String countryName) {
 
    List<ProductDto> res = new ArrayList<>();
 
    try {

        Optional<Country> dbCountry = countryRepository.getByCountryName(countryName);
 
        if (dbCountry.isEmpty()) {
            log.warn("No country found with name: {}", countryName);
            return res;
        }
 
        Country country = dbCountry.get();
 
        List<Product> products = productRepository.findByCountriesContaining(country);
 
        if (products.isEmpty()) {
            log.info("No products found for country: {}", countryName);
            return res;
        }
 
        res = products.stream()
            .map(prodMapper::prodToDto)
            .collect(Collectors.toList());
 
        log.info("Found {} product(s) for country: {}", res.size(), countryName);
 
    } catch (Exception e) {
        log.error("Error fetching products by country '{}': {}", countryName, e.getMessage(), e);
    }
 
    return res;
}

    @Transactional
    public void retirerQuantite(Long idProd, int quantite) {
        if (idProd == null) {
            throw new IllegalArgumentException("idProd cannot be null");
        }
        if (quantite <= 0) {
            throw new IllegalArgumentException("Quantite must be greater than zero");
        }

        Product product = productRepository.findById(idProd)
            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + idProd));

        Integer currentQty = product.getProdQty();
        if (currentQty == null) {
            throw new IllegalStateException("Product quantity is undefined for id: " + idProd);
        }
        if (currentQty < quantite) {
            throw new IllegalStateException("Insufficient stock. Current: " + currentQty + ", requested: " + quantite);
        }

        product.setProdQty(currentQty - quantite);
        productRepository.save(product);
    }

    // ─── PRIVATE ─────────────────────────────────────────────────────────────────

    private void validateProductInputs(
        String prodName, BigDecimal unitPrice, BigDecimal soldPrice,
        Integer prodQty, List<String> countryNames, String categoryName, String prodDescription
    ) {
        if (prodName == null || prodName.isBlank())
            throw new IllegalArgumentException("Product name is required");
        if (unitPrice == null)
            throw new IllegalArgumentException("Unit price is required");
        if (soldPrice == null)
            throw new IllegalArgumentException("Sold price is required");
        if (prodQty == null || prodQty < 0)
            throw new IllegalArgumentException("Product quantity must be zero or greater");
        if (countryNames == null || countryNames.isEmpty())
            throw new IllegalArgumentException("At least one country is required");
        if (categoryName == null || categoryName.isBlank())
            throw new IllegalArgumentException("Category is required");
        if(prodDescription == null || prodDescription.isBlank()) 
            throw new IllegalArgumentException("Description is required");
    }

    public void ajouterQuantite(String name, int quantite) {
        Optional<Product> p = productRepository.findByProdName(name);
        if (p == null) {
            throw new RuntimeException("Produit non trouvé");
        }
        
        p.get().setProdQty(p.get().getProdQty() + quantite);
        productRepository.save(p.get());
    }


}
