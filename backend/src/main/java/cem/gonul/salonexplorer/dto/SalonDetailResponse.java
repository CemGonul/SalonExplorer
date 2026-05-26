package cem.gonul.salonexplorer.dto;

import java.math.BigDecimal;

public record SalonDetailResponse(
        Long id,
        String name,
        String address,
        String district,
        String phone,
        String websiteOrSocialUrl,
        String services,
        String priceRange,
        BigDecimal rating,
        Integer reviewCount
) {
}
