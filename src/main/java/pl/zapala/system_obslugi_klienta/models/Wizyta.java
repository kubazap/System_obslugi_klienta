package pl.zapala.system_obslugi_klienta.models;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name="wizyty")
public class Wizyta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Date dataWizyty;
    private String godzina;
    private String pokoj;
    private Boolean czyOplacona;
    private String naleznosc;
    private String sposobPlatnosci; // Karta kredytowa, Gotówka, Blik
    private String uwagi;
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
