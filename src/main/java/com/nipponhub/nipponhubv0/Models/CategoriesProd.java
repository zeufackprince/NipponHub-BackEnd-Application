package com.nipponhub.nipponhubv0.Models;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories_prod")
@Data
@NoArgsConstructor
public class CategoriesProd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCatProd;

    @Column(name = "cat_prod_name", nullable = false)
    private String catProdName;

    @Column(name = "cat_prod_description")
    private String catProdDes;

    @Column(name = "cat_prod_url")
    private String catProdUrl;

    @OneToMany(mappedBy = "categoriesProd")
    private List<Product> products;

}
