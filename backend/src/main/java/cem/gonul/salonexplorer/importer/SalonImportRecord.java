package cem.gonul.salonexplorer.importer;

import java.math.BigDecimal;

public record SalonImportRecord(
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
