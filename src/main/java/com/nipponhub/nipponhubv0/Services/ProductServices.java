package com.nipponhub.nipponhubv0.Services;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nipponhub.nipponhubv0.Controllers.FileController;
import com.nipponhub.nipponhubv0.DTO.ProductDto;
import com.nipponhub.nipponhubv0.Mappers.ProductMapper;
import com.nipponhub.nipponhubv0.Models.Product;
import com.nipponhub.nipponhubv0.Repositories.ProductRepository;

import lombok.Data;

@Service
@Data
public class ProductServices {

    private final ProductRepository productRepository;

    private final ProductMapper prodMapper;

    private final FileController fileController;

    private final String path ="/Product_images";

    private final String baseUrl = "http://localhost:8080";

    public ProductDto createProduct(String prodName, String unitprice, String soldPrice, Integer prodQty,
            List<MultipartFile> ProdUrl) throws IOException {

                Product prod = new Product();
                prod.setProdName(prodName);
                prod.setProdQty(prodQty);
                prod.setUnitprice(unitprice);

                List<String> poster = new ArrayList<>();
                List<String> posterUrl = new ArrayList<>();

                for (MultipartFile file : ProdUrl){
                    if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
                        throw new FileAlreadyExistsException("File already exists! Please enter another file name!");
                    }
                    String uploadedFileName = fileController.uploadFileHandler(file);
                    poster.add(uploadedFileName);
                    String posterUrls = baseUrl + "/file/" + uploadedFileName;
                    posterUrl.add(posterUrls);
                }
                
                prod.setProdUrl(poster);

                Product product = this.productRepository.save(prod);

                ProductDto res = prodMapper.prodToDto(product);

            return res;
    }
    
}
