package kz.burhancakmak.aysoftmobile.Models.Clients;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ClientsQuery {
    @SerializedName("hata")
    @Expose
    private Boolean hata;
    @SerializedName("hataMesaj")
    @Expose
    private String hataMesaj;
    @SerializedName("ClCard")
    @Expose
    private List<String> clCard = null;
    @SerializedName("ShipInfo")
    @Expose
    private List<String> shipInfo = null;
    @SerializedName("YeniCariKod")
    @Expose
    private String YeniCariKod;
    @SerializedName("YeniCariKodOzelKod1")
    @Expose
    private String YeniCariKodOzelKod1;
    @SerializedName("YeniCariKodOzelKod4")
    @Expose
    private String YeniCariKodOzelKod4;
    @SerializedName("YeniCariKodYetkiKodu")
    @Expose
    private String YeniCariKodYetkiKodu;
    @SerializedName("YeniCariOdemePlani")
    @Expose
    private String YeniCariOdemePlani;
    @SerializedName("YeniCariTicariIslemGrubu")
    @Expose
    private String YeniCariTicariIslemGrubu;
    @SerializedName("YeniCariHesapGrubu")
    @Expose
    private String YeniCariHesapGrubu;
    @SerializedName("200")
    @Expose
    private String _200;

    public ClientsQuery() {
    }

    public Boolean getHata() {
        return hata;
    }

    public List<String> getClCard() {
        return clCard;
    }

    public void setClCard(List<String> clCard) {
        this.clCard = clCard;
    }

    public List<String> getShipInfo() {
        return shipInfo;
    }

    public void setShipInfo(List<String> shipInfo) {
        this.shipInfo = shipInfo;
    }

    public String getYeniCariKod() {
        return YeniCariKod;
    }

    public void setYeniCariKod(String yeniCariKod) {
        YeniCariKod = yeniCariKod;
    }

    public String getYeniCariKodOzelKod1() {
        return YeniCariKodOzelKod1;
    }

    public void setYeniCariKodOzelKod1(String yeniCariKodOzelKod1) {
        YeniCariKodOzelKod1 = yeniCariKodOzelKod1;
    }

    public String getYeniCariKodOzelKod4() {
        return YeniCariKodOzelKod4;
    }

    public void setYeniCariKodOzelKod4(String yeniCariKodOzelKod4) {
        YeniCariKodOzelKod4 = yeniCariKodOzelKod4;
    }

    public String getYeniCariKodYetkiKodu() {
        return YeniCariKodYetkiKodu;
    }

    public void setYeniCariKodYetkiKodu(String yeniCariKodYetkiKodu) {
        YeniCariKodYetkiKodu = yeniCariKodYetkiKodu;
    }

    public String getYeniCariOdemePlani() {
        return YeniCariOdemePlani;
    }

    public void setYeniCariOdemePlani(String yeniCariOdemePlani) {
        YeniCariOdemePlani = yeniCariOdemePlani;
    }

    public String getYeniCariTicariIslemGrubu() {
        return YeniCariTicariIslemGrubu;
    }

    public void setYeniCariTicariIslemGrubu(String yeniCariTicariIslemGrubu) {
        YeniCariTicariIslemGrubu = yeniCariTicariIslemGrubu;
    }

    public String getYeniCariHesapGrubu() {
        return YeniCariHesapGrubu;
    }

    public void setYeniCariHesapGrubu(String yeniCariHesapGrubu) {
        YeniCariHesapGrubu = yeniCariHesapGrubu;
    }

    public String getHataMesaj() {
        return hataMesaj;
    }

    public String get200() {
        return _200;
    }

    public void set200(String _200) {
        this._200 = _200;
    }

}