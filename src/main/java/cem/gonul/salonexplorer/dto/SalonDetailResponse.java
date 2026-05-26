package cem.gonul.salonexplorer.dto;

import java.math.BigDecimal;

public record SalonDetailResponse(
        Long id,
        String googlePlaceId,
        String name,
        String address,
        String district,
        String phone,
        String websiteUrl,
        String socialMediaUrl,
        String services,
        String priceRange,
        BigDecimal rating,
        Integer reviewCount,
        Double latitude,
        Double longitude,
        String googleMapsUrl,
        String businessStatus
) {
}
