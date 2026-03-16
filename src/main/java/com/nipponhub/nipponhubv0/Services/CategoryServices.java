package com.nipponhub.nipponhubv0.Services;

import com.nipponhub.nipponhubv0.DTO.CategoryDto;
import com.nipponhub.nipponhubv0.Models.CategoriesProd;
import com.nipponhub.nipponhubv0.Repositories.mysql.CategoriesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServices {

    private final CategoriesRepository categoriesRepository;

    // ─── CREATE ──────────────────────────────────────────────────────────────────

    public String createCategory(String catProdName, String catProdDes) {
        try {
            // ── Validate ──────────────────────────────────────────────────────
            if (catProdName == null || catProdName.isBlank()) {
                return "Error Creating Category: name must not be blank";
            }

            // ── Check for duplicate ───────────────────────────────────────────
            if (categoriesRepository.findByCatProdName(catProdName).isPresent()) {
                return "Error Creating Category: '" + catProdName + "' already exists";
            }

            // ── Save ──────────────────────────────────────────────────────────
            CategoriesProd category = new CategoriesProd();
            category.setCatProdName(catProdName.trim());
            category.setCatProdDes(catProdDes != null ? catProdDes.trim() : null);

            categoriesRepository.save(category);
            log.info("Category created: {}", catProdName);

            return "Category Created Successfully";

        } catch (Exception e) {
            log.error("Error creating category '{}': {}", catProdName, e.getMessage(), e);
            return "Error Creating Category: " + e.getMessage();
        }
    }

    // ─── READ ─────────────────────────────────────────────────────────────────────

    /**
     * Returns all categories.
     * @Transactional keeps session open in case the products list is accessed.
     */
    @Transactional
    public List<CategoryDto> getAllCategories() {
        
        return categoriesRepository.findAll()
            .stream()
            .map(cat -> {
                CategoryDto dto = new CategoryDto();
                dto.setIdProd(cat.getIdCatProd());
                dto.setCatProdName(cat.getCatProdName());
                dto.setCatProdDes(cat.getCatProdDes());
                return dto;
            })
            .toList();
    }

    /**
     * Find a single category by its primary key.
     */
    @Transactional
    public Optional<CategoriesProd> getCategoryById(Long id) {
        return categoriesRepository.findById(id);
    }
}
