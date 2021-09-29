package kz.burhancakmak.aysoftmobile.Models.Firms;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cihazlarFirma")
public class CihazlarFirma {

    @PrimaryKey
    private int KayitNo;
    private int CihazKayitNo;
    private int MenuGrupKayitNo;
    private String FirmaNo;
    private String DonemNo;
    private String SatisElemaniKodu;
    private String TicariIslemGrubu;
    private int Isyeri;
    private int Depo;
    private String OndegerFiyatGrubu1;
    private String OndegerFiyatGrubu2;
    private String OndegerKasaKodu;
    private String OndegerAciklamaAlani;
    private String DepoListesi1;
    private String DepoListesi2;
    private String CariFiltre;
    private String CariSevkiyatAdresiFiltre;
    private String StokFiltre;
    private String FiyatFiltre;
    private String ResimAdresi;
    private String Depo1Aciklama1;
    private String Depo1Aciklama2;
    private String Depo2Aciklama1;
    private String Depo2Aciklama2;
    private int KurusHaneSayisi;
    private String StokEkraniGorunumSekli;
    private String Kullanim;
    private int KurusHaneSayisiStok;
    private int KurusHaneSayisiCari;
    private String GecmisFirmaNo;
    private String GecmisDonemNo;
    private String FirmaAdi1;
    private String FirmaAdi2;
    private String FirmaAdi3;
    private Integer YerelDovizKayitNo;
    private Integer RaporlamaDoviziKayitNo;

    public Integer getYerelDovizKayitNo() {
        return YerelDovizKayitNo;
    }

    public void setYerelDovizKayitNo(Integer yerelDovizKayitNo) {
        YerelDovizKayitNo = yerelDovizKayitNo;
    }

    public Integer getRaporlamaDoviziKayitNo() {
        return RaporlamaDoviziKayitNo;
    }

    public void setRaporlamaDoviziKayitNo(Integer raporlamaDoviziKayitNo) {
        RaporlamaDoviziKayitNo = raporlamaDoviziKayitNo;
    }

    public int getKurusHaneSayisi() {
        return KurusHaneSayisi;
    }

    public void setKurusHaneSayisi(int kurusHaneSayisi) {
        KurusHaneSayisi = kurusHaneSayisi;
    }

    public String getGecmisFirmaNo() {
        return GecmisFirmaNo;
    }

    public void setGecmisFirmaNo(String gecmisFirmaNo) {
        GecmisFirmaNo = gecmisFirmaNo;
    }

    public String getGecmisDonemNo() {
        return GecmisDonemNo;
    }

    public void setGecmisDonemNo(String gecmisDonemNo) {
        GecmisDonemNo = gecmisDonemNo;
    }

    public String getFirmaAdi1() {
        return FirmaAdi1;
    }

    public void setFirmaAdi1(String firmaAdi1) {
        FirmaAdi1 = firmaAdi1;
    }

    public String getFirmaAdi2() {
        return FirmaAdi2;
    }

    public void setFirmaAdi2(String firmaAdi2) {
        FirmaAdi2 = firmaAdi2;
    }

    public String getFirmaAdi3() {
        return FirmaAdi3;
    }

    public void setFirmaAdi3(String firmaAdi3) {
        FirmaAdi3 = firmaAdi3;
    }

    public String getDepo1Aciklama1() {
        return Depo1Aciklama1;
    }

    public void setDepo1Aciklama1(String depo1Aciklama1) {
        Depo1Aciklama1 = depo1Aciklama1;
    }

    public String getDepo1Aciklama2() {
        return Depo1Aciklama2;
    }

    public void setDepo1Aciklama2(String depo1Aciklama2) {
        Depo1Aciklama2 = depo1Aciklama2;
    }

    public String getDepo2Aciklama1() {
        return Depo2Aciklama1;
    }

    public void setDepo2Aciklama1(String depo2Aciklama1) {
        Depo2Aciklama1 = depo2Aciklama1;
    }

    public String getDepo2Aciklama2() {
        return Depo2Aciklama2;
    }

    public void setDepo2Aciklama2(String depo2Aciklama2) {
        Depo2Aciklama2 = depo2Aciklama2;
    }


    public String getStokEkraniGorunumSekli() {
        return StokEkraniGorunumSekli;
    }

    public void setStokEkraniGorunumSekli(String stokEkraniGorunumSekli) {
        StokEkraniGorunumSekli = stokEkraniGorunumSekli;
    }

    public int getKurusHaneSayisiStok() {
        return KurusHaneSayisiStok;
    }

    public void setKurusHaneSayisiStok(int kurusHaneSayisiStok) {
        KurusHaneSayisiStok = kurusHaneSayisiStok;
    }

    public int getKurusHaneSayisiCari() {
        return KurusHaneSayisiCari;
    }

