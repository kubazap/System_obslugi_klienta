package pl.zapala.system_obslugi_klienta.exception;

import dev.samstevens.totp.exceptions.CodeGenerationException;

public class CodeGenerationFailedException extends RuntimeException {
    public CodeGenerationFailedException(String message) {
        super(message);
    }
    public CodeGenerationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
    public CodeGenerationFailedException(String message, CodeGenerationException cause) {
        super(message, cause);
    }
}