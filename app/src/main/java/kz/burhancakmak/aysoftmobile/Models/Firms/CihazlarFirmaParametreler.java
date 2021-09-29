package kz.burhancakmak.aysoftmobile.Models.Firms;

public class CihazlarFirmaParametreler {
    private Integer KayitNo;
    private Integer CihazlarFirmaKayitNo;
    private String ParametreTipi;
    private String ParametreAdi;
    private String ParametreDegeri;
    private String Aciklama;
    private Integer MobilCihazdaDegistirebilir;
    private String Grup;

    public Integer getMobilCihazdaDegistirebilir() {
        return MobilCihazdaDegistirebilir;
    }

    public void setMobilCihazdaDegistirebilir(Integer mobilCihazdaDegistirebilir) {
        MobilCihazdaDegistirebilir = mobilCihazdaDegistirebilir;
    }

    public String getGrup() {
        return Grup;
    }

    public void setGrup(String grup) {
        Grup = grup;
    }

    public Integer getKayitNo() {
        return KayitNo;
    }

    public void setKayitNo(Integer kayitNo) {
        KayitNo = kayitNo;
    }

    public Integer getCihazlarFirmaKayitNo() {
        return CihazlarFirmaKayitNo;
    }

    public void setCihazlarFirmaKayitNo(Integer cihazlarFirmaKayitNo) {
        CihazlarFirmaKayitNo = cihazlarFirmaKayitNo;
    }

    public String getParametreTipi() {
        return ParametreTipi;
    }

    public void setParametreTipi(String parametreTipi) {
        ParametreTipi = parametreTipi;
    }

    public String getParametreAdi() {
        return ParametreAdi;
    }

    public void setParametreAdi(String parametreAdi) {
        ParametreAdi = parametreAdi;
    }

    public String getParametreDegeri() {
        return ParametreDegeri;
    }

    public void setParametreDegeri(String parametreDegeri) {
        ParametreDegeri = parametreDegeri;
    }

    public String getAciklama() {
        return Aciklama;
    }

    public void setAciklama(String aciklama) {
        Aciklama = aciklama;
    }
}
