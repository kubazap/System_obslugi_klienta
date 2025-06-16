package pl.zapala.system_obslugi_klienta.models;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.List;

/**
 * Encja reprezentująca klienta w systemie obsługi.
 * Przechowuje dane osobowe klienta oraz powiązane wizyty.
 */
@Entity
@Table(name = "klienci")
public class Klient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Imię klienta.
     */
    private String imie;

    /**
     * Nazwisko klienta.
     */
    private String nazwisko;

    /**
     * Data urodzenia klienta.
     */
    private Date dataUrodzenia;

    /**
     * Ulica i numer domu, miejsce zamieszkania klienta.
     * Przykład: "Marszałkowska 10".
     */
    private String ulicaNumerDomu;

    /**
     * Miejscowość zamieszkania klienta.
     */
    private String miejscowosc;

    /**
     * Kod pocztowy miejsca zamieszkania.
     * Format: "00-001".
     */
    private String kodPocztowy;

    /**
     * Numer telefonu kontaktowego klienta.
     */
    private String numerTelefonu;

    /**
     * Adres e-mail klienta.
     */
    private String email;

    /**
     * Lista wizyt powiązanych z klientem.
     * Kaskadowe usuwanie zapewnia usunięcie wizyt wraz z klientem.
     */
    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<Wizyta> wizyty;

    public List<Wizyta> getWizyty() {
        return wizyty;
    }
    public void setWizyty(List<Wizyta> wizyty) {
        this.wizyty = wizyty;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
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


