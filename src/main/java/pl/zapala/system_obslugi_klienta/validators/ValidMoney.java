package pl.zapala.system_obslugi_klienta.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MoneyValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMoney {
    String message() default "Podaj poprawną kwotę. {kwota zł/eur/usd}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}