package pl.zapala.system_obslugi_klienta.config;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguracja komponentów TOTP używanych do generowania sekretów i weryfikacji kodów.
 */
@Configuration
public class TotpConfig {

    /**
     * Bean odpowiedzialny za generowanie sekretów TOTP.
     *
     * @return instancja DefaultSecretGenerator tworząca nowe sekrety
     */
    @Bean
    public SecretGenerator secretGenerator() {
        return new DefaultSecretGenerator();
    }

    /**
     * Bean odpowiedzialny za weryfikację kodów TOTP.
     * <ul>
     *     <li>Używa algorytmu SHA1 i długości kodu 6 cyfr.</li>
     *     <li>Okres czasu ustawiony na 30 sekund.</li>
     *     <li>Dopuszczalne opóźnienie do 4 okresów (±120 sekund) dla synchronizacji czasu.</li>
     * </ul>
     *
     * @return skonfigurowany DefaultCodeVerifier do sprawdzania poprawności kodów
     */
    @Bean
    public CodeVerifier totpVerifier() {
        DefaultCodeVerifier v = new DefaultCodeVerifier(
                new DefaultCodeGenerator(HashingAlgorithm.SHA1, 6),
                new SystemTimeProvider());
        v.setTimePeriod(30);
        v.setAllowedTimePeriodDiscrepancy(4);
        return v;
    }
}