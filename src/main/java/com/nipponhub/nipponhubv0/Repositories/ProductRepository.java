package com.nipponhub.nipponhubv0.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nipponhub.nipponhubv0.Models.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
    
    // Find by exact name
    Optional<Product> findByProdName(String prodName);

    // Find by name containing a keyword (useful for search)
    List<Product> findByProdNameContainingIgnoreCase(String keyword);
}
