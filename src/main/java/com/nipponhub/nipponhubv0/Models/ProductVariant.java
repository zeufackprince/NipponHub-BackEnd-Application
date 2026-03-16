package com.nipponhub.nipponhubv0.Models;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_variant")
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class ProductVariant {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String sku;
    private BigDecimal priceOverride;
    
    // @ManyToMany
    // @JoinTable(
    //     name = "product_variant_value",
    //     joinColumns = @JoinColumn(name = "variant_id"),
    //     inverseJoinColumns = @JoinColumn(name = "variant_value_id")
    // )
    // private List<VariantValue> variantValues;
}
