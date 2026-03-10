package com.nipponhub.nipponhubv0.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nipponhub.nipponhubv0.Models.CategoriesProd;

public interface CategoriesRepository extends JpaRepository<Long, CategoriesProd>{

    Optional<CategoriesProd> findByName(String category);
    
}
