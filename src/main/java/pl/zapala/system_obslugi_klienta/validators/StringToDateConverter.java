package pl.zapala.system_obslugi_klienta.validators;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class StringToDateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        return Date.valueOf(source); // wymaga formatu "yyyy-MM-dd"
    }
}
