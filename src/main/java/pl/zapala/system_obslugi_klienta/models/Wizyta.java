package pl.zapala.system_obslugi_klienta.models;

import jakarta.persistence.*;

import java.sql.Date;

/**
 * Encja reprezentująca wizytę w systemie obsługi klienta.
 * Zawiera informacje o terminie, pokoju, płatności oraz powiązaniu z klientem.
 */
@Entity
@Table(name = "wizyty")
public class Wizyta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Data wizyty.
     * Określa dzień, w którym wizyta jest zaplanowana.
     */
    private Date dataWizyty;

    /**
     * Godzina wizyty w formacie "HH:mm".
     */
    private String godzina;

    /**
     * Numer lub nazwa pokoju, w którym odbywa się wizyta.
     */
    private String pokoj;

    /**
     * Flaga wskazująca, czy wizyta została opłacona (true) czy nie (false).
     */
    private Boolean czyOplacona;

    /**
     * Kwota należności związanej z wizytą.
     * Format tekstowy, na przykład "100.00 PLN".
     */
    private String naleznosc;

    /**
     * Sposób dokonania płatności.
     * Możliwe wartości: "Karta kredytowa", "Gotówka", "Blik".
     */
    private String sposobPlatnosci;

    /**
     * Dodatkowe uwagi dotyczące wizyty.
     * Pole opcjonalne, może zawierać np. instrukcje dla pracownika.
     */
    private String uwagi;

    /**
     * Klient, dla którego zaplanowano wizytę.
     */
    @ManyToOne
    private Klient klient;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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
    public Klient getKlient() {
        return klient;
    }
    public void setKlient(Klient klient) {
        this.klient = klient;
    }
}
