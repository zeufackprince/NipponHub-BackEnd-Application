package com.nipponhub.nipponhubv0.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table
@Entity
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class FranchiseProd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFranchiseProd;

    @Column(name = "franchise_prod_name", nullable = false)
    private String franchiseProdName;

    @Column(name = "franchise_prod_description")
    private String franchiseProdDes;

    @Column(name = "franchise_prod_url")
    private String franchiseProdUrl;

    @ManyToOne
    private Product product;

}
