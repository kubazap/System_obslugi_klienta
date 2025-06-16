package pl.zapala.system_obslugi_klienta.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Walidator sprawdzający poprawność formatu kwoty pieniężnej w aplikacji.
 * <p>
 * Reguły walidacji:
 * <ul>
 *   <li>Wartość nie może być null ani pusta (po przycięciu białych znaków).</li>
 *   <li>Kwota musi składać się z 1 do 7 cyfr, po których następuje spacja i symbol waluty:
 *       "zł", "eur" lub "usd".</li>
 * </ul>
 * Przykładowo: "100 zł", "2500 eur", "75000 usd".
 */
public class MoneyValidator implements ConstraintValidator<ValidMoney, String> {

    /**
     * Sprawdza, czy przekazany ciąg znaków jest poprawnie sformatowaną kwotą pieniędzy.
     *
     * @param value   tekst reprezentujący kwotę (np. "12345 zł")
     * @param context kontekst walidacji (może być użyty do niestandardowych komunikatów)
     * @return true, jeśli value nie jest null, nie jest puste i pasuje do wzorca;
     *         false w przeciwnym razie
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return false;
        return value.matches("^\\d{1,7} (zł|eur|usd)$");
    }
}
