package kz.burhancakmak.aysoftmobile.Models.Clients;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ClientsSiparisResponse {

    @SerializedName("hata")
    @Expose
    private Boolean hata;

    @SerializedName("Mesaj")
    @Expose
    private String Mesaj;

    @SerializedName("SiparisKayitNo")
    @Expose
    private Integer SiparisKayitNo;

    @SerializedName("SiparisFisNo")
    @Expose
    private String SiparisFisNo;

    @SerializedName("200")
    @Expose
    private String _200;


    public Boolean getHata() {
        return hata;
    }

    public String getMesaj() {
        return Mesaj;
    }

    public Integer getSiparisKayitNo() {
        return SiparisKayitNo;
    }

    public String getSiparisFisNo() {
        return SiparisFisNo;
    }

    public String get_200() {
        return _200;
    }

    @Override
    public String toString() {
        return "ClientsSiparisResponse{" +
                "hata=" + hata +
                ", Mesaj='" + Mesaj + '\'' +
                ", KayitNo=" + SiparisKayitNo +
                ", FisNo='" + SiparisFisNo + '\'' +
                ", _200='" + _200 + '\'' +
                '}';
    }
}