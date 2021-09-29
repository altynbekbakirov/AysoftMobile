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

    public void setItemsItmunita(List<String> itemsItmunita) {
        this.itemsItmunita = itemsItmunita;
    }

    public List<String> getItemsUnitBarcode() {
        return itemsUnitBarcode;
    }

    public void setItemsUnitBarcode(List<String> itemsUnitBarcode) {
        this.itemsUnitBarcode = itemsUnitBarcode;
    }

    public List<String> getItemsPrclist() {
        return itemsPrclist;
    }

    public void setItemsPrclist(List<String> itemsPrclist) {
        this.itemsPrclist = itemsPrclist;
    }

    public List<String> getItemsToplamlar() {
        return itemsToplamlar;
    }

    public void setItemsToplamlar(List<String> itemsToplamlar) {
        this.itemsToplamlar = itemsToplamlar;
    }

    public String get200() {
        return _200;
    }

    public void set200(String _200) {
        this._200 = _200;
    }

}
