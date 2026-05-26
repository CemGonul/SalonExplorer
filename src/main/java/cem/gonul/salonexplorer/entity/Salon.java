package cem.gonul.salonexplorer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "salons")
@Getter
@Setter
@NoArgsConstructor
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "google_place_id", unique = true)
    private String googlePlaceId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(length = 100)
    private String phone;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "social_media_url", length = 500)
    private String socialMediaUrl;

    @Column(columnDefinition = "TEXT")
    private String services;

    @Column(name = "price_range", length = 100)
    private String priceRange;

    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "review_count")
    private Integer reviewCount;

    private Double latitude;

    private Double longitude;

    @Column(name = "google_maps_url", length = 500)
    private String googleMapsUrl;

    @Column(name = "business_status", length = 100)
    private String businessStatus;
}
