package pl.zapala.system_obslugi_klienta.models;

import jakarta.persistence.*;

import java.sql.Date;

/**
 * Encja reprezentująca pojedynczy plik powiązany z dokumentem.
 * Przechowuje informacje o dacie dodania, nazwie pliku oraz relacji do dokumentu.
 */
@Entity
@Table(name = "pliki")
public class Plik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Data dodania pliku do systemu (strefa serwera, tylko data).
     * Ustawiana w momencie tworzenia rekordu.
     */
    private Date dataDodania;

    /**
     * Fizyczna nazwa pliku przechowywana w magazynie,
     * wygenerowana jako znacznik czasu + rozszerzenie.
     */
    private String nazwaPliku;

    /**
     * Dokument, do którego należy ten plik.
     * Relacja wiele do jednego, kaskada usuwania z dokumentu.
     */
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
