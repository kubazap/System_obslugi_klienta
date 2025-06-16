package pl.zapala.system_obslugi_klienta.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Walidator sprawdzający poprawność numeru telefonu.
 * <p>
 * Reguły walidacji:
 * <ul>
 *   <li>Pole opcjonalne — wartość null lub pusta jest akceptowana.</li>
 *   <li>Jeśli podano wartość, musi zaczynać się od znaku '+' i kodu kraju (1–3 cyfry).</li>
 *   <li>Następnie dopuszczalne są grupy 1–4 cyfr, rozdzielone spacją lub myślnikiem.</li>
 * </ul>
 * Przykładowe poprawne formaty:
 * <ul>
 *   <li>"+48 123 456 789"</li>
 *   <li>"+1-800-1234"</li>
 *   <li>"+44 20 7946 0958"</li>
 * </ul>
 */
public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    /**
     * Sprawdza, czy wartość jest prawidłowym numerem telefonu międzynarodowego.
     *
     * @param value   numer telefonu do walidacji
     * @param context kontekst walidacji (może być użyty do budowy komunikatów)
     * @return true, jeśli wartość jest null lub pusta, lub pasuje do wzorca numeru;
     *         false w przeciwnym razie
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return true;
        return value.matches("^\\+\\d{1,3}([- ]?\\d{1,4})+$");
    }
}
