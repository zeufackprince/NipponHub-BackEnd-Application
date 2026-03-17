package com.nipponhub.nipponhubv0.Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// 

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"categoriesProd", "franchiseProd", "countries"}) 
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProd;

    @Column(name = "prod_name", nullable = false)
    private String prodName;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "sold_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal soldPrice;

    @Column(name = "prod_qty", nullable = false)
    private Integer prodQty;

    @Column(name = "prod_description", nullable = false)
    private String prodDescription;

    /**
     * List of MongoDB GridFS ObjectId strings.
     * Stored in a separate join table "product_images" in MySQL.
     * The actual image binaries live in MongoDB GridFS.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "product_images",
        joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "url")
    private List<String> prodUrl = new ArrayList<>();

    /**
     * Timestamp set automatically on first persist.
     * Uses LocalDateTime instead of legacy java.util.Date.
     */
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cat_prod")
    private CategoriesProd categoriesProd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id")
    private FranchiseProd franchiseProd;

    /**
     * Many-to-many with Country.
     * Product owns the join table "product_country".
     * FetchType.LAZY avoids loading all countries on every product query.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "product_country",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "country_id")
    )
    private List<Country> countries = new ArrayList<>();

    // ─── LIFECYCLE ───────────────────────────────────────────────────────────────

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product other = (Product) o;
        return idProd != null && idProd.equals(other.idProd);
    }

    @Override
    public int hashCode() {
        // Fixed value so hashCode is stable before and after persistence
        return getClass().hashCode();
    }
}
