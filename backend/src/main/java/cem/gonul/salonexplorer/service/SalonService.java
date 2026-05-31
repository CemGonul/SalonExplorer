package cem.gonul.salonexplorer.service;

import cem.gonul.salonexplorer.dto.SalonDetailResponse;
import cem.gonul.salonexplorer.dto.SalonListResponse;
import cem.gonul.salonexplorer.dto.SalonUpdateRequest;
import cem.gonul.salonexplorer.entity.Salon;
import cem.gonul.salonexplorer.exception.BadRequestException;
import cem.gonul.salonexplorer.exception.NotFoundException;
import cem.gonul.salonexplorer.mapper.SalonMapper;
import cem.gonul.salonexplorer.repository.SalonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalonService {

    private final SalonRepository salonRepository;
    private final SalonMapper salonMapper;

    @Transactional(readOnly = true)
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
            responses.add(salonMapper.toListResponse(salon));
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public SalonDetailResponse getSalonById(Long id) {
        Salon salon = findSalon(id);
        return salonMapper.toDetailResponse(salon);
    }

    @Transactional(readOnly = true)
    public List<String> getDistricts() {
        return salonRepository.findDistinctDistricts();
    }

    @Transactional
    public SalonDetailResponse updateSalon(Long id, SalonUpdateRequest request) {
        Salon salon = findSalon(id);

        if (salonRepository.existsByNameAndAddressAndIdNot(request.name(), request.address(), id)) {
            throw new BadRequestException("Another salon already exists with this name and address.");
        }

        salon.setName(request.name());
        salon.setAddress(request.address());
        salon.setWebsite(request.website());

        Salon savedSalon = salonRepository.save(salon);
        return salonMapper.toDetailResponse(savedSalon);
    }

    private Salon findSalon(Long id) {
        return salonRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Salon not found with id: " + id));
    }

    private Sort createSort(String sortBy, String direction) {
        if (sortBy == null || sortBy.isBlank()) {
            return Sort.unsorted();
        }

        String property = resolveSortProperty(sortBy);
        Sort.Direction sortDirection = resolveDirection(direction, property);

        return Sort.by(sortDirection, property);
    }

    private String resolveSortProperty(String sortBy) {
        String normalizedSortBy = sortBy.trim().toLowerCase();

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

        String normalizedDirection = direction.trim().toLowerCase();

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

}
