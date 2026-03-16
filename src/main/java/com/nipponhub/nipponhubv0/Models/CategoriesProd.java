package com.nipponhub.nipponhubv0.Models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "products")
@Entity
@Table(name = "categories_prod")
public class CategoriesProd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCatProd;

    // FIX: snake_case to match the actual DB column Hibernate creates
    @Column(name = "cat_prod_name", nullable = false, unique = true)
    private String catProdName;

    @Column(name = "cat_prod_des")
    private String catProdDes;

    // FIX: @JsonIgnore prevents infinite recursion when serializing
    // FIX: initialized to avoid NullPointerException on empty categories
    @JsonIgnore
    @OneToMany(mappedBy = "categoriesProd", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    // ── equals & hashCode — PK only (JPA best practice) ──────────────────────
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoriesProd)) return false;
        CategoriesProd other = (CategoriesProd) o;
        return idCatProd != null && idCatProd.equals(other.idCatProd);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
