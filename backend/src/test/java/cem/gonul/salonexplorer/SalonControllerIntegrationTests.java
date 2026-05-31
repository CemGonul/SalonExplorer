package cem.gonul.salonexplorer;

import cem.gonul.salonexplorer.entity.Salon;
import cem.gonul.salonexplorer.repository.SalonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SalonControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SalonRepository salonRepository;

    private Salon mokotowLowRating;
    private Salon mokotowHighRating;
    private Salon wolaSalon;

    @BeforeEach
    void setUp() {
        salonRepository.deleteAll();

        mokotowLowRating = salonRepository.save(createSalon(
                "Mokotow Beauty",
                "Pulawska 10, Warsaw",
                "Mokotów",
                "500 111 222",
                "https://mokotow.example.com",
                "Beauty Salon",
                new BigDecimal("4.2"),
                30
        ));

        mokotowHighRating = salonRepository.save(createSalon(
                "Mokotow Nails",
                "Rakowiecka 20, Warsaw",
                "Mokotów",
                "500 333 444",
                "https://nails.example.com",
                "Nail Salon",
                new BigDecimal("4.9"),
                90
        ));

        wolaSalon = salonRepository.save(createSalon(
                "Wola Spa",
                "Prosta 5, Warsaw",
                "Wola",
                null,
                null,
                "Spa Salon",
                new BigDecimal("4.6"),
                55
        ));
    }

    @Test
    void returnsSalonList() throws Exception {
        mockMvc.perform(get("/api/salons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].district").exists());
    }

    @Test
    void filtersByDistrictAndSortsByRatingDesc() throws Exception {
        mockMvc.perform(get("/api/salons")
                        .param("district", "Mokotów")
                        .param("sortBy", "rating")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value(mokotowHighRating.getName()))
                .andExpect(jsonPath("$[1].name").value(mokotowLowRating.getName()));
    }

    @Test
    void returnsDistrictsFromDatabase() throws Exception {
        mockMvc.perform(get("/api/salons/districts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value("Mokotów"))
                .andExpect(jsonPath("$[1]").value("Wola"));
    }

    @Test
    void returnsSalonDetailsById() throws Exception {
        mockMvc.perform(get("/api/salons/{id}", wolaSalon.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(wolaSalon.getName()))
                .andExpect(jsonPath("$.address").value(wolaSalon.getAddress()))
                .andExpect(jsonPath("$.district").value(wolaSalon.getDistrict()))
                .andExpect(jsonPath("$.services").value(wolaSalon.getServices()));
    }

    @Test
    void updatesSalonDetailsAndPersistsChanges() throws Exception {
        String requestBody = """
                {
                  "name": "Updated Wola Spa",
                  "address": "Updated Street 7, Warsaw",
                  "website": "https://updated.example.com"
                }
                """;

        mockMvc.perform(put("/api/salons/{id}", wolaSalon.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Wola Spa"))
                .andExpect(jsonPath("$.address").value("Updated Street 7, Warsaw"))
                .andExpect(jsonPath("$.website").value("https://updated.example.com"));

        Salon updatedSalon = salonRepository.findById(wolaSalon.getId()).orElseThrow();

        org.assertj.core.api.Assertions.assertThat(updatedSalon.getName()).isEqualTo("Updated Wola Spa");
        org.assertj.core.api.Assertions.assertThat(updatedSalon.getAddress()).isEqualTo("Updated Street 7, Warsaw");
        org.assertj.core.api.Assertions.assertThat(updatedSalon.getWebsite()).isEqualTo("https://updated.example.com");
        org.assertj.core.api.Assertions.assertThat(updatedSalon.getDistrict()).isEqualTo("Wola");
        org.assertj.core.api.Assertions.assertThat(updatedSalon.getRating()).isEqualByComparingTo(new BigDecimal("4.6"));
        org.assertj.core.api.Assertions.assertThat(updatedSalon.getReviewCount()).isEqualTo(55);
    }

    @Test
    void returnsBadRequestForInvalidSortField() throws Exception {
        mockMvc.perform(get("/api/salons")
                        .param("sortBy", "unknown"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported sortBy value: unknown. Allowed values: name, rating, reviewCount."));
    }

    @Test
    void returnsBadRequestWhenUpdatingToDuplicateSalon() throws Exception {
        String requestBody = """
                {
                  "name": "Mokotow Nails",
                  "address": "Rakowiecka 20, Warsaw",
                  "website": "https://duplicate.example.com"
                }
                """;

        mockMvc.perform(put("/api/salons/{id}", wolaSalon.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Another salon already exists with this name and address."));
    }

    @Test
    void returnsBadRequestForInvalidUpdateRequest() throws Exception {
        String requestBody = """
                {
                  "name": "",
                  "address": "Updated Street 7, Warsaw",
                  "website": "https://updated.example.com"
                }
                """;

        mockMvc.perform(put("/api/salons/{id}", wolaSalon.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").exists());
    }

    private Salon createSalon(
            String name,
            String address,
            String district,
            String phoneNumber,
            String website,
            String services,
            BigDecimal rating,
            Integer reviewCount
    ) {
        Salon salon = new Salon();
        salon.setName(name);
        salon.setAddress(address);
        salon.setDistrict(district);
        salon.setPhoneNumber(phoneNumber);
        salon.setWebsite(website);
        salon.setServices(services);
        salon.setRating(rating);
        salon.setReviewCount(reviewCount);

        return salon;
    }
}
