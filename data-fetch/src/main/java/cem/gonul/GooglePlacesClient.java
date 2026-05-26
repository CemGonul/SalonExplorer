package cem.gonul;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

public class GooglePlacesClient {

    private static final String API_URL = "https://places.googleapis.com/v1/places:searchText";
    private static final String FIELD_MASK = String.join(",",
            "places.displayName",
            "places.formattedAddress",
            "places.nationalPhoneNumber",
            "places.websiteUri",
            "places.priceLevel",
            "places.rating",
            "places.userRatingCount",
            "nextPageToken"
    );

    private final String apiKey;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public GooglePlacesClient(ObjectMapper objectMapper) {
        this.apiKey = System.getenv("GOOGLE_PLACES_API_KEY");
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GOOGLE_PLACES_API_KEY environment variable is not set.");
        }
    }

    public SearchResponse searchText(String query, String pageToken) throws IOException, InterruptedException {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("textQuery", query);
        body.put("pageSize", 20);

        if (pageToken != null && !pageToken.isBlank()) {
            body.put("pageToken", pageToken);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(40))
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", FIELD_MASK)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Google Places request failed with status "
                    + response.statusCode() + ": " + response.body());
        }

        return objectMapper.readValue(response.body(), SearchResponse.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SearchResponse(List<Place> places, String nextPageToken) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Place(
            DisplayName displayName,
            String formattedAddress,
            String nationalPhoneNumber,
            String websiteUri,
            String priceLevel,
            BigDecimal rating,
            Integer userRatingCount
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DisplayName(String text) {
    }
}
