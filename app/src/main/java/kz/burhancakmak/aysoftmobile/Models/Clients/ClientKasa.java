package kz.burhancakmak.aysoftmobile.Models.Clients;

public class ClientKasa {
    private Integer KayitNo;
    private Integer CariKayitNo;
    private Integer ZiyaretKayitNo;
    private String Tarih;
    private Double Tutar;
    private String Aciklama;
    private String MakbuzNo;
    private Integer IslemTipi;
    private String KasaKodu;
    private Integer ErpGonderildi;
    private Integer ErpKayitNo;
    private String ErpFisNo;
    private Integer Kapatildi;

    public ClientKasa() {
    }

    public Integer getKapatildi() {
        return Kapatildi;
    }

    public void setKapatildi(Integer kapatildi) {
        Kapatildi = kapatildi;
    }

    public Integer getZiyaretKayitNo() {
        return ZiyaretKayitNo;
    }

    public void setZiyaretKayitNo(Integer ziyaretKayitNo) {
        ZiyaretKayitNo = ziyaretKayitNo;
    }

    public Integer getIslemTipi() {
        return IslemTipi;
    }

    public void setIslemTipi(Integer islemTipi) {
        IslemTipi = islemTipi;
    }

    public Integer getKayitNo() {
        return KayitNo;
    }

    public void setKayitNo(Integer kayitNo) {
        KayitNo = kayitNo;
    }

    public Integer getCariKayitNo() {
        return CariKayitNo;
    }

    public void setCariKayitNo(Integer cariKayitNo) {
        CariKayitNo = cariKayitNo;
    }

    public String getTarih() {
        return Tarih;
    }

    public void setTarih(String tarih) {
        Tarih = tarih;
    }

    public Double getTutar() {
        return Tutar;
    }

    public void setTutar(Double tutar) {
        Tutar = tutar;
    }

    public String getAciklama() {
        return Aciklama;
    }

    public void setAciklama(String aciklama) {
        Aciklama = aciklama;
    }

    public String getMakbuzNo() {
        return MakbuzNo;
    }

    public void setMakbuzNo(String makbuzNo) {
        MakbuzNo = makbuzNo;
    }

    public String getKasaKodu() {
        return KasaKodu;
    }

    public void setKasaKodu(String kasaKodu) {
        KasaKodu = kasaKodu;
    }

    public Integer getErpGonderildi() {
        return ErpGonderildi;
    }

    public void setErpGonderildi(Integer erpGonderildi) {
        ErpGonderildi = erpGonderildi;
    }

    public Integer getErpKayitNo() {
        return ErpKayitNo;
    }

    public void setErpKayitNo(Integer erpKayitNo) {
        ErpKayitNo = erpKayitNo;
    }

    public String getErpFisNo() {
        return ErpFisNo;
    }

    public void setErpFisNo(String erpFisNo) {
        ErpFisNo = erpFisNo;
    }
}
