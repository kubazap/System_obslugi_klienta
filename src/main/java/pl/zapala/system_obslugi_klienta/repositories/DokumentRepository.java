package pl.zapala.system_obslugi_klienta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zapala.system_obslugi_klienta.models.Dokument;

public interface DokumentRepository extends JpaRepository<Dokument, Integer> {

}
