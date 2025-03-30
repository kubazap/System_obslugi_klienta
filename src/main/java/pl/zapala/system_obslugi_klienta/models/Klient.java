package pl.zapala.system_obslugi_klienta.models;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name="klienci")
public class Klient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String imieNazwisko;
    private Date dataUrodzenia;
    private String adresZamieszkania;
    private String numerTelefonu;
    private String email;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getImieNazwisko() {
        return imieNazwisko;
    }
    public void setImieNazwisko(String imieNazwisko) {
        this.imieNazwisko = imieNazwisko;
    }
    public Date getDataUrodzenia() {
        return dataUrodzenia;
    }
    public void setDataUrodzenia(Date dataUrodzenia) {
        this.dataUrodzenia = dataUrodzenia;
    }
    public String getAdresZamieszkania() {
        return adresZamieszkania;
    }
    public void setAdresZamieszkania(String adresZamieszkania) {
        this.adresZamieszkania = adresZamieszkania;
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


