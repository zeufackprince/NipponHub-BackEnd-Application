package com.nipponhub.nipponhubv0.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nipponhub.nipponhubv0.Models.CategoriesProd;
import com.nipponhub.nipponhubv0.Services.CategoryServices;

import lombok.Data;

@RestController
@RequestMapping("/api/v0/categories")
@Data
public class CategoryController {
    
    private final CategoryServices categoryServices;

    @PostMapping("/createCategory")
    public String createCategory(String catProdName) {
        return this.categoryServices.createCategory(catProdName);
    }

    @GetMapping("/getAllCategories")
    public List<CategoriesProd> getAllCategories() {
        return this.categoryServices.getAllCategories();
    }
}
