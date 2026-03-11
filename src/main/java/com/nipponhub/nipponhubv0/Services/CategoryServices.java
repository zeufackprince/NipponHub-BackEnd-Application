package com.nipponhub.nipponhubv0.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nipponhub.nipponhubv0.Models.CategoriesProd;
import com.nipponhub.nipponhubv0.Repositories.mysql.CategoriesRepository;

import lombok.Data;

@Service
@Data
public class CategoryServices {

    private final CategoriesRepository categoriesRepository;
    
    public String createCategory(String catProdName) {
        String res = "";
        try {
            CategoriesProd category = new CategoriesProd();
            category.setCatProdName(catProdName);

            this.categoriesRepository.save(category);
            res = "Category Created Successfully...";
        } catch (Exception e) {
            res = "Error Creating Category..." + e;
        }
        return res;
    }

    public List<CategoriesProd> getAllCategories() {
        return this.categoriesRepository.findAll();
    }
}
