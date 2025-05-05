package pl.zapala.system_obslugi_klienta.validators;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class NameValidator implements ConstraintValidator<ValidName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return false;
        return value.matches("^[A-ZĄĆĘŁŃÓŚŹŻa-ząćęłńóśźż\\- ]+$");
    }
}