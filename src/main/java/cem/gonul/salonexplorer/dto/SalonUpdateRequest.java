package cem.gonul.salonexplorer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
        @Pattern(regexp = "^$|^[+]?[0-9\\s().-]{7,30}$", message = "Phone number format is invalid")
        String phone,

        @Size(max = 500)
        String websiteUrl,

        @Size(max = 500)
        String socialMediaUrl,

        String services,

        @Size(max = 100)
        String priceRange
) {
}
