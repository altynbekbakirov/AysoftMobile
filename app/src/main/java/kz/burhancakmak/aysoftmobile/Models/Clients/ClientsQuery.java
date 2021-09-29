package kz.burhancakmak.aysoftmobile.Models.Clients;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ClientsQuery {

    @SerializedName("hata")
    @Expose
    private Boolean hata;
    @SerializedName("ClCard")
    @Expose
    private List<String> clCard = null;
    @SerializedName("ShipInfo")
    @Expose
    private List<String> shipInfo = null;
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

    public String get200() {
        return _200;
    }

    public void set200(String _200) {
        this._200 = _200;
    }

}