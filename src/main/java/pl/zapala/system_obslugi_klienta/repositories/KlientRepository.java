package pl.zapala.system_obslugi_klienta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zapala.system_obslugi_klienta.models.Klient;

public interface KlientRepository extends JpaRepository<Klient, Integer> {

    /**
     * Wyszukuje klienta na podstawie unikalnego adresu e-mail.
     *
     * @param email adres e-mail klienta
     * @return encja Klient lub null, jeśli żaden klient o podanym e-mailu nie istnieje
     */
    public Klient findByEmail(String email);
}
