package pl.zapala.system_obslugi_klienta.security;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.stereotype.Component;

/**
 * Narzędzie do generowania kodów TOTP na podstawie sekretu użytkownika.
 * <p>
 * Używa domyślnego generatora kodów SHA1 z 6-cyfrowym kodem
 * oraz dostarcza aktualny czas w krokach co 30 sekund.
 */
@Component
public class TotpUtil {

    private final CodeGenerator generator;
    private final SystemTimeProvider timeProvider = new SystemTimeProvider();

    /**
     * Konstruktor inicjalizujący generator kodów TOTP.
     * <p>
     * Używa algorytmu SHA1 i długości kodu 6 cyfr.
     */
    public TotpUtil() {
        this.generator = new DefaultCodeGenerator(HashingAlgorithm.SHA1, 6);
    }

    /**
     * Generuje bieżący kod TOTP na podstawie sekretu.
     * <p>
     * Oblicza krok czasu (co 30 sekund) i przekazuje go do generatora.
     *
     * @param secret sekret TOTP przypisany do użytkownika
     * @return 6-cyfrowy kod TOTP ważny dla bieżącego kroku czasowego
     * @throws CodeGenerationException gdy generowanie kodu nie powiedzie się
     */
    public String generateCurrentCode(String secret) throws CodeGenerationException {
        long timeStep = timeProvider.getTime() / 30;
        return generator.generate(secret, timeStep);
    }
}