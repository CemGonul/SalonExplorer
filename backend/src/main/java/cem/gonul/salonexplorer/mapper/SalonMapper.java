package cem.gonul.salonexplorer.mapper;

import cem.gonul.salonexplorer.dto.SalonDetailResponse;
import cem.gonul.salonexplorer.dto.SalonListResponse;
import cem.gonul.salonexplorer.entity.Salon;
import org.springframework.stereotype.Component;

@Component
public class SalonMapper {

    public SalonListResponse toListResponse(Salon salon) {
        return new SalonListResponse(
                salon.getId(),
                salon.getName(),
                salon.getDistrict(),
                salon.getRating(),
                salon.getReviewCount(),
                salon.getPriceRange()
        );
    }

    public SalonDetailResponse toDetailResponse(Salon salon) {
        return new SalonDetailResponse(
                salon.getId(),
                salon.getName(),
                salon.getAddress(),
                salon.getDistrict(),
                salon.getPhoneNumber(),
                salon.getWebsite(),
                salon.getServices(),
                salon.getPriceRange(),
                salon.getRating(),
                salon.getReviewCount()
        );
    }
}
