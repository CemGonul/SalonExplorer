package cem.gonul.salonexplorer.dto;

import java.math.BigDecimal;

public record SalonListResponse(
        Long id,
        String name,
        String district,
        BigDecimal rating,
        Integer reviewCount,
        String priceRange
) {
}
