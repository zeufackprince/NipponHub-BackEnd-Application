package com.nipponhub.nipponhubv0.DTO;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AchatItemDetailDto {
    private Long productId;
    private String productName;
    private int quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal total;
}
