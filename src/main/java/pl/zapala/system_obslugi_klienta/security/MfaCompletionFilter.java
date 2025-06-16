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

import java.io.IOException;
import java.util.List;

/**
 * Filtr Spring Security, który finalizuje proces uwierzytelniania wieloskładnikowego (MFA).
 * <p>
 * Sprawdza w sesji HTTP atrybut wskazujący pomyślne przejście MFA ("MFA_PASSED").
 * Jeśli atrybut istnieje, tworzy pełne uwierzytelnienie z rolą ROLE_USER,
 * ustawia je w kontekście bezpieczeństwa i usuwa znacznik sesyjny.
 */
@Component
public class MfaCompletionFilter extends OncePerRequestFilter {
    /**
     * Metoda wywoływana przy każdym żądaniu HTTP.
     * <p>
     * Jeżeli w sesji istnieje atrybut "MFA_PASSED",
     * ustawia pełne uwierzytelnienie użytkownika w kontekście SecurityContext
     * i czyści atrybut sesji.
     *
     * @param req   obiekt żądania HTTP
     * @param res   obiekt odpowiedzi HTTP
     * @param chain łańcuch filtrów do wywołania po przetworzeniu
     * @throws ServletException w razie błędu servletu
     * @throws IOException      w razie błędu I/O podczas przetwarzania żądania
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("MFA_PASSED") != null) {
            String email = (String) session.getAttribute("EMAIL");

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
