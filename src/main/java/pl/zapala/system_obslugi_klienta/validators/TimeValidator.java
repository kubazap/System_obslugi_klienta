package pl.zapala.system_obslugi_klienta.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Walidator sprawdzający poprawność formatu godziny wizyty.
 * <p>
 * Reguły walidacji:
 * <ul>
 *   <li>Wartość nie może być null ani pusta (po przycięciu białych znaków).</li>
 *   <li>Godzina musi być w formacie "HH.mm", gdzie:
 *     <ul>
 *       <li>HH – od "00" do "23"</li>
 *       <li>mm – od "00" do "59"</li>
 *     </ul>
 *   </li>
 * </ul>
 * Przykładowe poprawne wartości:
 * <ul>
 *   <li>"09.30"</li>
 *   <li>"14.05"</li>
 *   <li>"23.59"</li>
 * </ul>
 */
public class TimeValidator implements ConstraintValidator<ValidTime, String> {

    /**
     * Sprawdza, czy przekazany ciąg znaków jest poprawnie sformatowaną godziną.
     *
     * @param value   tekst reprezentujący godzinę (np. "08.15")
     * @param context kontekst walidacji (może być użyty do budowy komunikatów o błędach)
     * @return true, jeśli wartość nie jest null, nie jest pusta i pasuje do wzorca "^([01]\\d|2[0-3])\\.[0-5]\\d$";
     *         false w przeciwnym razie
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return false;
        return value.matches("^([01]\\d|2[0-3])\\.[0-5]\\d$");
    }
}
