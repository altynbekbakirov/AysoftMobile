package kz.burhancakmak.aysoftmobile.Models.Clients;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ClientsKasaResponse {

    @SerializedName("hata")
    @Expose
    private Boolean hata;

    @SerializedName("KasaHareketKayitNo")
    @Expose
    private Integer KasaHareketKayitNo;

    @SerializedName("FisNo")
    @Expose
    private String FisNo;

    @SerializedName("200")
    @Expose
    private String _200;

    public Boolean getHata() {
        return hata;
    }

    public Integer getKasaHareketKayitNo() {
        return KasaHareketKayitNo;
    }

    public String getFisNo() {
        return FisNo;
    }

    public String get_200() {
        return _200;
    }

    @Override
    public String toString() {
        return "ClientsKasaResponse{" +
                "hata=" + hata +
                ", KasaHareketKayitNo=" + KasaHareketKayitNo +
                ", FisNo='" + FisNo + '\'' +
                ", _200='" + _200 + '\'' +
                '}';
    }
}