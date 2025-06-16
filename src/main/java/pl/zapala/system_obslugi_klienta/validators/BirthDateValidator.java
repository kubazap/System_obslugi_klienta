package pl.zapala.system_obslugi_klienta.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Walidator sprawdzający poprawność daty urodzenia klienta.
 * <p>
 * Reguły walidacji:
 * <ul>
 *   <li>Data nie może być null.</li>
 *   <li>Data musi być wcześniejsza lub równa bieżącej dacie.</li>
 *   <li>Data nie może być starsza niż 100 lat wstecz.</li>
 * </ul>
 * Zwraca false, gdy wartość jest null lub spoza dozwolonego zakresu.
 */
public class BirthDateValidator implements ConstraintValidator<ValidBirthDate, Date> {

    /**
     * Sprawdza, czy przekazana data urodzenia mieści się w dozwolonym przedziale.
     *
     * @param value   data urodzenia do walidacji
     * @param context kontekst walidacji (może być użyty do budowania niestandardowych komunikatów)
     * @return true, jeśli data jest niepusta, nie jest w przyszłości i nie jest starsza niż 100 lat; false w przeciwnym razie
     */
    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        if (value == null) return false;

        LocalDate date = value.toLocalDate();
        LocalDate today = LocalDate.now();
        LocalDate earliest = today.minusYears(100);

        return !date.isAfter(today) && !date.isBefore(earliest);
    }
}