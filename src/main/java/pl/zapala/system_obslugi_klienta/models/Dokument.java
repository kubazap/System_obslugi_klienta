package pl.zapala.system_obslugi_klienta.models;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="dokumenty")
public class Dokument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nazwaDokumentu;
    private String typ; // Umowa, Faktura, Formularz, Raport, Reklamacja, Inne
    private Date dataDodania;
    private Boolean status;
    private String uwagi;
    @OneToMany(mappedBy="dokument", cascade = CascadeType.ALL)
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
