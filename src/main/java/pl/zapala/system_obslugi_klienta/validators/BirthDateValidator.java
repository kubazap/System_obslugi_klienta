package pl.zapala.system_obslugi_klienta.validators;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.sql.Date;
import java.time.LocalDate;

public class BirthDateValidator implements ConstraintValidator<ValidBirthDate, Date> {

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        if (value == null) return false;

        LocalDate date = value.toLocalDate();
        LocalDate today = LocalDate.now();
        LocalDate earliest = today.minusYears(100);

        return !date.isAfter(today) && !date.isBefore(earliest);
    }
}