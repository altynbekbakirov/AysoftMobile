package kz.burhancakmak.aysoftmobile.Models.Clients;

public class ClientSiparis {

    private long KayitNo;
    private int CariKayitNo;
    private Integer ZiyaretKayitNo;
    private String CariKod;
    private String Tarih;
    private String EklenmeSaati;
    private String DegisiklikSaati;
    private String Aciklama;
    private String SiparisTeslimTarihi;
    private Integer IslemTipi;
    private Integer OdemeSekli;
    private Integer ErpGonderildi;
    private Integer ErpKayitNo;
    private String ErpSiparisFisNo;
    private Double KordinatLatitute;
    private Double KordinatLongitude;
    private String SiparisOncesiFoto1;
    private String SiparisOncesiFoto2;
    private String SiparisOncesiFoto3;
    private Double Tutar;
    private Double GenelIndirimOrani;
    private Double GenelIndirimTutari;
    private Double SatirIndirimTutari;
    private Double NetTutar;
    private Integer SevkiyatAdresiKayitno;
    private Integer GenelIndirimYontemi;

    public Integer getZiyaretKayitNo() {
        return ZiyaretKayitNo;
    }

    public void setZiyaretKayitNo(Integer ziyaretKayitNo) {
        ZiyaretKayitNo = ziyaretKayitNo;
    }

    public Integer getSevkiyatAdresiKayitno() {
        return SevkiyatAdresiKayitno;
    }

    public void setSevkiyatAdresiKayitno(Integer sevkiyatAdresiKayitno) {
        SevkiyatAdresiKayitno = sevkiyatAdresiKayitno;
    }

    public Integer getGenelIndirimYontemi() {
        return GenelIndirimYontemi;
    }

    public void setGenelIndirimYontemi(Integer genelIndirimYontemi) {
        GenelIndirimYontemi = genelIndirimYontemi;
    }

    public String getSiparisTeslimTarihi() {
        return SiparisTeslimTarihi;
    }

    public void setSiparisTeslimTarihi(String siparisTeslimTarihi) {
        SiparisTeslimTarihi = siparisTeslimTarihi;
    }

    public String getEklenmeSaati() {
        return EklenmeSaati;
    }

    public void setEklenmeSaati(String eklenmeSaati) {
        EklenmeSaati = eklenmeSaati;
    }

    public String getDegisiklikSaati() {
        return DegisiklikSaati;
    }

    public void setDegisiklikSaati(String degisiklikSaati) {
        DegisiklikSaati = degisiklikSaati;
    }

    public String getCariKod() {
        return CariKod;
    }

    public void setCariKod(String cariKod) {
        CariKod = cariKod;
    }

    public Integer getOdemeSekli() {
        return OdemeSekli;
    }

    public void setOdemeSekli(Integer odemeSekli) {
        OdemeSekli = odemeSekli;
    }

    public Integer getErpKayitNo() {
        return ErpKayitNo;
    }

    public void setErpKayitNo(Integer erpKayitNo) {
        ErpKayitNo = erpKayitNo;
    }

    public String getErpSiparisFisNo() {
        return ErpSiparisFisNo;
    }

    public void setErpSiparisFisNo(String erpSiparisFisNo) {
        ErpSiparisFisNo = erpSiparisFisNo;
    }

    public Double getKordinatLatitute() {
        return KordinatLatitute;
    }

    public void setKordinatLatitute(Double kordinatLatitute) {
        KordinatLatitute = kordinatLatitute;
    }

    public Double getKordinatLongitude() {
        return KordinatLongitude;
    }

    public void setKordinatLongitude(Double kordinatLongitude) {
        KordinatLongitude = kordinatLongitude;
    }

    public String getSiparisOncesiFoto1() {
        return SiparisOncesiFoto1;
    }

    public void setSiparisOncesiFoto1(String siparisOncesiFoto1) {
        SiparisOncesiFoto1 = siparisOncesiFoto1;
    }

    public String getSiparisOncesiFoto2() {
        return SiparisOncesiFoto2;
    }

    public void setSiparisOncesiFoto2(String siparisOncesiFoto2) {
        SiparisOncesiFoto2 = siparisOncesiFoto2;
    }

    public String getSiparisOncesiFoto3() {
        return SiparisOncesiFoto3;
    }

    public void setSiparisOncesiFoto3(String siparisOncesiFoto3) {
        SiparisOncesiFoto3 = siparisOncesiFoto3;
    }

    public ClientSiparis() {
    }

    public Integer getIslemTipi() {
        return IslemTipi;
    }

    public void setIslemTipi(Integer islemTipi) {
        IslemTipi = islemTipi;
    }

    public Double getNetTutar() {
        return NetTutar;
    }

    public void setNetTutar(Double netTutar) {
        NetTutar = netTutar;
    }

    public Double getSatirIndirimTutari() {
        return SatirIndirimTutari;
    }

    public void setSatirIndirimTutari(Double satirIndirimTutari) {
        SatirIndirimTutari = satirIndirimTutari;
    }

    public Double getGenelIndirimOrani() {
        return GenelIndirimOrani;
    }

    public void setGenelIndirimOrani(Double genelIndirimOrani) {
        GenelIndirimOrani = genelIndirimOrani;
    }

    public Double getGenelIndirimTutari() {
        return GenelIndirimTutari;
    }

    public void setGenelIndirimTutari(Double genelIndirimTutari) {
        GenelIndirimTutari = genelIndirimTutari;
    }

    public long getKayitNo() {
        return KayitNo;
    }

    public void setKayitNo(long kayitNo) {
        this.KayitNo = kayitNo;
    }

    public int getCariKayitNo() {
        return CariKayitNo;
    }

    public void setCariKayitNo(int cariKayitNo) {
        this.CariKayitNo = cariKayitNo;
    }

    public String getTarih() {
        return Tarih;
    }

    public void setTarih(String tarih) {
        this.Tarih = tarih;
    }

    public Integer getErpGonderildi() {
        return ErpGonderildi;
    }

    public void setErpGonderildi(Integer erpGonderildi) {
        this.ErpGonderildi = erpGonderildi;
    }

    public String getAciklama() {
        return Aciklama;
    }

    public void setAciklama(String aciklama) {
        this.Aciklama = aciklama;
    }

    public Double getTutar() {
        return Tutar;
    }

    public void setTutar(Double tutar) {
        this.Tutar = tutar;
    }
}
