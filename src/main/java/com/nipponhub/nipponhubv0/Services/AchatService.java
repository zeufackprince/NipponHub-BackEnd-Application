package com.nipponhub.nipponhubv0.Services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nipponhub.nipponhubv0.DTO.AchatDto;
import com.nipponhub.nipponhubv0.DTO.AchatItemDetailDto;
import com.nipponhub.nipponhubv0.Models.AchatItem;
import com.nipponhub.nipponhubv0.Models.Achats;
import com.nipponhub.nipponhubv0.Models.Product;
import com.nipponhub.nipponhubv0.Repositories.mysql.AchatRepository;
import com.nipponhub.nipponhubv0.Repositories.mysql.ProductRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AchatService {

    private final AchatRepository achatRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;

    @Transactional
    public AchatDto enregistrerAchat(Achats achats) {
        achats.setDate(LocalDate.now());
        
        for (AchatItem item : achats.getItems()) {
            // Note: Make sure product is fetched or ID is present before calling this
            productService.ajouterQuantite(item.getProduct().getProdName(), item.getQuantite());
            item.setAchat(achats);
        }
        
        Achats dbAchats = this.achatRepository.save(achats);
        return entityToDto(dbAchats, "Achat enregistré avec succès");
    }

    @Transactional(readOnly = true)
    public List<AchatDto> getAllAchats() {
        return this.achatRepository.findAll().stream()
                .map(achat -> entityToDto(achat, "Data fetched successfully"))
                .collect(Collectors.toList());
    }

    private AchatDto entityToDto(Achats achats, String message) {
        AchatDto res = new AchatDto();
        res.setId(achats.getId());
        res.setDate(achats.getDate());
        res.setMessage(message);

        BigDecimal totalCost = BigDecimal.ZERO;
        int totalItems = 0;
        
        List<AchatItemDetailDto> itemDetails = achats.getItems().stream().map(item -> {
            Product prod = productRepository.findById(item.getProduct().getIdProd())
                .orElseThrow(() -> new EntityNotFoundException("Produit not found with ID: " + item.getProduct().getIdProd()));

            BigDecimal qte = BigDecimal.valueOf(item.getQuantite());
            BigDecimal itemTotal = prod.getUnitPrice().multiply(qte);

            return new AchatItemDetailDto(
                prod.getIdProd(),
                prod.getProdName(),
                item.getQuantite(),
                prod.getUnitPrice(),
                itemTotal
            );
        }).collect(Collectors.toList());

        for (AchatItemDetailDto detail : itemDetails) {
            totalCost = totalCost.add(detail.getTotal());
            totalItems += detail.getQuantite();
        }

        res.setCoutTotal(totalCost);
        res.setTotalItem(totalItems);
        res.setItems(itemDetails);

        return res;
    }
}