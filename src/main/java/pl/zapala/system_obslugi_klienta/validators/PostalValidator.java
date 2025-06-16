package pl.zapala.system_obslugi_klienta.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Walidator sprawdzający poprawność formatu kodu pocztowego.
 * <p>
 * Reguły walidacji:
 * <ul>
 *   <li>Wartość nie może być null ani pusta (po przycięciu białych znaków).</li>
 *   <li>Kod pocztowy musi mieć format "XX-XXX", gdzie X to cyfra (0–9).</li>
 * </ul>
 * Przykładowe poprawne wartości:
 * <ul>
 *   <li>"00-001"</li>
 *   <li>"12-345"</li>
 * </ul>
 */
public class PostalValidator implements ConstraintValidator<ValidPostal, String> {

    /**
     * Sprawdza, czy przekazana wartość jest prawidłowym kodem pocztowym.
     *
     * @param value   tekst reprezentujący kod pocztowy do walidacji
     * @param context kontekst walidacji (może być użyty do budowy komunikatów o błędach)
     * @return true, jeśli wartość nie jest null, nie jest pusta i pasuje do wzorca "\\d{2}-\\d{3}";
     *         false w przeciwnym razie
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return false;
        return value.matches("^\\d{2}-\\d{3}$");
    }
}