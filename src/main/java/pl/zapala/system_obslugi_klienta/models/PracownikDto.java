package pl.zapala.system_obslugi_klienta.models;

public class PracownikDto {
    private Integer id;
    private String imie;
    private String nazwisko;

    public PracownikDto() {
    }

    public PracownikDto(Integer id, String imie, String nazwisko) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
    }

    public Integer getId() {return id;}

    public String getImie() {return imie;}

    public String getNazwisko() {return nazwisko;}

    public void setId(Integer id) {this.id = id;}

    public void setImie(String imie) {this.imie = imie;}

    public void setNazwisko(String nazwisko) {this.nazwisko = nazwisko;}
}