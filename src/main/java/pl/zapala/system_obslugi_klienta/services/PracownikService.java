package pl.zapala.system_obslugi_klienta.services;

import dev.samstevens.totp.secret.SecretGenerator;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;

/**
 * Serwis zarządzający Pracownikami, implementujący ładowanie danych użytkownika
 * potrzebnych do uwierzytelniania w Spring Security oraz generowanie sekretu TOTP przy pierwszym logowaniu.
 */
@Service
public class PracownikService implements UserDetailsService {

    private final PracownikRepository repo;
    private final SecretGenerator secretGen;

    /**
     * Konstruktor serwisu PracownikService.
     *
     * @param repo      repozytorium Pracownik umożliwiające dostęp do danych pracowników
     * @param secretGen generator sekretu TOTP dla dwuskładnikowego uwierzytelniania
     */
    public PracownikService(PracownikRepository repo, SecretGenerator secretGen) {
        this.repo = repo;
        this.secretGen = secretGen;
    }

    /**
     * Ładuje użytkownika na podstawie podanego adresu e-mail.
     * <p>
     * Jeśli pracownik nie ma jeszcze wygenerowanego sekretu TOTP,
     * generuje go i zapisuje w repozytorium.
     * Następnie zwraca obiekt UserDetails.
     *
     * @param email e-mail pracownika używany jako unikalny identyfikator użytkownika
     * @return obiekt UserDetails zawierający nazwę użytkownika oraz hasło
     * @throws UsernameNotFoundException gdy pracownik o podanym e-mailu nie zostanie znaleziony
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Pracownik p = repo.findByEmail(email);
        if (p == null) {
            throw new UsernameNotFoundException(email);
        }

        // Generowanie sekretu TOTP, jeśli nie istnieje
        if (p.getTotpSecret() == null) {
            String secret = secretGen.generate();
            p.setTotpSecret(secret);
            repo.save(p);
        }

        // Tworzenie obiektu UserDetails dla Spring Security
        return User.withUsername(p.getEmail())
                .password(p.getHaslo())
                .authorities("ROLE_USER")
                .build();
    }
}