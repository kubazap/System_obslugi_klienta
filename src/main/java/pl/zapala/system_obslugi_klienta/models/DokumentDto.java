package pl.zapala.system_obslugi_klienta.models;

import jakarta.validation.constraints.NotEmpty;

import java.sql.Date;

/**
 * DTO używane w API i w widokach do przesyłania danych dokumentu.
 */
public class DokumentDto {

    /**
     * Data dodania dokumentu, wyświetlana w widoku i wysyłana w odpowiedziach API.
     * Pole opcjonalne — może być null przy tworzeniu nowego dokumentu.
     */
    private Date dataDodania;

    /**
     * Nazwa dokumentu widoczna w formularzu i odpowiedziach API.
     * Pole wymagane — nie może być puste.
     */
    @NotEmpty(message = "Pole nazwy dokumentu jest wymagane.")
    private String nazwaDokumentu;

    /**
     * Kategoria dokumentu (np. "Umowa", "Faktura", "Raport").
     * Pole wymagane — nie może być puste.
     */
    @NotEmpty(message = "Pole typu jest wymagane.")
    private String typ;

    /**
     * Flaga stanu dokumentu:
     * - true  — dokument aktywny/zatwierdzony,
     * - false — dokument nieaktywny/oczekujący.
     * Pole opcjonalne — jeśli null, traktowane jako false.
     */
    private Boolean status;

    /**
     * Dodatkowe uwagi wpisywane przez użytkownika w formularzu.
     * Pole opcjonalne — może pozostać puste.
     */
    private String uwagi;


    public Date getDataDodania() {
        return dataDodania;
    }
    public void setDataDodania(Date dataDodania) {
        this.dataDodania = dataDodania;
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
}
