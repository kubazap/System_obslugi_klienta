package pl.zapala.system_obslugi_klienta.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TimeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTime {
    String message() default "Nieprawidłowy format godziny (hh.mm)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}