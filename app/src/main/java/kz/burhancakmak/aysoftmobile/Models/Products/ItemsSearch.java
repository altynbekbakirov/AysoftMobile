package kz.burhancakmak.aysoftmobile.Models.Products;

public class ItemsSearch {
    private String StokKodu;
    private String StokAdiRu;
    private String StokAdiTr;
    private String Barkod;
    private Integer Carpan1;
    private Integer Carpan2;
    private String Birim;
    private Double Fiyat;
    private Integer BirimCevrim;
    private String SonTarih;
    private String FiyatBirimKod;
    private String DovizKod;
    private Double SatilacakFiyat;
    private String SatilacakBirimKod;
    private Integer SatilacakBirimCarpan;
    private Double GunlukKur;
    private String depono;
    private String depoadi;
    private Integer miktar;
    private String satilacakbirim;
    private Integer satilacakbirimcarpan;
    private Integer satilacakmiktar;
    private String stoklokasyon;

    public String getStokKodu() {
        return StokKodu;
    }

    public void setStokKodu(String stokKodu) {
        StokKodu = stokKodu;
    }

    public String getStokAdiRu() {
        return StokAdiRu;
    }

    public void setStokAdiRu(String stokAdiRu) {
        StokAdiRu = stokAdiRu;
    }

    public String getStokAdiTr() {
        return StokAdiTr;
    }

    public void setStokAdiTr(String stokAdiTr) {
        StokAdiTr = stokAdiTr;
    }

    public String getBarkod() {
        return Barkod;
    }

    public void setBarkod(String barkod) {
        Barkod = barkod;
    }

    public Integer getCarpan1() {
        return Carpan1;
    }

    public void setCarpan1(Integer carpan1) {
        Carpan1 = carpan1;
    }

    public Integer getCarpan2() {
        return Carpan2;
    }

    public void setCarpan2(Integer carpan2) {
        Carpan2 = carpan2;
    }

    public String getBirim() {
        return Birim;
    }

    public void setBirim(String birim) {
        Birim = birim;
    }

    public Double getFiyat() {
        return Fiyat;
    }

    public void setFiyat(Double fiyat) {
        Fiyat = fiyat;
    }

    public Integer getBirimCevrim() {
        return BirimCevrim;
    }

    public void setBirimCevrim(Integer birimCevrim) {
        BirimCevrim = birimCevrim;
    }

    public String getSonTarih() {
        return SonTarih;
    }

    public void setSonTarih(String sonTarih) {
        SonTarih = sonTarih;
    }

    public String getFiyatBirimKod() {
        return FiyatBirimKod;
    }

    public void setFiyatBirimKod(String fiyatBirimKod) {
        FiyatBirimKod = fiyatBirimKod;
    }

    public String getDovizKod() {
        return DovizKod;
    }

    public void setDovizKod(String dovizKod) {
        DovizKod = dovizKod;
    }

    public Double getSatilacakFiyat() {
        return SatilacakFiyat;
    }

    public void setSatilacakFiyat(Double satilacakFiyat) {
        SatilacakFiyat = satilacakFiyat;
    }

    public String getSatilacakBirimKod() {
        return SatilacakBirimKod;
    }

    public void setSatilacakBirimKod(String satilacakBirimKod) {
        SatilacakBirimKod = satilacakBirimKod;
    }

    public Integer getSatilacakBirimCarpan() {
        return SatilacakBirimCarpan;
    }

    public void setSatilacakBirimCarpan(Integer satilacakBirimCarpan) {
        SatilacakBirimCarpan = satilacakBirimCarpan;
    }

    public Double getGunlukKur() {
        return GunlukKur;
    }

    public void setGunlukKur(Double gunlukKur) {
        GunlukKur = gunlukKur;
    }

    public String getDepono() {
        return depono;
    }

    public void setDepono(String depono) {
        this.depono = depono;
    }

    public String getDepoadi() {
        return depoadi;
    }

    public void setDepoadi(String depoadi) {
        this.depoadi = depoadi;
    }

    public Integer getMiktar() {
        return miktar;
    }

    public void setMiktar(Integer miktar) {
        this.miktar = miktar;
    }

    public String getSatilacakbirim() {
        return satilacakbirim;
    }

    public void setSatilacakbirim(String satilacakbirim) {
        this.satilacakbirim = satilacakbirim;
    }

    public Integer getSatilacakbirimcarpan() {
        return satilacakbirimcarpan;
    }

    public void setSatilacakbirimcarpan(Integer satilacakbirimcarpan) {
        this.satilacakbirimcarpan = satilacakbirimcarpan;
    }

    public Integer getSatilacakmiktar() {
        return satilacakmiktar;
    }

    public void setSatilacakmiktar(Integer satilacakmiktar) {
        this.satilacakmiktar = satilacakmiktar;
    }

    public String getStoklokasyon() {
        return stoklokasyon;
    }

    public void setStoklokasyon(String stoklokasyon) {
        this.stoklokasyon = stoklokasyon;
    }

    @Override
    public String toString() {
        return "ItemsSearch{" +
                "StokKodu='" + StokKodu + '\'' +
                ", StokAdiRu='" + StokAdiRu + '\'' +
                ", StokAdiTr='" + StokAdiTr + '\'' +
                ", Barkod='" + Barkod + '\'' +
                '}';
    }
}
