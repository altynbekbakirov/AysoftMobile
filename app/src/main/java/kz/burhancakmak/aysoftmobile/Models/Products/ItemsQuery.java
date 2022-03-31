package kz.burhancakmak.aysoftmobile.Models.Products;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemsQuery {

    @SerializedName("hata")
    @Expose
    private Boolean hata;

    @SerializedName("Items")
    @Expose
    private List<String> items = null;

    @SerializedName("Items_Itmunita")
    @Expose
    private List<String> itemsItmunita = null;

    @SerializedName("Items_UnitBarcode")
    @Expose
    private List<String> itemsUnitBarcode = null;

    @SerializedName("Items_Prclist")
    @Expose
    private List<String> itemsPrclist = null;

    @SerializedName("Items_Toplamlar")
    @Expose
    private List<String> itemsToplamlar = null;

    @SerializedName("Depolar")
    @Expose
    private List<String> Depolar = null;

    @SerializedName("DepolarAdresler")
    @Expose
    private List<String> DepolarAdresler = null;

    @SerializedName("DepoStokYerleri")
    @Expose
    private List<String> DepoStokYerleri = null;

    @SerializedName("200")
    @Expose
    private String _200;

    public Boolean getHata() {
        return hata;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public List<String> getItemsItmunita() {
        return itemsItmunita;
    }

    public List<String> getItemsUnitBarcode() {
        return itemsUnitBarcode;
    }

    public List<String> getItemsPrclist() {
        return itemsPrclist;
    }

    public List<String> getItemsToplamlar() {
        return itemsToplamlar;
    }

    public List<String> getDepolar() {
        return Depolar;
    }

    public List<String> getDepolarAdresler() {
        return DepolarAdresler;
    }

    public List<String> getDepoStokYerleri() {
        return DepoStokYerleri;
    }

    public String get200() {
        return _200;
    }

    public void set200(String _200) {
        this._200 = _200;
    }

}
