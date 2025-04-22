package pl.zapala.system_obslugi_klienta.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;

import java.io.IOException;
import java.util.List;

@Component
public class MfaCompletionFilter extends OncePerRequestFilter {

    private final PracownikRepository repo;

    public MfaCompletionFilter(PracownikRepository repo) {
        this.repo = repo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("MFA_PASSED") != null) {
            String email = (String) session.getAttribute("EMAIL");
            Pracownik p = repo.findByEmail(email);

            UsernamePasswordAuthenticationToken fullAuth =
                    new UsernamePasswordAuthenticationToken(
                            email, null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER")));

            SecurityContextHolder.getContext().setAuthentication(fullAuth);
            session.removeAttribute("MFA_PASSED");
        }
        chain.doFilter(req, res);
    }
}
