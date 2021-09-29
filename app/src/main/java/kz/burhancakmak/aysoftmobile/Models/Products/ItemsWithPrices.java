package kz.burhancakmak.aysoftmobile.Models.Products;

public class ItemsWithPrices {

    private Integer KayitNo;
    private String StokKodu;
    private String StokAdi1;
    private Double Kalan1;
    private Double Kalan2;
    private String StokResim;
    private String StokResim1;
    private String StokResim2;
    private String StokResim3;
    private String Birim;
    private Integer Carpan2;
    private Double Fiyat1;
    private Double Fiyat2;
    private String Barkod;
    private String Barkod2;
    private Integer Miktar;
    private Integer ClientNo;
    private String UreticiKodu;
    private String OzelKod1;
    private String OzelKod2;
    private String OzelKod3;
    private String OzelKod4;
    private String OzelKod5;
    private String Birim2;
    private Integer SiparisSatinalma;
    private Integer SiparisSatis;
    private String Doviz1;
    private String Doviz2;

    public String getDoviz1() {
        return Doviz1;
    }

    public void setDoviz1(String doviz1) {
        Doviz1 = doviz1;
    }

    public String getDoviz2() {
        return Doviz2;
    }

    public void setDoviz2(String doviz2) {
        Doviz2 = doviz2;
    }

    public Integer getCarpan2() {
        return Carpan2;
    }

    public String getBarkod2() {
        return Barkod2;
    }

    public void setBarkod2(String barkod2) {
        Barkod2 = barkod2;
    }

    public void setCarpan2(Integer carpan2) {
        Carpan2 = carpan2;
    }

    public Integer getKayitNo() {
        return KayitNo;
    }

    public String getUreticiKodu() {
        return UreticiKodu;
    }

    public void setUreticiKodu(String ureticiKodu) {
        UreticiKodu = ureticiKodu;
    }

    public String getOzelKod1() {
        return OzelKod1;
    }

    public void setOzelKod1(String ozelKod1) {
        OzelKod1 = ozelKod1;
    }

    public String getOzelKod2() {
        return OzelKod2;
    }

    public void setOzelKod2(String ozelKod2) {
        OzelKod2 = ozelKod2;
    }

    public String getOzelKod3() {
        return OzelKod3;
    }

    public void setOzelKod3(String ozelKod3) {
        OzelKod3 = ozelKod3;
    }

    public String getOzelKod4() {
        return OzelKod4;
    }

    public void setOzelKod4(String ozelKod4) {
        OzelKod4 = ozelKod4;
    }

    public String getOzelKod5() {
        return OzelKod5;
    }

    public void setOzelKod5(String ozelKod5) {
        OzelKod5 = ozelKod5;
    }

    public String getBirim2() {
        return Birim2;
    }

    public void setBirim2(String birim2) {
        Birim2 = birim2;
    }

    public Integer getSiparisSatinalma() {
        return SiparisSatinalma;
    }

    public void setSiparisSatinalma(Integer siparisSatinalma) {
        SiparisSatinalma = siparisSatinalma;
    }

    public Integer getSiparisSatis() {
        return SiparisSatis;
    }

    public void setSiparisSatis(Integer siparisSatis) {
        SiparisSatis = siparisSatis;
    }

    public void setKayitNo(Integer kayitNo) {
        KayitNo = kayitNo;
    }

    public String getBarkod() {
        return Barkod;
    }

    public void setBarkod(String barkod) {
        Barkod = barkod;
    }

    public String getStokResim2() {
        return StokResim2;
    }

    public void setStokResim2(String stokResim2) {
        StokResim2 = stokResim2;
    }

    public String getStokResim3() {
        return StokResim3;
    }

    public void setStokResim3(String stokResim3) {
        StokResim3 = stokResim3;
    }

    public String getStokResim1() {
        return StokResim1;
    }

    public void setStokResim1(String stokResim1) {
        StokResim1 = stokResim1;
    }

    public String getStokKodu() {
        return StokKodu;
    }

    public void setStokKodu(String stokKodu) {
        StokKodu = stokKodu;
    }

    public String getStokAdi1() {
        return StokAdi1;
    }

    public void setStokAdi1(String stokAdi1) {
        StokAdi1 = stokAdi1;
    }

    public Double getKalan1() {
        return Kalan1;
    }

    public void setKalan1(Double kalan1) {
        Kalan1 = kalan1;
    }

    public Double getKalan2() {
        return Kalan2;
    }

    public void setKalan2(Double kalan2) {
        Kalan2 = kalan2;
    }

    public String getStokResim() {
        return StokResim;
    }

    public void setStokResim(String stokResim) {
        StokResim = stokResim;
    }

    public String getBirim() {
        return Birim;
    }

    public void setBirim(String birim) {
        Birim = birim;
    }

    public Double getFiyat1() {
        return Fiyat1;
    }

    public void setFiyat1(Double fiyat1) {
        Fiyat1 = fiyat1;
    }

    public Double getFiyat2() {
        return Fiyat2;
    }

    public void setFiyat2(Double fiyat2) {
        Fiyat2 = fiyat2;
    }

    public Integer getMiktar() {
        return Miktar;
    }

    public void setMiktar(Integer miktar) {
        Miktar = miktar;
    }

    public Integer getClientNo() {
        return ClientNo;
    }

    public void setClientNo(Integer clientNo) {
        ClientNo = clientNo;
    }

    /* @Embedded
    public Items842 items842;

    @Relation(parentColumn = "KayitNo", entityColumn = "StokKayitNo")
    public List<ItemsPrclist842> prclist842;*/
}
