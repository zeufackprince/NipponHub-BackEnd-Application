package com.nipponhub.nipponhubv0.Services;

import com.nipponhub.nipponhubv0.DTO.ProductDto;
import com.nipponhub.nipponhubv0.Mappers.ProductMapper;
import com.nipponhub.nipponhubv0.Models.CategoriesProd;
import com.nipponhub.nipponhubv0.Models.Country;
import com.nipponhub.nipponhubv0.Models.Product;
import com.nipponhub.nipponhubv0.Repositories.mysql.CategoriesRepository;
import com.nipponhub.nipponhubv0.Repositories.mysql.CountryRepository;
import com.nipponhub.nipponhubv0.Repositories.mysql.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * Create a new product with optional images.
     *
     * FIX: Manual MongoDB rollback added — if MySQL save fails after GridFS upload,
     * the orphaned files are deleted to keep both databases consistent.
     *
     * FIX: Input validation added — required fields are checked before any DB call.
     */
    @Transactional
    public ProductDto createProduct(
        String prodName,
        BigDecimal unitPrice,
        BigDecimal soldPrice,
        Integer prodQty,
        List<MultipartFile> imageFiles,
        List<String> countryNames,
        String categoryName
    ) throws IOException {

        // ── Input Validation ──────────────────────────────────────────────────
        validateProductInputs(prodName, unitPrice, soldPrice, prodQty, countryNames, categoryName);

        // ── Step 1: Upload images to MongoDB GridFS ───────────────────────────
        // Done BEFORE saving to MySQL so we have the file IDs ready.
        // If MySQL save fails, we manually roll back GridFS (it's non-transactional).
        List<String> fileIds = new ArrayList<>();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            fileIds = fileStorageService.uploadFiles(imageFiles); // productId linked after save
        }

        try {
            // ── Step 2: Resolve category ──────────────────────────────────────
            CategoriesProd category = categoriesRepository.findByCatProdName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryName));

            // ── Step 3: Resolve countries ─────────────────────────────────────
            List<Country> countries = countryRepository.findByCountryNameIn(countryNames);
            if (countries.isEmpty()) {
                throw new RuntimeException("No countries found for: " + countryNames);
            }

            // ── Step 4: Build and save product ────────────────────────────────
            Product prod = new Product();
            prod.setProdName(prodName);
            prod.setUnitPrice(unitPrice);
            prod.setSoldPrice(soldPrice);
            prod.setProdQty(prodQty);
            prod.setProdUrl(fileIds);
            prod.setCategoriesProd(category);
            prod.setCountries(countries);

            Product saved = productRepository.save(prod);
            log.info("Product created — id: {}, name: {}", saved.getIdProd(), prodName);
            return prodMapper.prodToDto(saved);

        } catch (Exception e) {
            // ── Compensating rollback: remove GridFS files if MySQL save failed ─
            if (!fileIds.isEmpty()) {
                log.warn("MySQL save failed — rolling back {} GridFS file(s)", fileIds.size());
                fileStorageService.deleteFiles(fileIds);
            }
            throw e; // re-throw so @Transactional rolls back MySQL too
        }
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────────

    /**
     * Update an existing product.
     *
     * FIX: Declared throws IOException — no longer silently swallows file errors.
     * FIX: MongoDB rollback added for new images if MySQL update fails.
     */
    public ProductDto updateProduct(
        Long idProd,
        String prodName,
        BigDecimal unitPrice,
        BigDecimal soldPrice,
        Integer prodQty,
        List<MultipartFile> newImageFiles,
        List<String> countryNames,
        String categoryName
    ) throws IOException {

        ProductDto res = new ProductDto();
        List<String> newFileIds = new ArrayList<>();
        List<String> oldFileIds = new ArrayList<>();

        try {
            Product prod = productRepository.findById(idProd)
                .orElseThrow(() -> new RuntimeException("Product not found: " + idProd));

            // ── Replace images only if new ones are provided ──────────────────
            if (newImageFiles != null && !newImageFiles.isEmpty()) {
                oldFileIds = new ArrayList<>(prod.getProdUrl()); // save old ids for rollback
                newFileIds = fileStorageService.uploadFiles(newImageFiles, idProd);
                prod.setProdUrl(newFileIds);
            }

            // ── Update scalar fields only if provided ─────────────────────────
            if (prodName    != null) prod.setProdName(prodName);
            if (unitPrice   != null) prod.setUnitPrice(unitPrice);
            if (soldPrice   != null) prod.setSoldPrice(soldPrice);
            if (prodQty     != null) prod.setProdQty(prodQty);

            if (categoryName != null) {
                categoriesRepository.findByCatProdName(categoryName)
                    .ifPresent(prod::setCategoriesProd);
            }

            if (countryNames != null && !countryNames.isEmpty()) {
                prod.setCountries(countryRepository.findByCountryNameIn(countryNames));
            }

            // ── Save ──────────────────────────────────────────────────────────
            Product updated = productRepository.save(prod);

            // ── Delete OLD images only after successful MySQL save ─────────────
            if (!oldFileIds.isEmpty()) {
                fileStorageService.deleteFiles(oldFileIds);
                log.info("Replaced {} old image(s) for product id: {}", oldFileIds.size(), idProd);
            }

            res = prodMapper.prodToDto(updated);
            log.info("Product updated — id: {}", idProd);

        } catch (Exception e) {
            // ── Compensating rollback: remove newly uploaded GridFS files ──────
            if (!newFileIds.isEmpty()) {
                log.warn("Update failed — rolling back {} new GridFS file(s)", newFileIds.size());
                fileStorageService.deleteFiles(newFileIds);
            }
            log.error("Error updating product {}: {}", idProd, e.getMessage(), e);
            res.setMessage("Error updating product: " + e.getMessage());
        }

        return res;
    }

    // ─── READ ────────────────────────────────────────────────────────────────────

    /**
     * Return all products.
     */
    public List<ProductDto> getAllProducts() {
        List<ProductDto> res = new ArrayList<>();
        try {
            res = productRepository.findAll()
                .stream()
                .map(prodMapper::prodToDto)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all products: {}", e.getMessage(), e);
        }
        return res;
    }

    /**
     * Find a product by its MySQL primary key.
     */
    public ProductDto getProductById(Long idProd) {
        ProductDto res = new ProductDto();
        try {
            Product product = productRepository.findById(idProd)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + idProd));
            res = prodMapper.prodToDto(product);
        } catch (Exception e) {
            log.error("Error fetching product by id {}: {}", idProd, e.getMessage(), e);
            res.setMessage("Error: " + e.getMessage());
        }
        return res;
    }

    /**
     * Find a product by name (case-insensitive).
     *
     * FIX: Was case-sensitive — now uses findByProdNameIgnoreCase.
     */
    public ProductDto getProductByName(String prodName) {
        ProductDto res = new ProductDto();
        try {
            Product product = productRepository.findByProdName(prodName)
                .orElseThrow(() -> new RuntimeException("Product not found with name: " + prodName));
            res = prodMapper.prodToDto(product);
        } catch (Exception e) {
            log.error("Error fetching product by name {}: {}", prodName, e.getMessage(), e);
            res.setMessage("Error: " + e.getMessage());
        }
        return res;
    }

    // ─── PRIVATE HELPERS ─────────────────────────────────────────────────────────

    /**
     * Validates required product fields before any database operation.
     * FIX: Prevents products from being saved with null/blank required fields.
     */
    private void validateProductInputs(
        String prodName,
        BigDecimal unitPrice,
        BigDecimal soldPrice,
        Integer prodQty,
        List<String> countryNames,
        String categoryName
    ) {
        if (prodName == null || prodName.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price is required");
        }
        if (soldPrice == null) {
            throw new IllegalArgumentException("Sold price is required");
        }
        if (prodQty == null || prodQty < 0) {
            throw new IllegalArgumentException("Product quantity must be zero or greater");
        }
        if (countryNames == null || countryNames.isEmpty()) {
            throw new IllegalArgumentException("At least one country is required");
        }
        if (categoryName == null || categoryName.isBlank()) {
            throw new IllegalArgumentException("Category is required");
        }
    }
}
