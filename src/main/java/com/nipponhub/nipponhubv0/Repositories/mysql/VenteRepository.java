package com.nipponhub.nipponhubv0.Repositories.mysql;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nipponhub.nipponhubv0.Models.Vente;

public interface VenteRepository extends JpaRepository<Vente, Long> {

    List<Vente> findByDate(LocalDate date);

    
    
}
