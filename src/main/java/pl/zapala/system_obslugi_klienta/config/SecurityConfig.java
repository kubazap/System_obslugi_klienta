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
import pl.zapala.system_obslugi_klienta.security.MfaCompletionFilter;
import pl.zapala.system_obslugi_klienta.security.MfaSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           MfaSuccessHandler successHandler,
                                           MfaCompletionFilter mfaFilter) throws Exception {

        return http
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/", "/login", "/login/authenticate",
                                "/css/**", "/js/**", "/img/**", "/vendor/**").permitAll()
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

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}