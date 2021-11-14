package kz.burhancakmak.aysoftmobile.Models.Clients;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ClientExtractQuery {

    @SerializedName("hata")
    @Expose
    private Boolean hata;
    @SerializedName("CARIEKSTRE")
    @Expose
    private List<String> cariekstre = null;
    @SerializedName("200")
    @Expose
    private String _200;

    public ClientExtractQuery() {
    }

    public ClientExtractQuery(Boolean hata, List<String> cariekstre, String _200) {
        super();
        this.hata = hata;
        this.cariekstre = cariekstre;
        this._200 = _200;
    }

    public Boolean getHata() {
        return hata;
    }

    public void setHata(Boolean hata) {
        this.hata = hata;
    }

    public List<String> getCariekstre() {
        return cariekstre;
    }

    public void setCariekstre(List<String> cariekstre) {
        this.cariekstre = cariekstre;
    }

    public String get200() {
        return _200;
    }

    public void set200(String _200) {
        this._200 = _200;
    }

}
