package kz.burhancakmak.aysoftmobile.Models.Products;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ItemsPrclist {

    @PrimaryKey
    private Integer KayitNo;
    private Integer StokKayitNo;
    private Integer BirimKayitNo;
    private String FiyatGrubu;
    private String CariHesapKodu;
    private Double Fiyat;
    private String BaslangicTarih;
    private String BitisTarih;
    private Integer DovizTipiKayitNo;
    private String DovizIsareti;

    public ItemsPrclist() {
    }

    public Integer getKayitNo() {
        return KayitNo;
    }

    public void setKayitNo(Integer kayitNo) {
        KayitNo = kayitNo;
    }

    public Integer getStokKayitNo() {
        return StokKayitNo;
    }

    public void setStokKayitNo(Integer stokKayitNo) {
        StokKayitNo = stokKayitNo;
    }

    public Integer getBirimKayitNo() {
        return BirimKayitNo;
    }

    public void setBirimKayitNo(Integer birimKayitNo) {
        BirimKayitNo = birimKayitNo;
    }

    public String getFiyatGrubu() {
        return FiyatGrubu;
    }

    public void setFiyatGrubu(String fiyatGrubu) {
        FiyatGrubu = fiyatGrubu;
    }

    public String getCariHesapKodu() {
        return CariHesapKodu;
    }

    public void setCariHesapKodu(String cariHesapKodu) {
        CariHesapKodu = cariHesapKodu;
    }

    public Double getFiyat() {
        return Fiyat;
    }

    public void setFiyat(Double fiyat) {
        Fiyat = fiyat;
    }

    public String getBaslangicTarih() {
        return BaslangicTarih;
    }

    public void setBaslangicTarih(String baslangicTarih) {
        BaslangicTarih = baslangicTarih;
    }

    public String getBitisTarih() {
        return BitisTarih;
    }

    public void setBitisTarih(String bitisTarih) {
        BitisTarih = bitisTarih;
    }

    public Integer getDovizTipiKayitNo() {
        return DovizTipiKayitNo;
    }

    public void setDovizTipiKayitNo(Integer dovizTipiKayitNo) {
        DovizTipiKayitNo = dovizTipiKayitNo;
    }

    public String getDovizIsareti() {
        return DovizIsareti;
    }

    public void setDovizIsareti(String dovizIsareti) {
        DovizIsareti = dovizIsareti;
    }
}
