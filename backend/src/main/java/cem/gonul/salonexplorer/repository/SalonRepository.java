package cem.gonul.salonexplorer.repository;

import cem.gonul.salonexplorer.entity.Salon;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SalonRepository extends JpaRepository<Salon, Long> {

    boolean existsByNameAndAddress(String name, String address);

    boolean existsByNameAndAddressAndIdNot(String name, String address, Long id);

    List<Salon> findByDistrictIgnoreCase(String district, Sort sort);

    @Query("select distinct salon.district from Salon salon order by salon.district")
    List<String> findDistinctDistricts();
}
