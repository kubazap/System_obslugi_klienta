package pl.zapala.system_obslugi_klienta.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;

@Service
public class PracownikService implements UserDetailsService {
    @Autowired
    private PracownikRepository pracownicyRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Pracownik pracownik = pracownicyRepo.findByEmail(email);

        if (pracownik != null) {
            var springUser = User.withUsername(pracownik.getEmail())
                    .password(pracownik.getHaslo())
                    .build();
            return springUser;
        }

        return null;
    }
}