package kz.burhancakmak.aysoftmobile.Models.Products;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ItemsItmunita {

    @PrimaryKey
    private int KayitNo;
    private int StokKayitNo;
    private int SiraNo;
    private int BirimSiraKayitNo;
    private String Birim;
    private double Hacim;
    private double Agirlik;
    private int Carpan1;
    private int Carpan2;

    public ItemsItmunita() {
    }

    public int getKayitNo() {
        return KayitNo;
    }

    public void setKayitNo(int kayitNo) {
        KayitNo = kayitNo;
    }

    public int getStokKayitNo() {
        return StokKayitNo;
    }

    public void setStokKayitNo(int stokKayitNo) {
        StokKayitNo = stokKayitNo;
    }

    public int getSiraNo() {
        return SiraNo;
    }

    public void setSiraNo(int siraNo) {
        SiraNo = siraNo;
    }

    public int getBirimSiraKayitNo() {
        return BirimSiraKayitNo;
    }

    public void setBirimSiraKayitNo(int birimSiraKayitNo) {
        BirimSiraKayitNo = birimSiraKayitNo;
    }

    public String getBirim() {
        return Birim;
    }

    public void setBirim(String birim) {
        Birim = birim;
    }

    public double getHacim() {
        return Hacim;
    }

    public void setHacim(double hacim) {
        Hacim = hacim;
    }

    public double getAgirlik() {
        return Agirlik;
    }

    public void setAgirlik(double agirlik) {
        Agirlik = agirlik;
    }

    public int getCarpan1() {
        return Carpan1;
    }

    public void setCarpan1(int carpan1) {
        Carpan1 = carpan1;
    }

    public int getCarpan2() {
        return Carpan2;
    }

    public void setCarpan2(int carpan2) {
        Carpan2 = carpan2;
    }
}
