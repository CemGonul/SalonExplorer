package cem.gonul.salonexplorer.service;

import cem.gonul.salonexplorer.dto.SalonDetailResponse;
import cem.gonul.salonexplorer.dto.SalonListResponse;
import cem.gonul.salonexplorer.dto.SalonUpdateRequest;
import cem.gonul.salonexplorer.entity.Salon;
import cem.gonul.salonexplorer.exception.BadRequestException;
import cem.gonul.salonexplorer.exception.NotFoundException;
import cem.gonul.salonexplorer.repository.SalonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SalonService {

    private final SalonRepository salonRepository;

    public List<SalonListResponse> getSalons(String district, String sortBy, String direction) {
        Sort sort = createSort(sortBy, direction);
        List<Salon> salons;

        if (district != null && !district.isBlank()) {
            salons = salonRepository.findByDistrictIgnoreCase(district.trim(), sort);
        } else {
            salons = salonRepository.findAll(sort);
        }

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
        salon.setPhoneNumber(request.phoneNumber());
        salon.setWebsite(request.website());
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
                salon.getReviewCount(),
                salon.getPriceRange()
        );
    }

    private Sort createSort(String sortBy, String direction) {
        String property = resolveSortProperty(sortBy);
        Sort.Direction sortDirection = resolveDirection(direction, property);

        return Sort.by(sortDirection, property);
    }

    private String resolveSortProperty(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "name";
        }

        String normalizedSortBy = sortBy.trim().toLowerCase(Locale.ROOT);

        if ("name".equals(normalizedSortBy)) {
            return "name";
        }
        if ("rating".equals(normalizedSortBy)) {
            return "rating";
        }
        if ("reviewcount".equals(normalizedSortBy)) {
            return "reviewCount";
        }

        throw new BadRequestException("Unsupported sortBy value: " + sortBy
                + ". Allowed values: name, rating, reviewCount.");
    }

    private Sort.Direction resolveDirection(String direction, String property) {
        if (direction == null || direction.isBlank()) {
            return getDefaultDirection(property);
        }

        String normalizedDirection = direction.trim().toLowerCase(Locale.ROOT);

        if ("asc".equals(normalizedDirection)) {
            return Sort.Direction.ASC;
        }
        if ("desc".equals(normalizedDirection)) {
            return Sort.Direction.DESC;
        }

        throw new BadRequestException("Unsupported direction value: " + direction
                + ". Allowed values: asc, desc.");
    }

    private Sort.Direction getDefaultDirection(String property) {
        if ("rating".equals(property) || "reviewCount".equals(property)) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    private SalonDetailResponse toDetailResponse(Salon salon) {
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
