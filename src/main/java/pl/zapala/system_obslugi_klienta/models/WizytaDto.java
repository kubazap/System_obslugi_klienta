package pl.zapala.system_obslugi_klienta.models;

import jakarta.validation.constraints.*;
import pl.zapala.system_obslugi_klienta.validators.*;

import java.sql.Date;

/**
 * DTO używane w API i formularzach do wprowadzania i przesyłania danych wizyty.
 */
public class WizytaDto {

    /**
     * Data wizyty.
     * Pole wymagane — nie może być null.
     * Walidowane adnotacją @ValidVisitDate (musi być dzisiejsza lub przyszła).
     */
    @NotNull(message = "Pole daty wizyty jest wymagane.")
    @ValidVisitDate
    private Date dataWizyty;

    /**
     * Godzina wizyty w formacie "HH:mm".
     * Pole wymagane — nie może być puste.
     * Walidowane adnotacją @ValidTime.
     */
    @NotEmpty(message = "Pole godziny jest wymagane.")
    @ValidTime
    private String godzina;

    /**
     * Numer lub nazwa pokoju, w którym odbywa się wizyta.
     * Pole wymagane — nie może być puste.
     * Walidowane adnotacją @ValidRoom.
     */
    @NotEmpty(message = "Pole pokoju jest wymagane.")
    @ValidRoom
    private String pokoj;

    /**
     * Flaga wskazująca, czy wizyta została opłacona.
     * Pole opcjonalne — jeśli null, traktowane jako nieopłacona (false).
     */
    private Boolean czyOplacona;

    /**
     * Kwota należności związanej z wizytą.
     * Pole wymagane — nie może być puste.
     * Walidowane adnotacją @ValidMoney (np. format liczbowy z dwiema cyframi po przecinku).
     */
    @NotEmpty(message = "Pole należności jest wymagane.")
    @ValidMoney
    private String naleznosc;

    /**
     * Sposób dokonania płatności (np. "Karta kredytowa", "Gotówka", "Blik").
     * Pole wymagane — nie może być puste.
     */
    @NotEmpty(message = "Pole sposobu płatności jest wymagane.")
    private String sposobPlatnosci;

    /**
     * Dodatkowe uwagi dotyczące wizyty.
     * Pole opcjonalne — może zawierać instrukcje lub komentarze.
     */
    private String uwagi;

    /**
     * Identyfikator klienta, dla którego planowana jest wizyta.
     * Pole wymagane — nie może być null.
     */
    @NotNull(message = "Pole klienta jest wymagane.")
    private Integer klientId;

    public Integer getKlientId() {
        return klientId;
    }
    public void setKlientId(Integer klientId) {
        this.klientId = klientId;
    }

    public Date getDataWizyty() {
        return dataWizyty;
    }
    public void setDataWizyty(Date dataWizyty) {
        this.dataWizyty = dataWizyty;
    }
    public String getGodzina() {
        return godzina;
    }
    public void setGodzina(String godzina) {
        this.godzina = godzina;
    }
    public String getPokoj() {
        return pokoj;
    }
    public void setPokoj(String pokoj) {
        this.pokoj = pokoj;
    }
    public Boolean getCzyOplacona() {
        return czyOplacona;
    }
    public void setCzyOplacona(Boolean czyOplacona) {
        this.czyOplacona = czyOplacona;
    }
    public String getNaleznosc() {
        return naleznosc;
    }
    public void setNaleznosc(String naleznosc) {
        this.naleznosc = naleznosc;
    }
    public String getSposobPlatnosci() {
        return sposobPlatnosci;
    }
    public void setSposobPlatnosci(String sposobPlatnosci) {
        this.sposobPlatnosci = sposobPlatnosci;
    }
    public String getUwagi() {
        return uwagi;
    }
    public void setUwagi(String uwagi) {
        this.uwagi = uwagi;
    }
}
