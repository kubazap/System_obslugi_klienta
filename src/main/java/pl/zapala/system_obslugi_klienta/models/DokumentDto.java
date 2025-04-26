package pl.zapala.system_obslugi_klienta.models;

import jakarta.validation.constraints.NotEmpty;

import java.sql.Date;

public class DokumentDto {
    private Date dataDodania;
    @NotEmpty(message= "Pole nazwy dokumentu jest wymagane.")
    private String nazwaDokumentu;
    @NotEmpty(message= "Pole typu jest wymagane.")
    private String typ;
    private Boolean status;
    private String uwagi;

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
}
