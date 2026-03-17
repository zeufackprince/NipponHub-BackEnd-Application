package com.nipponhub.nipponhubv0.Services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nipponhub.nipponhubv0.DTO.VenteDto;
import com.nipponhub.nipponhubv0.DTO.VenteItemDetailDto;
import com.nipponhub.nipponhubv0.Models.Product;
import com.nipponhub.nipponhubv0.Models.Vente;
import com.nipponhub.nipponhubv0.Models.VenteItem;
import com.nipponhub.nipponhubv0.Repositories.mysql.ProductRepository;
import com.nipponhub.nipponhubv0.Repositories.mysql.VenteRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VenteService {

    private final ProductService productService;

    private final ProductRepository productRepository;
    
    private final VenteRepository venteRepo;

    @Transactional
    public VenteDto registerVente(Vente vente) {
        vente.setDate(LocalDate.now());

        for (VenteItem item : vente.getItems()) {
            Product prod = productRepository.findById(item.getProduct().getIdProd())
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec ID: " + item.getProduct().getIdProd()));

            BigDecimal qte = BigDecimal.valueOf(item.getQuantite());
            BigDecimal itemTotal;
            BigDecimal itemGain;

            // Handle default selling price
            if (item.getPrixVendu() == null || item.getPrixVendu().compareTo(BigDecimal.ZERO) == 0) {
                itemTotal = prod.getUnitPrice().multiply(qte);
                itemGain = BigDecimal.ZERO;
                item.setPrixVendu(prod.getUnitPrice()); // Update the item with actual selling price used
            } else {
                itemTotal = item.getPrixVendu().multiply(qte);
                itemGain = item.getPrixVendu().subtract(prod.getUnitPrice()).multiply(qte);
            }

            // Update DB Stock
            productService.retirerQuantite(prod.getIdProd(), item.getQuantite());

            item.setPrix(itemTotal);
            item.setGain(itemGain);
            item.setVente(vente);
        }

        Vente saved = venteRepo.save(vente);
        return mapVenteToDto(saved, "Vente enregistrée avec succès");
    }

    @Transactional(readOnly = true)
    public List<VenteDto> getAllVente() {
        List<Vente> allVente = this.venteRepo.findAll();
        if(allVente.isEmpty()){
            throw new EntityNotFoundException("No sales data found in DB!!");
        }
        return allVente.stream()
                .map(vente -> mapVenteToDto(vente, "Data fetched with success"))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VenteDto getVenteById(Long id) {
        Vente vente = venteRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Vente with ID " + id + " not found"));
        return mapVenteToDto(vente, "Vente fetched successfully");
    }

    @Transactional(readOnly = true)
    public List<VenteDto> getVentesByDate(LocalDate date) {
        List<Vente> ventes = venteRepo.findByDate(date);
        if (ventes.isEmpty()) {
            throw new EntityNotFoundException("No ventes found for date " + date);
        }
        return ventes.stream()
                .map(vente -> mapVenteToDto(vente, "Ventes fetched successfully"))
                .collect(Collectors.toList());
    }

    private VenteDto mapVenteToDto(Vente vente, String message) {
        VenteDto dto = new VenteDto();
        dto.setId(vente.getId());
        dto.setDate(vente.getDate());
        dto.setMessage(message);

        BigDecimal coutTotal = BigDecimal.ZERO;
        BigDecimal prixVenduTotal = BigDecimal.ZERO;
        BigDecimal totalGain = BigDecimal.ZERO;
        int totalItem = 0;

        List<VenteItemDetailDto> itemDetails = vente.getItems().stream().map(item -> {
            Product prod = productRepository.findById(item.getProduct().getIdProd())
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé"));

            BigDecimal qte = BigDecimal.valueOf(item.getQuantite());
            BigDecimal itemTotal;
            BigDecimal itemGain;
            BigDecimal appliedPrixVendu = item.getPrixVendu() != null ? item.getPrixVendu() : BigDecimal.ZERO;

            if (appliedPrixVendu.compareTo(BigDecimal.ZERO) == 0) {
                itemTotal = prod.getUnitPrice().multiply(qte);
                itemGain = BigDecimal.ZERO;
                appliedPrixVendu = prod.getUnitPrice();
            } else {
                itemTotal = appliedPrixVendu.multiply(qte);
                itemGain = appliedPrixVendu.subtract(prod.getUnitPrice()).multiply(qte);
            }

            return new VenteItemDetailDto(
                prod.getIdProd(),
                prod.getProdName(),
                item.getQuantite(),
                prod.getUnitPrice(),
                appliedPrixVendu,
                itemTotal,
                itemGain
            );
        }).collect(Collectors.toList());

        for (VenteItemDetailDto detail : itemDetails) {
            // Calculating the base cost (Unit Price * Qty)
            BigDecimal baseCost = detail.getPrixUnitaire().multiply(BigDecimal.valueOf(detail.getQuantite()));
            coutTotal = coutTotal.add(baseCost);
            
            prixVenduTotal = prixVenduTotal.add(detail.getTotal());
            totalGain = totalGain.add(detail.getGain());
            totalItem += detail.getQuantite();
        }

        dto.setCoutTotal(coutTotal);
        dto.setPrixVendu(prixVenduTotal);
        dto.setGain(totalGain);
        dto.setTotalItem(totalItem);
        dto.setItems(itemDetails);

        return dto;
    }
}