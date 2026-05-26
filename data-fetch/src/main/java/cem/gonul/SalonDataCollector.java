package cem.gonul;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SalonDataCollector {

    private static final List<SearchQuery> SEARCH_QUERIES = List.of(
            new SearchQuery("Mokotów", "beauty salon Warsaw Mokotow", "Beauty Salon"),
            new SearchQuery("Śródmieście", "beauty salon Warsaw Srodmiescie", "Beauty Salon"),
            new SearchQuery("Wola", "beauty salon Warsaw Wola", "Beauty Salon"),
            new SearchQuery("Mokotów", "spa salon Warsaw Mokotow", "Spa Salon"),
            new SearchQuery("Śródmieście", "spa salon Warsaw Srodmiescie", "Spa Salon"),
            new SearchQuery("Wola", "spa salon Warsaw Wola", "Spa Salon"),
            new SearchQuery("Mokotów", "nail salon Warsaw Mokotow", "Nail Salon"),
            new SearchQuery("Śródmieście", "nail salon Warsaw Srodmiescie", "Nail Salon"),
            new SearchQuery("Wola", "nail salon Warsaw Wola", "Nail Salon")
    );

    private final ObjectMapper objectMapper;
    private final GooglePlacesClient googlePlacesClient;
    private final Map<String, SalonRecord> salonsByNameAndAddress = new LinkedHashMap<>();
    private final Map<String, Integer> countByDistrict = new LinkedHashMap<>();
    private final Map<String, Integer> countByQuery = new LinkedHashMap<>();

    public SalonDataCollector() {
        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        this.googlePlacesClient = new GooglePlacesClient(objectMapper);
    }

    public void collect(int targetCount) throws Exception {
        int targetPerDistrict = (int) Math.ceil((double) targetCount / countDistricts());
        int targetPerQuery = (int) Math.ceil((double) targetPerDistrict / countQueriesPerDistrict());
        Path outputDir = Path.of("output");
        Path rawOutputDir = outputDir.resolve("raw");
        Path cleanedOutputPath = outputDir.resolve("salons_cleaned.json");

        Files.createDirectories(rawOutputDir);

        System.out.println("Target salons: " + targetCount);
        System.out.println("Target per district: " + targetPerDistrict);
        System.out.println("Target per query: " + targetPerQuery);

        int requestNumber = 0;

        for (SearchQuery searchQuery : SEARCH_QUERIES) {
            if (salonsByNameAndAddress.size() >= targetCount) {
                break;
            }
            if (getDistrictCount(searchQuery.district()) >= targetPerDistrict) {
                continue;
            }
            if (getQueryCount(searchQuery.query()) >= targetPerQuery) {
                continue;
            }

            String pageToken = null;
            int pageNumber = 1;

            do {
                if (salonsByNameAndAddress.size() >= targetCount) {
                    break;
                }
                if (getDistrictCount(searchQuery.district()) >= targetPerDistrict) {
                    break;
                }
                if (getQueryCount(searchQuery.query()) >= targetPerQuery) {
                    break;
                }

                requestNumber++;
                System.out.println("[" + requestNumber + "] " + searchQuery.query() + " page " + pageNumber);

                GooglePlacesClient.SearchResponse response = googlePlacesClient.searchText(
                        searchQuery.query(),
                        pageToken
                );

                saveRawResponse(rawOutputDir, searchQuery.query(), pageNumber, response);
                addPlaces(response.places(), searchQuery, targetPerDistrict, targetPerQuery);

                System.out.println("Collected unique salons: " + salonsByNameAndAddress.size());
                System.out.println(searchQuery.district() + " salons: " + getDistrictCount(searchQuery.district())
                        + "/" + targetPerDistrict);
                System.out.println(searchQuery.query() + " salons: " + getQueryCount(searchQuery.query())
                        + "/" + targetPerQuery);

                pageToken = response.nextPageToken();
                pageNumber++;

                if (pageToken != null && !pageToken.isBlank()) {
                    Thread.sleep(2000);
                }
            } while (pageToken != null && !pageToken.isBlank());
        }

        List<SalonRecord> cleanedSalons = new ArrayList<>(salonsByNameAndAddress.values());
        objectMapper.writeValue(cleanedOutputPath.toFile(), cleanedSalons);

        printFinalSummary();
        System.out.println("Done. Saved " + cleanedSalons.size() + " unique salons to " + cleanedOutputPath);
    }

    private void printFinalSummary() {
        System.out.println("Final district counts:");

        for (String district : getDistricts()) {
            System.out.println(district + ": " + getDistrictCount(district));
        }
    }

    private void saveRawResponse(
            Path rawOutputDir,
            String query,
            int pageNumber,
            GooglePlacesClient.SearchResponse response
    ) throws IOException {
        String safeQueryName = query.replaceAll("[^a-zA-Z0-9]+", "-")
                .replaceAll("^-|-$", "")
                .toLowerCase();
        Path rawPath = rawOutputDir.resolve(safeQueryName + "-page-" + pageNumber + ".json");
        objectMapper.writeValue(rawPath.toFile(), response);
    }

    private void addPlaces(
            List<GooglePlacesClient.Place> places,
            SearchQuery searchQuery,
            int targetPerDistrict,
            int targetPerQuery
    ) {
        if (places == null) {
            return;
        }

        for (GooglePlacesClient.Place place : places) {
            if (getDistrictCount(searchQuery.district()) >= targetPerDistrict) {
                break;
            }
            if (getQueryCount(searchQuery.query()) >= targetPerQuery) {
                break;
            }
            addPlace(place, searchQuery);
        }
    }

    private void addPlace(GooglePlacesClient.Place place, SearchQuery searchQuery) {
        if (place.displayName() == null || place.displayName().text() == null
                || place.formattedAddress() == null) {
            return;
        }

        String name = place.displayName().text();
        String address = place.formattedAddress();
        String nameAddressKey = (name + "|" + address).toLowerCase();

        if (salonsByNameAndAddress.containsKey(nameAddressKey)) {
            SalonRecord existingSalon = salonsByNameAndAddress.get(nameAddressKey);
            salonsByNameAndAddress.put(nameAddressKey, new SalonRecord(
                    existingSalon.name(),
                    existingSalon.address(),
                    existingSalon.district(),
                    existingSalon.phoneNumber(),
                    existingSalon.website(),
                    mergeServices(existingSalon.services(), searchQuery.service()),
                    existingSalon.priceRange(),
                    existingSalon.rating(),
                    existingSalon.reviewCount()
            ));
            return;
        }

        salonsByNameAndAddress.put(nameAddressKey, new SalonRecord(
                name,
                address,
                searchQuery.district(),
                getPhone(place),
                place.websiteUri(),
                searchQuery.service(),
                convertPriceLevel(place.priceLevel()),
                place.rating(),
                place.userRatingCount()
        ));
        countByDistrict.put(searchQuery.district(), getDistrictCount(searchQuery.district()) + 1);
        countByQuery.put(searchQuery.query(), getQueryCount(searchQuery.query()) + 1);
    }

    private String getPhone(GooglePlacesClient.Place place) {
        if (place.nationalPhoneNumber() != null && !place.nationalPhoneNumber().isBlank()) {
            return place.nationalPhoneNumber();
        }

        return null;
    }

    private String mergeServices(String existingServices, String newService) {
        if (newService == null || newService.isBlank()) {
            return existingServices;
        }
        if (existingServices == null || existingServices.isBlank()) {
            return newService;
        }

        String[] services = existingServices.split(",");

        for (String service : services) {
            if (service.trim().equalsIgnoreCase(newService)) {
                return existingServices;
            }
        }

        return existingServices + ", " + newService;
    }

    private String convertPriceLevel(String priceLevel) {
        if (priceLevel == null || priceLevel.isBlank()) {
            return null;
        }

        if ("PRICE_LEVEL_FREE".equals(priceLevel)) {
            return "Free";
        }
        if ("PRICE_LEVEL_INEXPENSIVE".equals(priceLevel)) {
            return "Inexpensive";
        }
        if ("PRICE_LEVEL_MODERATE".equals(priceLevel)) {
            return "Moderate";
        }
        if ("PRICE_LEVEL_EXPENSIVE".equals(priceLevel)) {
            return "Expensive";
        }
        if ("PRICE_LEVEL_VERY_EXPENSIVE".equals(priceLevel)) {
            return "Very expensive";
        }

        return priceLevel;
    }

    private int getDistrictCount(String district) {
        return countByDistrict.getOrDefault(district, 0);
    }

    private int getQueryCount(String query) {
        return countByQuery.getOrDefault(query, 0);
    }

    private int countDistricts() {
        return getDistricts().size();
    }

    private List<String> getDistricts() {
        List<String> districts = new ArrayList<>();

        for (SearchQuery searchQuery : SEARCH_QUERIES) {
            if (!districts.contains(searchQuery.district())) {
                districts.add(searchQuery.district());
            }
        }

        return districts;
    }

    private int countQueriesPerDistrict() {
        int firstDistrictQueryCount = 0;
        String firstDistrict = SEARCH_QUERIES.get(0).district();

        for (SearchQuery searchQuery : SEARCH_QUERIES) {
            if (firstDistrict.equals(searchQuery.district())) {
                firstDistrictQueryCount++;
            }
        }

        return firstDistrictQueryCount;
    }

    private record SearchQuery(String district, String query, String service) {
    }
}
