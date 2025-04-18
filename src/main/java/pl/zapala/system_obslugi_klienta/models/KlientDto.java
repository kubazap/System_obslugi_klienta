package pl.zapala.system_obslugi_klienta.models;

import jakarta.validation.constraints.*;

import java.sql.Date;

public class KlientDto {
    @NotEmpty(message= "Pole imię jest wymagane.")
    private String imie;
    @NotEmpty(message= "Pole nazwisko jest wymagane.")
    private String nazwisko;
    @Past
    @NotNull(message= "Pole daty urodzin jest wymagane.")
    private Date dataUrodzenia;
    @NotEmpty(message= "Pole ulicy i numeru domu jest wymagane.")
    private String ulicaNumerDomu;
    @NotEmpty(message= "Pole miejscowości jest wymagane.")
    private String miejscowosc;
    @NotEmpty(message= "Pole kodu pocztowego jest wymagane. Poprawny format kodu: XX-XXX.")
    private String kodPocztowy;
    private String numerTelefonu;
    @Email
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
