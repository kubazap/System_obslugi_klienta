package pl.zapala.system_obslugi_klienta.validators;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class RoomValidator implements ConstraintValidator<ValidRoom, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return false;
        return value.matches("^[A-Za-z0-9. ]+$");
    }
}