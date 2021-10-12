package kz.burhancakmak.aysoftmobile.Models.Firms;

public class CihazlarMenu {


    private Integer KayitNo;
    private Integer MenuGrupKayitNo;
    private Integer Tip;
    private String Aciklama1;
    private String Aciklama2;
    private String Filtre;
    private String Siralama;
    private Integer Ondeger;
    private String Kullanim;
    private String SiraNo;
    private Integer UstMenuKayitNo;
    private Integer MenuTipi;

    public String getSiraNo() {
        return SiraNo;
    }

    public void setSiraNo(String siraNo) {
        SiraNo = siraNo;
    }

    public Integer getUstMenuKayitNo() {
        return UstMenuKayitNo;
    }

    public void setUstMenuKayitNo(Integer ustMenuKayitNo) {
        UstMenuKayitNo = ustMenuKayitNo;
    }

    public Integer getMenuTipi() {
        return MenuTipi;
    }

    public void setMenuTipi(Integer menuTipi) {
        MenuTipi = menuTipi;
    }

    public String getSiralama() {
        return Siralama;
    }

    public void setSiralama(String siralama) {
        Siralama = siralama;
    }

    public Integer getOndeger() {
        return Ondeger;
    }

    public void setOndeger(Integer ondeger) {
        Ondeger = ondeger;
    }

    public Integer getKayitNo() {
        return KayitNo;
    }

    public void setKayitNo(Integer kayitNo) {
        KayitNo = kayitNo;
    }

    public Integer getMenuGrupKayitNo() {
        return MenuGrupKayitNo;
    }

    public void setMenuGrupKayitNo(Integer menuGrupKayitNo) {
        MenuGrupKayitNo = menuGrupKayitNo;
    }

    public Integer getTip() {
        return Tip;
    }

    public void setTip(Integer tip) {
        Tip = tip;
    }

    public String getAciklama1() {
        return Aciklama1;
    }

    public void setAciklama1(String aciklama1) {
        Aciklama1 = aciklama1;
    }

    public String getAciklama2() {
        return Aciklama2;
    }

    public void setAciklama2(String aciklama2) {
        Aciklama2 = aciklama2;
    }

    public String getFiltre() {
        return Filtre;
    }

    public void setFiltre(String filtre) {
        Filtre = filtre;
    }

    public String getKullanim() {
        return Kullanim;
    }

    public void setKullanim(String kullanim) {
        Kullanim = kullanim;
    }
}
