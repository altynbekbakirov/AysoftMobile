package kz.burhancakmak.aysoftmobile.Models.Firms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CihazlarQuery {

    @SerializedName("hata")
    @Expose
    private Boolean hata;

    @SerializedName("CihazId")
    @Expose
    private String cihazId;

    @SerializedName("CihazlarFirmalar")
    @Expose
    private List<String> cihazlarFirmalar = null;

    @SerializedName("CihazlarMenu")
    @Expose
    private List<String> cihazlarMenu = null;

    @SerializedName("CihazlarFirmaParametreler")
    @Expose
    private List<String> CihazlarFirmaParametreler = null;

    @SerializedName("CihazlarFirmaOdemeSekli")
    @Expose
    private List<String> CihazlarFirmaOdemeSekli = null;

    @SerializedName("CihazlarFirmaDepolar")
    @Expose
    private List<String> CihazlarFirmaDepolar = null;

    @SerializedName("Doviz")
    @Expose
    private List<String> Doviz = null;

    @SerializedName("200")
    @Expose
    private String _200;

    public CihazlarQuery() {
    }

    public List<String> getDoviz() {
        return Doviz;
    }

    public void setDoviz(List<String> doviz) {
        Doviz = doviz;
    }

    public List<String> getCihazlarFirmaParametreler() {
        return CihazlarFirmaParametreler;
    }

    public void setCihazlarFirmaParametreler(List<String> cihazlarFirmaParametreler) {
        CihazlarFirmaParametreler = cihazlarFirmaParametreler;
    }

    public String get_200() {
        return _200;
    }

    public void set_200(String _200) {
        this._200 = _200;
    }

    public Boolean getHata() {
        return hata;
    }

    public void setHata(Boolean hata) {
        this.hata = hata;
    }

    public String getCihazId() {
        return cihazId;
    }

    public void setCihazId(String cihazId) {
        this.cihazId = cihazId;
    }

    public List<String> getCihazlarFirmalar() {
        return cihazlarFirmalar;
    }

    public void setCihazlarFirmalar(List<String> cihazlarFirmalar) {
        this.cihazlarFirmalar = cihazlarFirmalar;
    }

    public List<String> getCihazlarFirmaOdemeSekli() {
        return CihazlarFirmaOdemeSekli;
    }

    public void setCihazlarFirmaOdemeSekli(List<String> cihazlarFirmaOdemeSekli) {
        CihazlarFirmaOdemeSekli = cihazlarFirmaOdemeSekli;
    }

    public List<String> getCihazlarFirmaDepolar() {
        return CihazlarFirmaDepolar;
    }

    public void setCihazlarFirmaDepolar(List<String> cihazlarFirmaDepolar) {
        CihazlarFirmaDepolar = cihazlarFirmaDepolar;
    }

    public List<String> getCihazlarMenu() {
        return cihazlarMenu;
    }

    public void setCihazlarMenu(List<String> cihazlarMenu) {
        this.cihazlarMenu = cihazlarMenu;
    }

    public String get200() {
        return _200;
    }

    public void set200(String _200) {
        this._200 = _200;
    }

}