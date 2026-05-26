package cem.gonul.salonexplorer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record SalonUpdateRequest(
        @NotBlank
        @Size(max = 255)
        String name,

        @NotBlank
        @Size(max = 500)
        String address,

        @NotBlank
        @Size(max = 100)
        String district,

        @Size(max = 100)
        String phone,

        @Size(max = 500)
        String websiteOrSocialUrl,

        String services,

        @Size(max = 100)
        String priceRange,

        BigDecimal rating,

        Integer reviewCount
) {
}
