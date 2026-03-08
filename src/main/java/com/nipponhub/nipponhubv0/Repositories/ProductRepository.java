package com.nipponhub.nipponhubv0.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nipponhub.nipponhubv0.Models.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
    
}
