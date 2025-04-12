package pl.zapala.system_obslugi_klienta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zapala.system_obslugi_klienta.models.Plik;

public interface RepozytoriumPlikow extends JpaRepository<Plik, Integer> {

}
