package cem.gonul;

import java.math.BigDecimal;

public record SalonRecord(
        String name,
        String address,
        String district,
        String phoneNumber,
        String website,
        String services,
        String priceRange,
        BigDecimal rating,
        Integer reviewCount
) {
}
