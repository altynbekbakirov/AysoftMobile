package kz.burhancakmak.aysoftmobile.Models.Clients;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClientZiyaretResponse {
    @SerializedName("hata")
    @Expose
    private Boolean hata;

    @SerializedName("Mesaj")
    @Expose
    private String Mesaj;

    @SerializedName("ZiyaretKayitNo")
    @Expose
    private Integer ZiyaretKayitNo;

    @SerializedName("200")
    @Expose
    private String _200;

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

    public Integer getZiyaretKayitNo() {
        return ZiyaretKayitNo;
    }

    public void setZiyaretKayitNo(Integer ziyaretKayitNo) {
        ZiyaretKayitNo = ziyaretKayitNo;
    }

    public String get_200() {
        return _200;
    }

    public void set_200(String _200) {
        this._200 = _200;
    }
}
