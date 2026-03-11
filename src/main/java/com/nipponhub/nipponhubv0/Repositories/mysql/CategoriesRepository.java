package com.nipponhub.nipponhubv0.Repositories.mysql;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nipponhub.nipponhubv0.Models.CategoriesProd;

public interface CategoriesRepository extends JpaRepository<CategoriesProd, Long>{

    Optional<CategoriesProd> findByCatProdName(String catProdName);
    
}
