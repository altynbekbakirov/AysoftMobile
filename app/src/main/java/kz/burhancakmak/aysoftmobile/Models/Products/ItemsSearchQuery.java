package kz.burhancakmak.aysoftmobile.Models.Products;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItemsSearchQuery {
    @SerializedName("hata")
    @Expose
    private Boolean hata;

    @SerializedName("StokFiyatlar")
    @Expose
    private List<String> _0 = null;

    @SerializedName("StokBakiye")
    @Expose
    private List<String> _1 = null;

    @SerializedName("200")
    @Expose
    private String _200;

    public Boolean getHata() {
        return hata;
    }

    public void setHata(Boolean hata) {
        this.hata = hata;
    }

    public List<String> get0() {
        return _0;
    }

    public void set0(List<String> _0) {
        this._0 = _0;
    }

    public List<String> get1() {
        return _1;
    }

    public void set1(List<String> _1) {
        this._1 = _1;
    }

    public String get200() {
        return _200;
    }

    public void set200(String _200) {
        this._200 = _200;
    }
}
