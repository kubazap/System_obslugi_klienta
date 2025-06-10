package pl.zapala.system_obslugi_klienta.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PhoneValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {
    String message() default "Podaj poprawny numer telefonu.{np. +48 123 123 123}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}