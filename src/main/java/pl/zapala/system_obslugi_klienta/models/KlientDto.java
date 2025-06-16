package pl.zapala.system_obslugi_klienta.models;

import jakarta.validation.constraints.*;
import java.sql.Date;

import pl.zapala.system_obslugi_klienta.validators.*;

/**
 * DTO używane w API i formularzach do wprowadzania i przesyłania danych klienta.
 */
public class KlientDto {

    /**
     * Imię klienta.
     * Pole wymagane — nie może być puste.
     * Walidowane za pomocą @ValidName (tylko litery, bez cyfr i znaków specjalnych).
     */
    @NotEmpty(message = "Pole imię jest wymagane.")
    @ValidName
    private String imie;

    /**
     * Nazwisko klienta.
     * Pole wymagane — nie może być puste.
     * Walidowane za pomocą @ValidName.
     */
    @NotEmpty(message = "Pole nazwisko jest wymagane.")
    @ValidName
    private String nazwisko;

    /**
     * Data urodzenia klienta.
     * Pole wymagane — nie może być null.
     * Musi wskazywać datę z przeszłości (@Past) i spełniać dodatkowe reguły @ValidBirthDate.
     */
    @Past
    @NotNull(message = "Pole daty urodzin jest wymagane.")
    @ValidBirthDate
    private Date dataUrodzenia;

    /**
     * Ulica i numer domu zamieszkania klienta.
     * Pole wymagane — nie może być puste.
     * Walidowane za pomocą @ValidStreet (np. "Marszałkowska 10").
     */
    @NotEmpty(message = "Pole ulicy i numeru domu jest wymagane.")
    @ValidStreet
    private String ulicaNumerDomu;

    /**
     * Miejscowość zamieszkania klienta.
     * Pole wymagane — nie może być puste.
     * Walidowane za pomocą @ValidName.
     */
    @NotEmpty(message = "Pole miejscowości jest wymagane.")
    @ValidName
    private String miejscowosc;

    /**
     * Kod pocztowy miejsca zamieszkania.
     * Pole wymagane — nie może być puste.
     * Format: XX-XXX, walidowany przez @ValidPostal.
     */
    @NotEmpty(message = "Pole kodu pocztowego jest wymagane. Poprawny format kodu: XX-XXX.")
    @ValidPostal
    private String kodPocztowy;

    /**
     * Numer telefonu kontaktowego.
     * Pole opcjonalne — jeśli podane, walidowane przez @ValidPhone.
     */
    @ValidPhone
    private String numerTelefonu;

    /**
     * Adres e-mail klienta.
     * Pole opcjonalne — jeśli podane, musi być poprawnym adresem e-mail (@Email).
     */
    @Email(message = "Niepoprawny format adresu e-mail.")
    private String email;

    public String getImie() {
        return imie;
    }
    public void setImie(String imie) {
        this.imie = imie;
    }
    public String getNazwisko() {
        return nazwisko;
    }
    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }
    public Date getDataUrodzenia() {
        return dataUrodzenia;
    }
    public void setDataUrodzenia(Date dataUrodzenia) {
        this.dataUrodzenia = dataUrodzenia;
    }
    public String getUlicaNumerDomu() {
        return ulicaNumerDomu;
    }
    public void setUlicaNumerDomu(String ulicaNumerDomu) {
        this.ulicaNumerDomu = ulicaNumerDomu;
    }
    public String getMiejscowosc() {
        return miejscowosc;
    }
    public void setMiejscowosc(String miejscowosc) {
        this.miejscowosc = miejscowosc;
    }
    public String getKodPocztowy() {
        return kodPocztowy;
    }
    public void setKodPocztowy(String kodPocztowy) {
        this.kodPocztowy = kodPocztowy;
    }
    public String getNumerTelefonu() {
        return numerTelefonu;
    }
    public void setNumerTelefonu(String numerTelefonu) {
        this.numerTelefonu = numerTelefonu;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
