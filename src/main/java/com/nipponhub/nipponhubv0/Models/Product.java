package com.nipponhub.nipponhubv0.Models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProd;

    @Column(name = "prod_name")
    private String prodName;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "sold_price")
    private BigDecimal soldPrice;

    @Column(name = "prod_qty")
    private Integer prodQty;

    @ElementCollection
    @CollectionTable(
        name = "product_images",
        joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "url")
    private List<String> prodUrl = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "id_cat_prod")
    private CategoriesProd categoriesProd;

    @ManyToOne
    @JoinColumn(name = "franchise_id")
    private FranchiseProd franchiseProd;

    // ✅ Product owns the join table
    @ManyToMany
    @JoinTable(
        name = "product_country",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "country_id")
    )
    private List<Country> countries = new ArrayList<>();

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = new Date();
    }
}