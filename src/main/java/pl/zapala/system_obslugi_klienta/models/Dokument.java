package pl.zapala.system_obslugi_klienta.models;

import jakarta.persistence.*;

import java.sql.Date;

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
    private String nazwaPliku;
    private String uwagi;
    private Integer parentId;

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
    public String getNazwaPliku() {
        return nazwaPliku;
    }
    public void setNazwaPliku(String nazwaPliku) {
        this.nazwaPliku = nazwaPliku;
    }
    public String getUwagi() {
        return uwagi;
    }
    public void setUwagi(String uwagi) {
        this.uwagi = uwagi;
    }
    public Integer getParentId() {
        return parentId;
    }
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
