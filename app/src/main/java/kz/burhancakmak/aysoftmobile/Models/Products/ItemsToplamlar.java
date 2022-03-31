package kz.burhancakmak.aysoftmobile.Models.Products;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ItemsToplamlar {

    @PrimaryKey
    private int KayitNo;
    private int StokKayitNo;
    private int DepoNo;
    private String DepoAdi;
    private double Toplam;
    private String StokKodu;
    private String StokYeriKodu;
    private String StokAciklamasi;

    public ItemsToplamlar() {
    }

    public String getStokKodu() {
        return StokKodu;
    }

    public void setStokKodu(String stokKodu) {
        StokKodu = stokKodu;
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

    public int getDepoNo() {
        return DepoNo;
    }

    public void setDepoNo(int depoNo) {
        DepoNo = depoNo;
    }

    public String getDepoAdi() {
        return DepoAdi;
    }

    public void setDepoAdi(String depoAdi) {
        DepoAdi = depoAdi;
    }

    public double getToplam() {
        return Toplam;
    }

    public void setToplam(double toplam) {
        Toplam = toplam;
    }

    public String getStokYeriKodu() {
        return StokYeriKodu;
    }

    public void setStokYeriKodu(String stokYeriKodu) {
        StokYeriKodu = stokYeriKodu;
    }

    public String getStokAciklamasi() {
        return StokAciklamasi;
    }

    public void setStokAciklamasi(String stokAciklamasi) {
        StokAciklamasi = stokAciklamasi;
    }
}
