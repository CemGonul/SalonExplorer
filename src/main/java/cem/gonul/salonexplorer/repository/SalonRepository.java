package cem.gonul.salonexplorer.repository;

import cem.gonul.salonexplorer.entity.Salon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonRepository extends JpaRepository<Salon, Long> {
}
