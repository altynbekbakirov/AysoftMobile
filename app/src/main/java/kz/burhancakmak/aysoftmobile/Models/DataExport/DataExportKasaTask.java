package kz.burhancakmak.aysoftmobile.Models.DataExport;

public class DataExportKasaTask {
    private Long KayitNo;
    private Integer CariKayitNo;
    private String Kod;
    private String Unvani;
    private String Tarih;
    private Double Tutar;
    private String Aciklama;
    private String MakbuzNo;
    private Integer IslemTipi;
    private Integer ErpGonderildi;
    private Integer ErpKayitNo;
    private String ErpFisNo;
    private Boolean cbChecked;
    private Integer tur;
    private String KasaKodu;

    public DataExportKasaTask() {
    }

    public Integer getCariKayitNo() {
        return CariKayitNo;
    }

    public String getKasaKodu() {
        return KasaKodu;
    }

    public void setKasaKodu(String kasaKodu) {
        KasaKodu = kasaKodu;
    }

    public void setCariKayitNo(Integer cariKayitNo) {
        CariKayitNo = cariKayitNo;
    }

    public Boolean getCbChecked() {
        return cbChecked;
    }

    public void setCbChecked(Boolean cbChecked) {
        this.cbChecked = cbChecked;
    }

    public String getUnvani() {
        return Unvani;
    }

    public void setUnvani(String unvani) {
        Unvani = unvani;
    }

    public Integer getTur() {
        return tur;
    }

    public void setTur(Integer tur) {
        this.tur = tur;
    }

    public String getKod() {
        return Kod;
    }

    public void setKod(String kod) {
        Kod = kod;
    }

    public Integer getIslemTipi() {
        return IslemTipi;
    }

    public void setIslemTipi(Integer islemTipi) {
        IslemTipi = islemTipi;
    }

    public Long getKayitNo() {
        return KayitNo;
    }

    public void setKayitNo(Long kayitNo) {
        KayitNo = kayitNo;
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
