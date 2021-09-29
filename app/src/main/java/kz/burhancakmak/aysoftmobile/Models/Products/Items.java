package kz.burhancakmak.aysoftmobile.Models.Products;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Items {

    @PrimaryKey
    private Integer KayitNo;
    private String StokKodu;
    private String UreticiKodu;
    private String StokAdi1;
    private String StokAdi2;
    private String StokAdi3;
    private String VKod;
    private String VAciklama1;
    private String VAciklama2;
    private String VAciklama3;
    private String ResimDosyasiKucuk;
    private String ResimDosyasiBuyuk1;
    private String ResimDosyasiBuyuk2;
    private String ResimDosyasiBuyuk3;
    private String GrupKodu;
    private String OzelKod1;
    private String OzelKod2;
    private String OzelKod3;
    private String OzelKod4;
    private String OzelKod5;
    private String MarkaKodu;
    private String YetkiKodu;
    private Integer SiparisGrubu;
    private Integer IndirimYapilamaz;
    private Double Kalan1;
    private Double Kalan2;
    private String NewIcon;
    private String IndirimIcon;
    private String KampanyaIcon;
    private Integer SiparisCarpani;

    public Items() {
    }

    public String getNewIcon() {
        return NewIcon;
    }

    public void setNewIcon(String newIcon) {
        NewIcon = newIcon;
    }

    public String getIndirimIcon() {
        return IndirimIcon;
    }

    public void setIndirimIcon(String indirimIcon) {
        IndirimIcon = indirimIcon;
    }

    public String getKampanyaIcon() {
        return KampanyaIcon;
    }

    public void setKampanyaIcon(String kampanyaIcon) {
        KampanyaIcon = kampanyaIcon;
    }

    public Integer getSiparisCarpani() {
        return SiparisCarpani;
    }

    public void setSiparisCarpani(Integer siparisCarpani) {
        SiparisCarpani = siparisCarpani;
    }

    public Integer getSiparisGrubu() {
        return SiparisGrubu;
    }

    public void setSiparisGrubu(Integer siparisGrubu) {
        SiparisGrubu = siparisGrubu;
    }

    public Integer getKayitNo() {
        return KayitNo;
    }

    public void setKayitNo(Integer kayitNo) {
        KayitNo = kayitNo;
    }

    public String getStokKodu() {
        return StokKodu;
    }

    public void setStokKodu(String stokKodu) {
        StokKodu = stokKodu;
    }

    public String getUreticiKodu() {
        return UreticiKodu;
    }

    public void setUreticiKodu(String ureticiKodu) {
        UreticiKodu = ureticiKodu;
    }

    public String getStokAdi1() {
        return StokAdi1;
    }

    public void setStokAdi1(String stokAdi1) {
        StokAdi1 = stokAdi1;
    }

    public String getStokAdi2() {
        return StokAdi2;
    }

    public void setStokAdi2(String stokAdi2) {
        StokAdi2 = stokAdi2;
    }

    public String getStokAdi3() {
        return StokAdi3;
    }

    public void setStokAdi3(String stokAdi3) {
        StokAdi3 = stokAdi3;
    }

    public String getVKod() {
        return VKod;
    }

    public void setVKod(String VKod) {
        this.VKod = VKod;
    }

    public String getVAciklama1() {
        return VAciklama1;
    }

    public void setVAciklama1(String VAciklama1) {
        this.VAciklama1 = VAciklama1;
    }

    public String getVAciklama2() {
        return VAciklama2;
    }

    public void setVAciklama2(String VAciklama2) {
        this.VAciklama2 = VAciklama2;
    }

    public String getVAciklama3() {
        return VAciklama3;
    }

    public void setVAciklama3(String VAciklama3) {
        this.VAciklama3 = VAciklama3;
    }

    public String getResimDosyasiKucuk() {
        return ResimDosyasiKucuk;
    }

    public void setResimDosyasiKucuk(String resimDosyasiKucuk) {
        ResimDosyasiKucuk = resimDosyasiKucuk;
    }

    public String getResimDosyasiBuyuk1() {
        return ResimDosyasiBuyuk1;
    }

    public void setResimDosyasiBuyuk1(String resimDosyasiBuyuk1) {
        ResimDosyasiBuyuk1 = resimDosyasiBuyuk1;
    }

    public String getResimDosyasiBuyuk2() {
        return ResimDosyasiBuyuk2;
    }

    public void setResimDosyasiBuyuk2(String resimDosyasiBuyuk2) {
        ResimDosyasiBuyuk2 = resimDosyasiBuyuk2;
    }

    public String getResimDosyasiBuyuk3() {
        return ResimDosyasiBuyuk3;
    }

    public void setResimDosyasiBuyuk3(String resimDosyasiBuyuk3) {
        ResimDosyasiBuyuk3 = resimDosyasiBuyuk3;
    }

    public String getGrupKodu() {
        return GrupKodu;
    }

    public void setGrupKodu(String grupKodu) {
        GrupKodu = grupKodu;
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

    public String getMarkaKodu() {
        return MarkaKodu;
    }

    public void setMarkaKodu(String markaKodu) {
        MarkaKodu = markaKodu;
    }

    public String getYetkiKodu() {
        return YetkiKodu;
    }

    public void setYetkiKodu(String yetkiKodu) {
        YetkiKodu = yetkiKodu;
    }

    public Integer getIndirimYapilamaz() {
        return IndirimYapilamaz;
    }

    public void setIndirimYapilamaz(Integer indirimYapilamaz) {
        IndirimYapilamaz = indirimYapilamaz;
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

}
