package kz.burhancakmak.aysoftmobile.Models.Clients;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClientsMap {

    @SerializedName("hata")
    @Expose
    private Boolean hata;
    @SerializedName("CariGuncelleme")
    @Expose
    private List<String> cariGuncelleme = null;
    @SerializedName("200")
    @Expose
    private String _200;

    /**
     * No args constructor for use in serialization
     *
     */
    public ClientsMap() {
    }

    /**
     *
     * @param cariGuncelleme
     * @param hata
     * @param _200
     */
    public ClientsMap(Boolean hata, List<String> cariGuncelleme, String _200) {
        super();
        this.hata = hata;
        this.cariGuncelleme = cariGuncelleme;
        this._200 = _200;
    }

    public Boolean getHata() {
        return hata;
    }

    public void setHata(Boolean hata) {
        this.hata = hata;
    }

    public List<String> getCariGuncelleme() {
        return cariGuncelleme;
    }

    public void setCariGuncelleme(List<String> cariGuncelleme) {
        this.cariGuncelleme = cariGuncelleme;
    }

    public String get200() {
        return _200;
    }

    public void set200(String _200) {
        this._200 = _200;
    }

}
