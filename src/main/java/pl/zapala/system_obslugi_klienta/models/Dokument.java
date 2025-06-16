package pl.zapala.system_obslugi_klienta.models;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Encja reprezentująca dokument w systemie obsługi klienta.
 * Przechowuje podstawowe informacje o dokumencie oraz powiązane pliki.
 */
@Entity
@Table(name = "dokumenty")
public class Dokument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Nazwa dokumentu widoczna w systemie.
     * Przykłady: "Umowa nr 12/2025", "Faktura VAT 2025/05".
     */
    private String nazwaDokumentu;

    /**
     * Typ dokumentu określający jego kategorię.
     * Dozwolone wartości:
     * - "Umowa"
     * - "Faktura"
     * - "Formularz"
     * - "Raport"
     * - "Reklamacja"
     * - "Inne"
     */
    private String typ;

    /**
     * Data dodania dokumentu do systemu.
     * Ustawiana automatycznie przy tworzeniu nowego rekordu.
     */
    private Date dataDodania;

    /**
     * Status dokumentu wskazujący, czy dokument jest aktywny (true) czy np. zarchiwizowany (false).
     */
    private Boolean status;

    /**
     * Dodatkowe uwagi lub opis dokumentu.
     * Pole opcjonalne, może zawierać np. informacje o zmianach lub komentarze pracownika.
     */
    private String uwagi;

    /**
     * Lista plików powiązanych z dokumentem.
     * Każdy plik reprezentuje rzeczywisty plik przechowywany w magazynie.
     */
    @OneToMany(mappedBy = "dokument", cascade = CascadeType.ALL)
    private List<Plik> pliki = new ArrayList<>();

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNazwaDokumentu() {
        return nazwaDokumentu;
    }
    public void setNazwaDokumentu(String nazwaDokumentu) {
        this.nazwaDokumentu = nazwaDokumentu;
    }
    public String getTyp() {
        return typ;
    }
    public void setTyp(String typ) {
        this.typ = typ;
    }
    public Date getDataDodania() {
        return dataDodania;
    }
    public void setDataDodania(Date dataDodania) {
        this.dataDodania = dataDodania;
    }
    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
    public String getUwagi() {
        return uwagi;
    }
    public void setUwagi(String uwagi) {
        this.uwagi = uwagi;
    }
    public List<Plik> getPliki() {
        return pliki;
    }
    public void setPliki(List<Plik> pliki) {
        this.pliki = pliki;
    }
}
