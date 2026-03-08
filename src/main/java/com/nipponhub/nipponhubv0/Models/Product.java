package com.nipponhub.nipponhubv0.Models;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long IdProd;

    @Column(name = "ProdName")
    private String ProdName;

    @Column(name = "Unitprice")
    private String Unitprice;

    @Column(name = "SoldPrice")
    private String SoldPrice;

    @Column(name = "ProdQty")
    private Integer ProdQty;
    
    @Column(name = "ProdUrl")
    private List<String> ProdUrl;

    @Column(name = "CreatedAt")
    private Date createdAt;

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = new Date();
    } 
}
