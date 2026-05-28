package cem.gonul.salonexplorer.repository;

import cem.gonul.salonexplorer.entity.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SalonRepository extends JpaRepository<Salon, Long> {

    boolean existsByNameAndAddress(String name, String address);

    List<Salon> findByDistrictIgnoreCase(String district, Sort sort);
}
