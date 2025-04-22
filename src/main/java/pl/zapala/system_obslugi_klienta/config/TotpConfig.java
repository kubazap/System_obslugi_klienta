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

@Configuration
public class TotpConfig {
    @Bean
    public SecretGenerator secretGenerator() {
        return new DefaultSecretGenerator();
    }

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
