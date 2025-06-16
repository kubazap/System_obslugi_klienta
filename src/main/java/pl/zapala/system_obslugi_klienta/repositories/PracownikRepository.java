package pl.zapala.system_obslugi_klienta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zapala.system_obslugi_klienta.models.Pracownik;

public interface PracownikRepository extends JpaRepository<Pracownik, Integer> {

    /**
     * Wyszukuje pracownika na podstawie unikalnego adresu e-mail.
     *
     * @param email adres e-mail pracownika
     * @return encja Pracownik lub null, jeśli nie znaleziono pracownika o podanym adresie
     */
    Pracownik findByEmail(String email);
}
