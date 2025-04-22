package pl.zapala.system_obslugi_klienta.services;

import dev.samstevens.totp.secret.SecretGenerator;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;

@Service
public class PracownikService implements UserDetailsService {

    private final PracownikRepository repo;
    private final SecretGenerator secretGen;

    public PracownikService(PracownikRepository repo, SecretGenerator secretGen) {
        this.repo = repo;
        this.secretGen = secretGen;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Pracownik p = repo.findByEmail(email);
        if (p == null) throw new UsernameNotFoundException(email);

        if (p.getTotpSecret() == null) {
            p.setTotpSecret(secretGen.generate());
            repo.save(p);
        }

        return User.withUsername(p.getEmail())
                .password(p.getHaslo())
                .authorities("ROLE_USER")
                .build();
    }
}
