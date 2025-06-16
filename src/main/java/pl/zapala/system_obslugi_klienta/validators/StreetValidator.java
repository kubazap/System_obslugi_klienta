package pl.zapala.system_obslugi_klienta.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Walidator sprawdzający poprawność formatu ulicy i numeru domu.
 * <p>
 * Reguły walidacji:
 * <ul>
 *   <li>Wartość nie może być null ani pusta (po przycięciu białych znaków).</li>
 *   <li>Pole może zawierać:
 *     <ul>
 *       <li>litery łacińskie i polskie znaki diakrytyczne (A–Z, a–z, ąćęłńóśźż),</li>
 *       <li>cyfry (0–9),</li>
 *       <li>spację, kropkę '.', przecinek ',', ukośnik '/' oraz myślnik '-'.</li>
 *     </ul>
 *   </li>
 * </ul>
 * Przykładowe poprawne wartości:
 * <ul>
 *   <li>"Marszałkowska 10"</li>
 *   <li>"Świętokrzyska 5/7"</li>
 *   <li>"ul. Długa 12, lok. 3"</li>
 * </ul>
 */
public class StreetValidator implements ConstraintValidator<ValidStreet, String> {

    /**
     * Sprawdza, czy przekazana wartość spełnia reguły formatu ulicy i numeru domu.
     *
     * @param value   tekst z adresem (np. "Marszałkowska 10")
     * @param context kontekst walidacji (może być użyty do niestandardowych komunikatów)
     * @return true, jeśli wartość nie jest null, nie jest pusta i pasuje do wzorca;
     *         false w przeciwnym razie
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return false;
        return value.matches("^[A-ZĄĆĘŁŃÓŚŹŻa-ząćęłńóśźż0-9 .,/\\-]+$");
    }
}