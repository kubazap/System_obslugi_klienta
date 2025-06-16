package pl.zapala.system_obslugi_klienta.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Walidator sprawdzający poprawność identyfikatora pokoju.
 * <p>
 * Reguły walidacji:
 * <ul>
 *   <li>Wartość nie może być null ani pusta (po przycięciu białych znaków).</li>
 *   <li>Pole może zawierać litery (A–Z, a–z), cyfry (0–9), kropkę oraz spację.</li>
 * </ul>
 * Przykładowe poprawne wartości:
 * <ul>
 *   <li>"101"</li>
 *   <li>"Sala 3"</li>
 *   <li>"A.12"</li>
 * </ul>
 */
public class RoomValidator implements ConstraintValidator<ValidRoom, String> {

    /**
     * Sprawdza, czy przekazana wartość jest prawidłowym identyfikatorem pokoju.
     *
     * @param value   identyfikator pokoju do walidacji
     * @param context kontekst walidacji (może być użyty do budowy komunikatów o błędach)
     * @return true, jeśli wartość nie jest null, nie jest pusta i pasuje do wzorca
     *         "[A-Za-z0-9. ]+"; false w przeciwnym razie
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return false;
        return value.matches("^[A-Za-z0-9. ]+$");
    }
}