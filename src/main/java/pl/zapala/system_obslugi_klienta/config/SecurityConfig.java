package pl.zapala.system_obslugi_klienta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import pl.zapala.system_obslugi_klienta.security.MfaCompletionFilter;
import pl.zapala.system_obslugi_klienta.security.MfaSuccessHandler;

/**
 * Konfiguracja bezpieczeństwa aplikacji.
 * Definiuje reguły dostępu do zasobów, ustawienia logowania, wylogowania
 * oraz integrację wieloskładnikowego uwierzytelniania (MFA).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Konfiguruje łańcuch filtrów bezpieczeństwa (SecurityFilterChain).
     * <ul>
     *     <li>Umożliwia dostęp do strony logowania i zasobów statycznych bez uwierzytelnienia.</li>
     *     <li>Pozwala na publiczny dostęp do endpointów wiadomości.</li>
     *     <li>Wymaga uwierzytelnienia dla pozostałych żądań.</li>
     *     <li>Ustawia niestandardową stronę logowania oraz parametry formularza.</li>
     *     <li>Dodaje obsługę sukcesu logowania z wysłaniem TOTP oraz filtr kończący MFA.</li>
     * </ul>
     *
     * @param http           konfigurator HTTP Security
     * @param successHandler handler obsługujący sukces logowania (wysłanie kodu TOTP)
     * @param mfaFilter      filtr kończący proces MFA po weryfikacji kodu
     * @return zbudowany łańcuch filtrów bezpieczeństwa
     * @throws Exception w przypadku problemów z konfiguracją HTTP Security
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           MfaSuccessHandler successHandler,
                                           MfaCompletionFilter mfaFilter) throws Exception {

        return http
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/", "/login", "/login/authenticate",
                                "/css/**", "/js/**", "/img/**", "/vendor/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/messages").permitAll()
                        .requestMatchers(HttpMethod.GET, "/messages/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(f -> f
                        .loginPage("/login")
                        .loginProcessingUrl("/login/authenticate")
                        .usernameParameter("email")
                        .passwordParameter("haslo")
                        .successHandler(successHandler))
                .logout(l -> l.logoutSuccessUrl("/login?logout"))
                .addFilterAfter(mfaFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Definiuje bean PasswordEncoder wykorzystujący bcrypt do haszowania haseł.
     *
     * @return instancja BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}