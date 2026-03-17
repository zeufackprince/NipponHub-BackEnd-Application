package com.nipponhub.nipponhubv0.Controllers;

import com.nipponhub.nipponhubv0.DTO.CategoryDto;
import com.nipponhub.nipponhubv0.Services.CategoryServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v0/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryServices categoryServices;

    // ─── CREATE ──────────────────────────────────────────────────────────────────

    @PostMapping("/createCategory")
    public ResponseEntity<Map<String, String>> createCategory(
        @RequestBody CategoryDto request
    ) {
        String result = categoryServices.createCategory(
            request.getCatProdName(),
            request.getCatProdDes()
        );

        if (result.startsWith("Error")) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", result));
        }

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(Map.of("message", result));
    }

    // ─── READ ALL ────────────────────────────────────────────────────────────────

    /**
     * GET /api/v0/categories/getAllCategories
     */
    @GetMapping("/getAllCategories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryServices.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // ─── READ BY ID ──────────────────────────────────────────────────────────────

    /**
     * GET /api/v0/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return categoryServices.getCategoryById(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Category not found with id: " + id)));
    }
}
