package pl.zapala.system_obslugi_klienta.models;

import jakarta.validation.constraints.*;
import pl.zapala.system_obslugi_klienta.validators.ValidTime;
import pl.zapala.system_obslugi_klienta.validators.ValidVisitDate;

import java.sql.Date;

public class WizytaDto {
    @NotNull(message= "Pole daty wizyty jest wymagane.")
    @ValidVisitDate
    private Date dataWizyty;
    @NotEmpty(message= "Pole godziny jest wymagane.")
    @ValidTime
    private String godzina;
    @NotEmpty(message= "Pole pokoju jest wymagane.")
    private String pokoj;
    private Boolean czyOplacona;
    @NotEmpty(message= "Pole należności jest wymagane.")
    private String naleznosc;
    @NotEmpty(message= "Pole sposobu płatności jest wymagane.")
    private String sposobPlatnosci;
    private String uwagi;
    @NotNull
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
