package pl.zapala.system_obslugi_klienta.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Walidator sprawdzający poprawność formatu adresu e-mail.
 * <p>
 * Reguły walidacji:
 * <ul>
 *   <li>Wartość nie może być null ani pusta (po przycięciu białych znaków).</li>
 *   <li>Adres musi pasować do wzorca: lokalna część (litery, cyfry, +, _, . lub -),
 *       znak '@', domena (litery, cyfry, -, .) oraz sufiks od 2 do 6 liter.</li>
 * </ul>
 * Przykładowo: "user.name+tag@example-domain.com".
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    /**
     * Wyrażenie regularne definiujące dopuszczalny format adresu e-mail.
     */
    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    /**
     * Sprawdza, czy przekazany ciąg znaków jest poprawnym adresem e-mail.
     *
     * @param value   adres e-mail do walidacji
     * @param context kontekst walidacji (może służyć do budowania niestandardowych komunikatów)
     * @return true, jeśli wartość nie jest null, nie jest pusta i pasuje do wzorca EMAIL_REGEX;
     *         false w przeciwnym razie
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return false;
        return value.matches(EMAIL_REGEX);
    }
}
