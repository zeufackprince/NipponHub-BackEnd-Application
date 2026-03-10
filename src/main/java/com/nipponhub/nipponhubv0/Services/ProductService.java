package com.nipponhub.nipponhubv0.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nipponhub.nipponhubv0.DTO.ProductDto;
import com.nipponhub.nipponhubv0.Mappers.ProductMapper;
import com.nipponhub.nipponhubv0.Models.Country;
import com.nipponhub.nipponhubv0.Models.Product;
import com.nipponhub.nipponhubv0.Repositories.CategoriesRepository;
import com.nipponhub.nipponhubv0.Repositories.CountryRepository;
import com.nipponhub.nipponhubv0.Repositories.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// @Service
// @Data
// public class ProductServices {

//     private final ProductRepository productRepository;

    // private final ProductMapper prodMapper;

    // private final CountryRepository countryRepository;

    // private final CategoriesRepository categoriesRepository;

    // private final FileController fileController;

    // private final String path ="/Product_images";

    // private final String baseUrl = "http://localhost:8080";

    // public ProductDto createProduct(
    //                             String prodName, 
    //                             BigDecimal unitprice, 
    //                             BigDecimal soldPrice, 
    //                             Integer prodQty,
    //                             List<MultipartFile> ProdUrl, String country, String category
    //                         )  {
            
    //             Product prod = new Product();

    //             ProductDto res = new ProductDto();
    //             try {
                
    //             Optional<CategoriesProd> cat = this.categoriesRepository.findByName(category);

    //             Optional<Country> coun = this.countryRepository.findByName(country);

    //             if(cat.isPresent()) {
    //                 prod.setCategoriesProd(cat.get());
    //             }

    //             if(coun.isPresent()) {
    //                 prod.setCountries((List<Country>) coun.get());
    //             }

    //             res.setCategorieName(category);
    //             res.setCountryName(country);

    //             List<String> poster = new ArrayList<>();
    //             List<String> posterUrl = new ArrayList<>();

    //             for (MultipartFile file : ProdUrl){
    //                 if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
    //                     throw new FileAlreadyExistsException("File already exists! Please enter another file name!");
    //                 }
    //                 String uploadedFileName = fileController.uploadFileHandler(file);
    //                 poster.add(uploadedFileName);
    //                 String posterUrls = baseUrl + "/file/" + uploadedFileName;
    //                 posterUrl.add(posterUrls);
    //             }
                
    //             prod.setProdUrl(poster);
    //         } catch (Exception e)
    //         {
    //             res.setMessage("Error Creating the Product..." + e);
    //         }
    //             Product product = this.productRepository.save(prod);

    //             res = prodMapper.prodToDto(product);

    //         return res;
    // }

    // public ProductDto updateProduct(Long idProd, String prodName, BigDecimal unitPrice, BigDecimal soldPrice, Integer prodQty, List<MultipartFile> prodUrl, String country, String category) {
    //     ProductDto res = new ProductDto();
    //     try {
            
    //         Product prod = this.productRepository.findById(idProd).orElseThrow(() -> new RuntimeException("Product not found!"));
    //     List<String> poster = new ArrayList<>();
    //     List<String> posterUrl = new ArrayList<>();

    //     if (!prodUrl.isEmpty()) {

    //         List<String> fileName = prod.getProdUrl();
    //         for (String ImgName : fileName) {
    //             Files.deleteIfExists(Paths.get(path + File.separator + ImgName));
    //         }

    //         for (MultipartFile file : prodUrl) {
                
    //             String uploadedFileName = fileController.uploadFileHandler(file);
    //             poster.add(uploadedFileName);
    //             String posterUrls = baseUrl + "/file/" + uploadedFileName;
    //             posterUrl.add(posterUrls);
    //         }
    //     }
    //         prod.setProdName(prodName);
    //         prod.setUnitPrice(unitPrice);
    //         prod.setSoldPrice(soldPrice);
    //         prod.setProdQty(prodQty);
    //         prod.setProdUrl(poster);

    //         Product updatedProduct = this.productRepository.save(prod);

    //         res = prodMapper.prodToDto(updatedProduct);
    //     } catch (Exception e) {
    //         res.setMessage("Error Updating the Product..." + e);
    //     }
        
    //     return res;
    // }

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper prodMapper;
    private final CountryRepository countryRepository;
    private final CategoriesRepository categoriesRepository;
    private final FileStorageService fileStorageService; // ✅ replaces FileController
    // private final String baseUrl = "${app.base-url}";
    private final String baseUrl = "http://localhost:8080";

    public ProductDto createProduct(
        String prodName,
        BigDecimal unitPrice,
        BigDecimal soldPrice,
        Integer prodQty,
        List<MultipartFile> imageFiles,
        List<String> countryNames,
        String categoryName
    ) {
        ProductDto res = new ProductDto();
        try {
            Product prod = new Product();

            // Set category
            categoriesRepository.findByName(categoryName)
                .ifPresent(prod::setCategoriesProd);

            // Set countries (ManyToMany — accepts a list now)
            List<Country> countries = countryRepository.findByCountryNameIn(countryNames);
            prod.setCountries(countries);

            // Upload images to MongoDB GridFS and store their IDs
            if (imageFiles != null && !imageFiles.isEmpty()) {
                List<String> fileIds = fileStorageService.uploadFiles(imageFiles);
                prod.setProdUrl(fileIds); // stores MongoDB ObjectIds
            }

            prod.setProdName(prodName);
            prod.setUnitPrice(unitPrice);
            prod.setSoldPrice(soldPrice);
            prod.setProdQty(prodQty);

            Product saved = productRepository.save(prod);
            res = prodMapper.prodToDto(saved);

            log.info("Product created — id: {}, name: {}", saved.getIdProd(), prodName);

        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            res.setMessage("Error creating product: " + e.getMessage());
        }
        return res;
    }

    public ProductDto updateProduct(
        Long idProd,
        String prodName,
        BigDecimal unitPrice,
        BigDecimal soldPrice,
        Integer prodQty,
        List<MultipartFile> newImageFiles,
        List<String> countryNames,
        String categoryName
    ) {
        ProductDto res = new ProductDto();
        try {
            Product prod = productRepository.findById(idProd)
                .orElseThrow(() -> new RuntimeException("Product not found: " + idProd));

            // Replace images only if new ones are provided
            if (newImageFiles != null && !newImageFiles.isEmpty()) {
                // Delete old images from MongoDB
                fileStorageService.deleteFiles(prod.getProdUrl());

                // Upload new images
                List<String> newFileIds = fileStorageService.uploadFiles(newImageFiles);
                prod.setProdUrl(newFileIds);
            }

            // Update fields only if provided
            if (prodName    != null) prod.setProdName(prodName);
            if (unitPrice   != null) prod.setUnitPrice(unitPrice);
            if (soldPrice   != null) prod.setSoldPrice(soldPrice);
            if (prodQty     != null) prod.setProdQty(prodQty);

            if (categoryName != null) {
                categoriesRepository.findByName(categoryName)
                    .ifPresent(prod::setCategoriesProd);
            }

            if (countryNames != null && !countryNames.isEmpty()) {
                prod.setCountries(countryRepository.findByCountryNameIn(countryNames));
            }

            Product updated = productRepository.save(prod);
            res = prodMapper.prodToDto(updated);

            log.info("Product updated — id: {}", idProd);

        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage(), e);
            res.setMessage("Error updating product: " + e.getMessage());
        }
        return res;
    }


    // ─── GET ALL PRODUCTS ───────────────────────────────────────────
    public List<ProductDto> getAllProducts() {
        List<ProductDto> res = new ArrayList<>();
        try {
            List<Product> products = productRepository.findAll();
            res = products.stream()
                .map(prodMapper::prodToDto)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all products: {}", e.getMessage());
        }
        return res;
    }

// ─── GET PRODUCT BY ID ──────────────────────────────────────────
    public ProductDto getProductById(Long idProd) {
        ProductDto res = new ProductDto();
        try {
            Product product = productRepository.findById(idProd)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + idProd));

            res = prodMapper.prodToDto(product);

        } catch (Exception e) {
            log.error("Error fetching product by id {}: {}", idProd, e.getMessage());
            res.setMessage("Error: " + e.getMessage());
        }
        return res;
    }

// ─── GET PRODUCT BY NAME ────────────────────────────────────────
    public ProductDto getProductByName(String prodName) {
        ProductDto res = new ProductDto();
        try {
            Product product = productRepository.findByProdName(prodName)
                    .orElseThrow(() -> new RuntimeException("Product not found with name: " + prodName));

            res = prodMapper.prodToDto(product);

        } catch (Exception e) {
            log.error("Error fetching product by name {}: {}", prodName, e.getMessage());
            res.setMessage("Error: " + e.getMessage());
        }
        return res;
    }

    // public Page<ProductDto> getAllProducts(int page, int size) {
    // Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    // return productRepository.findAll(pageable)
    //         .map(prodMapper::prodToDto);
    // }

    
}
