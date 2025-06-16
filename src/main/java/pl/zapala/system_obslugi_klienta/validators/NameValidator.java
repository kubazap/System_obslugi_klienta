package pl.zapala.system_obslugi_klienta.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Walidator sprawdzający poprawność imienia lub nazwiska.
 * <p>
 * Reguły walidacji:
 * <ul>
 *   <li>Wartość nie może być null ani pusta (po przycięciu białych znaków).</li>
 *   <li>Może zawierać litery łacińskie i polskie znaki diakrytyczne.</li>
 *   <li>Dopuszczalne są znak myślnika '-' oraz spacja.</li>
 * </ul>
 * Przykładowo: "Anna", "Łukasz-Kowalski", "Maria Teresa".
 */
public class NameValidator implements ConstraintValidator<ValidName, String> {

    /**
     * Sprawdza, czy przekazany ciąg znaków spełnia reguły formatu imienia/nazwiska.
     *
     * @param value   imię lub nazwisko do walidacji
     * @param context kontekst walidacji (używany do budowania komunikatów o błędach)
     * @return true, jeśli wartość nie jest null, nie jest pusta i pasuje do wzorca;
     *         false w przeciwnym razie
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return false;
        return value.matches("^[A-ZĄĆĘŁŃÓŚŹŻa-ząćęłńóśźż\\- ]+$");
    }
}
