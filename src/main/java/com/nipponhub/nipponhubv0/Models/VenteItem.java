package com.nipponhub.nipponhubv0.Models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
public class VenteItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantite;

    private BigDecimal prix;

    @Column(nullable = false)
    private BigDecimal prixVendu;

    private BigDecimal gain;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Vente vente;

    // getters et setters
}

