package kz.burhancakmak.aysoftmobile.Models.Products;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ItemsUnitBarcode {

    @PrimaryKey
    private int KayitNo;
    private int Itmunitaref;
    private int StokKayitNo;
    private int SatirNo;
    private String Barkod;

    public ItemsUnitBarcode() {
    }

    public int getKayitNo() {
        return KayitNo;
    }

    public void setKayitNo(int kayitNo) {
        KayitNo = kayitNo;
    }

    public int getItmunitaref() {
        return Itmunitaref;
    }

    public void setItmunitaref(int itmunitaref) {
        Itmunitaref = itmunitaref;
    }

    public int getStokKayitNo() {
        return StokKayitNo;
    }

    public void setStokKayitNo(int stokKayitNo) {
        StokKayitNo = stokKayitNo;
    }

    public int getSatirNo() {
        return SatirNo;
    }

    public void setSatirNo(int satirNo) {
        SatirNo = satirNo;
    }

    public String getBarkod() {
        return Barkod;
    }

    public void setBarkod(String barkod) {
        Barkod = barkod;
    }
}
