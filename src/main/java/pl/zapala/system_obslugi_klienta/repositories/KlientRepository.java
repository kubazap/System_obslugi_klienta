package pl.zapala.system_obslugi_klienta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zapala.system_obslugi_klienta.models.Klient;

public interface KlientRepository extends JpaRepository<Klient, Integer> {
    public Klient findByEmail(String email);
}
