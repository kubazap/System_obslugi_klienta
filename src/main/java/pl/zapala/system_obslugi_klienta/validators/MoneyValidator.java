package pl.zapala.system_obslugi_klienta.validators;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class MoneyValidator implements ConstraintValidator<ValidMoney, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return false;
        return value.matches("^\\d{1,7} (zł|eur|usd)$");
    }
}