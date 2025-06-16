package pl.zapala.system_obslugi_klienta.models;

import jakarta.persistence.*;

/**
 * Encja reprezentująca pracownika w systemie obsługi klienta.
 * Przechowuje dane uwierzytelniające oraz sekret TOTP do MFA.
 */
@Entity
@Table(name = "pracownicy")
public class Pracownik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Imię pracownika.
     */
    private String imie;

    /**
     * Nazwisko pracownika.
     */
    private String nazwisko;

    /**
     * Unikalny adres e-mail służący do logowania.
     */
    private String email;

    /**
     * Zahaszowane hasło pracownika (bcrypt).
     */
    private String haslo;

    /**
     * Sekret używany do generowania kodów TOTP dla drugiego czynnika uwierzytelniania.
     */
    private String totpSecret;

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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getHaslo() {
        return haslo;
    }
    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public String getTotpSecret() {
        return totpSecret;
    }

    public void setTotpSecret(String totpSecret) {
        this.totpSecret = totpSecret;
    }
}