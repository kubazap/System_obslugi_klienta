package pl.zapala.system_obslugi_klienta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zapala.system_obslugi_klienta.models.Dokument;

import java.util.List;

public interface RepozytoriumDokumentow extends JpaRepository<Dokument, Integer> {
    List<Dokument> findAllByParentId(Integer parentId);
}
