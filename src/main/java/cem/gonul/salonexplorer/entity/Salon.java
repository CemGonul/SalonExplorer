package cem.gonul.salonexplorer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "salons",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_salons_name_address",
                columnNames = {"name", "address"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(length = 100)
    private String phone;

    @Column(name = "website_or_social_url", length = 500)
    private String websiteOrSocialUrl;

    @Column(columnDefinition = "TEXT")
    private String services;

    @Column(name = "price_range", length = 100)
    private String priceRange;

    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "review_count")
    private Integer reviewCount;
}
