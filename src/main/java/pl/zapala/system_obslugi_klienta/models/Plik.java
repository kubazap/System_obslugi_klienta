package pl.zapala.system_obslugi_klienta.models;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name="pliki")
public class Plik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Date dataDodania;
    private String nazwaPliku;
    @ManyToOne
    private Dokument dokument;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Date getDataDodania() {
        return dataDodania;
    }
    public void setDataDodania(Date dataDodania) {
        this.dataDodania = dataDodania;
    }
    public String getNazwaPliku() {
        return nazwaPliku;
    }
    public void setNazwaPliku(String nazwaPliku) {
        this.nazwaPliku = nazwaPliku;
    }
    public Dokument getDokument() {
        return dokument;
    }
    public void setDokument(Dokument dokument) {
        this.dokument = dokument;
    }
}
