package cem.gonul.salonexplorer.controller;

import cem.gonul.salonexplorer.dto.SalonDetailResponse;
import cem.gonul.salonexplorer.dto.SalonListResponse;
import cem.gonul.salonexplorer.dto.SalonUpdateRequest;
import cem.gonul.salonexplorer.service.SalonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/salons")
@RequiredArgsConstructor
public class SalonController {

    private final SalonService salonService;

    @GetMapping
    public List<SalonListResponse> getSalons(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction
    ) {
        return salonService.getSalons(district, sortBy, direction);
    }

    @GetMapping("/districts")
    public List<String> getDistricts() {
        return salonService.getDistricts();
    }

    @GetMapping("/{id}")
    public SalonDetailResponse getSalonById(@PathVariable Long id) {
        return salonService.getSalonById(id);
    }

    @PutMapping("/{id}")
    public SalonDetailResponse updateSalon(
            @PathVariable Long id,
            @Valid @RequestBody SalonUpdateRequest request
    ) {
        return salonService.updateSalon(id, request);
    }
}
