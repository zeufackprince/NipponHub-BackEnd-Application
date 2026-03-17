package com.nipponhub.nipponhubv0.Controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nipponhub.nipponhubv0.DTO.VenteDto;
import com.nipponhub.nipponhubv0.Models.Vente;
import com.nipponhub.nipponhubv0.Services.VenteService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v0/vente")
@AllArgsConstructor
public class VenteController {

    private final VenteService venteService;

    /**
     * POST /api/v0/vente/register
     * Enregistre une vente, calcule le gain et déduit les stocks.
     */
    @PostMapping("/register")
    public ResponseEntity<VenteDto> registerVente(@RequestBody Vente vente) {
        VenteDto res = venteService.registerVente(vente);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    /**
     * GET /api/v0/vente/all
     * Liste toutes les ventes effectuées.
     */
    @GetMapping("/all")
    public ResponseEntity<List<VenteDto>> getAllVentes() {
        return ResponseEntity.ok(venteService.getAllVente());
    }

    /**
     * GET /api/v0/vente/{id}
     * Récupère une vente spécifique par son identifiant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VenteDto> getVenteById(@PathVariable Long id) {
        return ResponseEntity.ok(venteService.getVenteById(id));
    }

    /**
     * GET /api/v0/vente/search?date=YYYY-MM-DD
     * Recherche les ventes par date spécifique.
     */
    @GetMapping("/search")
    public ResponseEntity<List<VenteDto>> getVentesByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(venteService.getVentesByDate(date));
    }
}
