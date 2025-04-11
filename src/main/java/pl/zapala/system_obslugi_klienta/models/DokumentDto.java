package pl.zapala.system_obslugi_klienta.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

public class DokumentDto {
    private Date dataDodania;
    @NotEmpty(message= "Pole nazwy dokumentu jest wymagane.")
    private String nazwaDokumentu;
    @NotEmpty(message= "Pole typu jest wymagane.")
    private String typ;
    private String nazwaPliku;
    private Boolean status;
    private String uwagi;
    private Integer parentId;

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
    public String getNazwaPliku() {
        return nazwaPliku;
    }
    public void setNazwaPliku(String nazwaPliku) {
        this.nazwaPliku = nazwaPliku;
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
    public Integer getParentId() {
        return parentId;
    }
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
