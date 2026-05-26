package cem.gonul.salonexplorer.service;

import cem.gonul.salonexplorer.dto.SalonDetailResponse;
import cem.gonul.salonexplorer.dto.SalonListResponse;
import cem.gonul.salonexplorer.dto.SalonUpdateRequest;
import cem.gonul.salonexplorer.entity.Salon;
import cem.gonul.salonexplorer.exception.NotFoundException;
import cem.gonul.salonexplorer.repository.SalonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalonService {

    private final SalonRepository salonRepository;

    public List<SalonListResponse> getAllSalons() {
        List<Salon> salons = salonRepository.findAll();
        List<SalonListResponse> responses = new ArrayList<>();

        for (Salon salon : salons) {
            responses.add(toListResponse(salon));
        }

        return responses;
    }

    public SalonDetailResponse getSalonById(Long id) {
        Salon salon = findSalon(id);
        return toDetailResponse(salon);
    }

    public SalonDetailResponse updateSalon(Long id, SalonUpdateRequest request) {
        Salon salon = findSalon(id);

        salon.setName(request.name());
        salon.setAddress(request.address());
        salon.setDistrict(request.district());
        salon.setPhone(request.phone());
        salon.setWebsiteOrSocialUrl(request.websiteOrSocialUrl());
        salon.setServices(request.services());
        salon.setPriceRange(request.priceRange());
        salon.setRating(request.rating());
        salon.setReviewCount(request.reviewCount());

        Salon savedSalon = salonRepository.save(salon);
        return toDetailResponse(savedSalon);
    }

    private Salon findSalon(Long id) {
        return salonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Salon not found with id: " + id));
    }

    private SalonListResponse toListResponse(Salon salon) {
        return new SalonListResponse(
                salon.getId(),
                salon.getName(),
                salon.getDistrict(),
                salon.getRating(),
                salon.getPriceRange()
        );
    }

    private SalonDetailResponse toDetailResponse(Salon salon) {
        return new SalonDetailResponse(
                salon.getId(),
                salon.getName(),
                salon.getAddress(),
                salon.getDistrict(),
                salon.getPhone(),
                salon.getWebsiteOrSocialUrl(),
                salon.getServices(),
                salon.getPriceRange(),
                salon.getRating(),
                salon.getReviewCount()
        );
    }
}
