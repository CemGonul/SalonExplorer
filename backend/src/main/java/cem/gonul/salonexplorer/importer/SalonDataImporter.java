package cem.gonul.salonexplorer.importer;

import cem.gonul.salonexplorer.entity.Salon;
import cem.gonul.salonexplorer.repository.SalonRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app.import-data", name = "enabled", havingValue = "true")
public class SalonDataImporter implements CommandLineRunner {

    private final SalonRepository salonRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.import-data.path}")
    private Resource dataFile;

    @Override
    public void run(String... args) throws Exception {
        List<SalonImportRecord> records;

        try (InputStream inputStream = dataFile.getInputStream()) {
            records = objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        }

        int skippedCount = 0;
        List<Salon> salonsToSave = new ArrayList<>();

        for (SalonImportRecord record : records) {
            if (salonRepository.existsByNameAndAddress(record.name(), record.address())) {
                skippedCount++;
                continue;
            }

            salonsToSave.add(toSalon(record));
        }

        salonRepository.saveAll(salonsToSave);

        log.info("Salon import finished. Imported: {}, skipped duplicates: {}", salonsToSave.size(), skippedCount);
    }

    private Salon toSalon(SalonImportRecord record) {
        Salon salon = new Salon();
        salon.setName(record.name());
        salon.setAddress(record.address());
        salon.setDistrict(record.district());
        salon.setPhoneNumber(record.phoneNumber());
        salon.setWebsite(record.website());
        salon.setServices(record.services());
        salon.setPriceRange(record.priceRange());
        salon.setRating(record.rating());
        salon.setReviewCount(record.reviewCount());

        return salon;
    }
}
