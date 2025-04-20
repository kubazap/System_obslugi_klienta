package pl.zapala.system_obslugi_klienta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zapala.system_obslugi_klienta.models.Pracownik;

public interface PracownikRepository extends JpaRepository<Pracownik, Integer> {
    public Pracownik findByEmail(String email);
}
