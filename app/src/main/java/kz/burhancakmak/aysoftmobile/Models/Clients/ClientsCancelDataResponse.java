package kz.burhancakmak.aysoftmobile.Models.Clients;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ClientsCancelDataResponse {

    @SerializedName("hata")
    @Expose
    private Boolean hata;

    @SerializedName("hataMesaj")
    @Expose
    private String Mesaj;


    public Boolean getHata() {
        return hata;
    }

    public void setHata(Boolean hata) {
        this.hata = hata;
    }

    public String getMesaj() {
        return Mesaj;
    }

    public void setMesaj(String mesaj) {
        Mesaj = mesaj;
    }

    @Override
    public String toString() {
        return "ClientsCancelDataResponse{" +
                "hata=" + hata +
                ", Mesaj='" + Mesaj + '\'' +
                '}';
    }
}