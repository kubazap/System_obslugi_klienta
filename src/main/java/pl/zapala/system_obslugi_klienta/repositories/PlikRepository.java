package pl.zapala.system_obslugi_klienta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zapala.system_obslugi_klienta.models.Plik;

public interface PlikRepository extends JpaRepository<Plik, Integer> {

}
