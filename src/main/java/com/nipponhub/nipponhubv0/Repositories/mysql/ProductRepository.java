package com.nipponhub.nipponhubv0.Repositories.mysql;

import com.nipponhub.nipponhubv0.Models.Country;
import com.nipponhub.nipponhubv0.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ─── STANDARD FINDERS ────────────────────────────────────────────────────────

    Optional<Product> findByProdName(String prodName);

    Optional<Product> findByProdNameIgnoreCase(String prodName);

    // ─── FETCH JOIN QUERIES ───────────────────────────────────────────────────────

    /**
     * FIX: Replaces findAll() for use in ProductService.getAllProducts().
     *
     * Plain findAll() returns Products with LAZY relations. Even with
     * @Transactional on the service, calling stream().map(mapper) after
     * findAll() can still hit proxy issues for @ManyToOne and @ManyToMany
     * fields because Spring may create sub-transactions internally.
     *
     * This JPQL query uses LEFT JOIN FETCH to eagerly load categoriesProd,
     * franchiseProd, and countries in a single SQL query — no proxy needed.
     *
     * Note: JOIN FETCH on a collection (@ManyToMany countries) requires
     * DISTINCT to avoid duplicate Product rows in the result.
     */
    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN FETCH p.categoriesProd
        LEFT JOIN FETCH p.franchiseProd
        LEFT JOIN FETCH p.countries
        """)
    List<Product> findAllWithRelations();

    /**
     * FIX: Fetch join version of findById — loads all lazy relations
     * in a single query so the mapper never needs a live session.
     *
     * @param id the product primary key
     * @return Optional<Product> with all relations populated
     */
    @Query("""
        SELECT p FROM Product p
        LEFT JOIN FETCH p.categoriesProd
        LEFT JOIN FETCH p.franchiseProd
        LEFT JOIN FETCH p.countries
        WHERE p.idProd = :id
        """)
    Optional<Product> findByIdWithRelations(@Param("id") Long id);

    /**
     * Fetch join version of findByProdName (case-insensitive).
     *
     * @param name the product name to search (case-insensitive)
     * @return Optional<Product> with all relations populated
     */
    @Query("""
        SELECT p FROM Product p
        LEFT JOIN FETCH p.categoriesProd
        LEFT JOIN FETCH p.franchiseProd
        LEFT JOIN FETCH p.countries
        WHERE LOWER(p.prodName) = LOWER(:name)
        """)
    Optional<Product> findByProdNameIgnoreCaseWithRelations(@Param("name") String name);

    List<Product> findByCountriesContaining(Country country);

    // List<Product> findByCategoriesProdContaining(CategoriesProd category);
}