    public void setKurusHaneSayisiCari(int kurusHaneSayisiCari) {
        KurusHaneSayisiCari = kurusHaneSayisiCari;
    }

    public CihazlarFirma() {
    }

    public String getResimAdresi() {
        return ResimAdresi;
    }

    public void setResimAdresi(String resimAdresi) {
        ResimAdresi = resimAdresi;
    }

    public int getKayitNo() {
        return KayitNo;
    }

    public void setKayitNo(int kayitNo) {
        KayitNo = kayitNo;
    }

    public int getCihazKayitNo() {
        return CihazKayitNo;
    }

    public void setCihazKayitNo(int cihazKayitNo) {
        CihazKayitNo = cihazKayitNo;
    }

    public int getMenuGrupKayitNo() {
        return MenuGrupKayitNo;
    }

    public void setMenuGrupKayitNo(int menuGrupKayitNo) {
        MenuGrupKayitNo = menuGrupKayitNo;
    }

    public String getFirmaNo() {
        return FirmaNo;
    }

    public void setFirmaNo(String firmaNo) {
        FirmaNo = firmaNo;
    }

    public String getDonemNo() {
        return DonemNo;
    }

    public void setDonemNo(String donemNo) {
        DonemNo = donemNo;
    }

    public String getSatisElemaniKodu() {
        return SatisElemaniKodu;
    }

    public void setSatisElemaniKodu(String satisElemaniKodu) {
        SatisElemaniKodu = satisElemaniKodu;
    }

    public String getTicariIslemGrubu() {
        return TicariIslemGrubu;
    }

    public void setTicariIslemGrubu(String ticariIslemGrubu) {
        TicariIslemGrubu = ticariIslemGrubu;
    }

    public int getIsyeri() {
        return Isyeri;
    }

    public void setIsyeri(int isyeri) {
        Isyeri = isyeri;
    }

    public int getDepo() {
        return Depo;
    }

    public void setDepo(int depo) {
        Depo = depo;
    }

    public String getOndegerFiyatGrubu1() {
        return OndegerFiyatGrubu1;
    }

    public void setOndegerFiyatGrubu1(String ondegerFiyatGrubu1) {
        OndegerFiyatGrubu1 = ondegerFiyatGrubu1;
    }

    public String getOndegerFiyatGrubu2() {
        return OndegerFiyatGrubu2;
    }

    public void setOndegerFiyatGrubu2(String ondegerFiyatGrubu2) {
        OndegerFiyatGrubu2 = ondegerFiyatGrubu2;
    }

    public String getOndegerKasaKodu() {
        return OndegerKasaKodu;
    }

    public void setOndegerKasaKodu(String ondegerKasaKodu) {
        OndegerKasaKodu = ondegerKasaKodu;
    }

    public String getOndegerAciklamaAlani() {
        return OndegerAciklamaAlani;
    }

    public void setOndegerAciklamaAlani(String ondegerAciklamaAlani) {
        OndegerAciklamaAlani = ondegerAciklamaAlani;
    }


    public String getCariFiltre() {
        return CariFiltre;
    }

    public void setCariFiltre(String cariFiltre) {
        CariFiltre = cariFiltre;
    }

    public String getCariSevkiyatAdresiFiltre() {
        return CariSevkiyatAdresiFiltre;
    }

    public void setCariSevkiyatAdresiFiltre(String cariSevkiyatAdresiFiltre) {
        CariSevkiyatAdresiFiltre = cariSevkiyatAdresiFiltre;
    }

    public String getStokFiltre() {
        return StokFiltre;
    }

    public void setStokFiltre(String stokFiltre) {
        StokFiltre = stokFiltre;
    }

    public String getFiyatFiltre() {
        return FiyatFiltre;
    }

    public void setFiyatFiltre(String fiyatFiltre) {
        FiyatFiltre = fiyatFiltre;
    }

    public String getKullanim() {
        return Kullanim;
    }

    public void setKullanim(String kullanim) {
        Kullanim = kullanim;
    }

    public String getDepoListesi1() {
        return DepoListesi1;
    }

    public void setDepoListesi1(String depoListesi1) {
        DepoListesi1 = depoListesi1;
    }

    public String getDepoListesi2() {
        return DepoListesi2;
    }

    public void setDepoListesi2(String depoListesi2) {
        DepoListesi2 = depoListesi2;
    }

    @Override
    public String toString() {
        return "CihazlarFirma{" +
                "KayitNo=" + KayitNo +
                ", FirmaNo=" + FirmaNo +
                ", OndegerFiyatGrubu1='" + OndegerFiyatGrubu1 + '\'' +
                ", OndegerFiyatGrubu2='" + OndegerFiyatGrubu2 + '\'' +
                '}';
    }
}
