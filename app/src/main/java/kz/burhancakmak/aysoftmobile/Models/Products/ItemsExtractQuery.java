package kz.burhancakmak.aysoftmobile.Models.Products;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItemsExtractQuery {

    @SerializedName("hata")
    @Expose
    private Boolean hata;

    @SerializedName("StokEkstre")
    @Expose
    private List<String> stokekstre = null;

    @SerializedName("hataMesaj")
    @Expose
    private String hataMesaj;

    @SerializedName("200")
    @Expose
    private String _200;

    public Boolean getHata() {
        return hata;
    }

    public void setHata(Boolean hata) {
        this.hata = hata;
    }

    public List<String> getStokekstre() {
        return stokekstre;
    }

    public void setStokekstre(List<String> stokekstre) {
        this.stokekstre = stokekstre;
    }

    public String get200() {
        return _200;
    }

    public void set200(String _200) {
        this._200 = _200;
    }

    public String getHataMesaj() {
        return hataMesaj;
    }
}
