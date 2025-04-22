package pl.zapala.system_obslugi_klienta.security;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.stereotype.Component;

@Component
public class TotpUtil {

    private final CodeGenerator generator;
    private final SystemTimeProvider timeProvider = new SystemTimeProvider();

    public TotpUtil() {
        this.generator = new DefaultCodeGenerator(HashingAlgorithm.SHA1, 6);
    }

    public String generateCurrentCode(String secret) throws CodeGenerationException {
        long timeStep = timeProvider.getTime() / 30;
        return generator.generate(secret, timeStep);
    }
}
