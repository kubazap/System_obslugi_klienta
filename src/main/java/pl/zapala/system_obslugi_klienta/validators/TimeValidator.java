package pl.zapala.system_obslugi_klienta.validators;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class TimeValidator implements ConstraintValidator<ValidTime, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return false;

        return value.matches("^([01]\\d|2[0-3])\\.[0-5]\\d$");
    }
}