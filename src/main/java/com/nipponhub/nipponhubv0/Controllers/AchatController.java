package com.nipponhub.nipponhubv0.Controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nipponhub.nipponhubv0.DTO.AchatDto;
import com.nipponhub.nipponhubv0.Models.Achats;
import com.nipponhub.nipponhubv0.Services.AchatService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v0/achat")
@AllArgsConstructor
public class AchatController {

    private final AchatService achatService;

    /**
     * POST /api/v0/achat/new-achat
     * Enregistre un nouvel achat et met à jour les stocks.
     */
    @PostMapping("/new-achat")
    public ResponseEntity<AchatDto> newAchat(@RequestBody Achats achats) {
        AchatDto res = this.achatService.enregistrerAchat(achats);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    /**
     * GET /api/v0/achat/get-all-achat
     * Récupère la liste de tous les achats avec les détails structurés.
     */
    @GetMapping("/get-all-achat")
    public ResponseEntity<List<AchatDto>> getAllAchat() {
        List<AchatDto> res = this.achatService.getAllAchats();
        return ResponseEntity.ok(res);
    }
}