package pl.zapala.system_obslugi_klienta.models;

import pl.zapala.system_obslugi_klienta.validators.ValidName;

/**
 * DTO reprezentujące minimalne informacje o pracowniku przesyłane w API.
 * Używane do wyświetlania list pracowników lub wyboru rozmówcy.
 */
public class PracownikDto {

    /**
     * Unikalny identyfikator pracownika.
     */
    private Integer id;

    /**
     * Imię pracownika.
     * Walidowane adnotacją @ValidName — tylko litery, bez cyfr i znaków specjalnych.
     */
    @ValidName
    private String imie;

    /**
     * Nazwisko pracownika.
     * Walidowane adnotacją @ValidName.
     */
    @ValidName
    private String nazwisko;
    public Integer getId() {return id;}

    public String getImie() {return imie;}

    public String getNazwisko() {return nazwisko;}

    public void setId(Integer id) {this.id = id;}

    public void setImie(String imie) {this.imie = imie;}

    public void setNazwisko(String nazwisko) {this.nazwisko = nazwisko;}
}