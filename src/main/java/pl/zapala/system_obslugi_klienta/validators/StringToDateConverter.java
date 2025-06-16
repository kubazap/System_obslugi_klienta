package pl.zapala.system_obslugi_klienta.validators;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Date;

/**
 * Konwerter Springa zamieniający ciąg znaków na obiekt {@link Date}.
 * <p>
 * Oczekiwany format ciągu: "yyyy-MM-dd".
 * Jeśli wartość wejściowa jest null lub pusta, zwraca null.
 */
@Component
public class StringToDateConverter implements Converter<String, Date> {

    /**
     * Konwertuje podany ciąg znaków na datę SQL.
     *
     * @param source ciąg znaków reprezentujący datę w formacie "yyyy-MM-dd"
     * @return obiekt {@link Date} odpowiadający przekazanemu ciągowi lub null, jeśli source jest null lub pusty
     * @throws IllegalArgumentException gdy ciąg ma nieprawidłowy format (np. niezgodny z "yyyy-MM-dd")
     */
    @Override
    public Date convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        return Date.valueOf(source);
    }
}
