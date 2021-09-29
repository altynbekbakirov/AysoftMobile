package kz.burhancakmak.aysoftmobile.Models.Clients;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ClCard {

    @PrimaryKey
    private int KayitNo;
    private String Kod;
    private String Unvani1;
    private String Unvani2;
    private String FiyatGrubu;
    private String OzelKod1;
    private String OzelKod2;
    private String OzelKod3;
    private String OzelKod4;
    private String OzelKod5;
    private String Adres1;
    private String Adres2;
    private String Sehir;
    private String TicariIslemGrubu;
    private String Telefon1;
    private String Telefon2;
    private String VergiNo;
    private String IndirimOrani;
    private Double KordinatLongitude;
    private Double KordinatLatitute;
    private String IlgiliKisi1;
    private String IlgiliKisi2;
    private String EmailAdresi1;
    private String EmailAdresi2;
    private String SiparisGrubuKullanimi;
    private String SonSatisTarihi;
    private String SonTahsilatTarihi;
    private String SonSatisGunSayisi;
    private String SonTahsilatGunSayisi;
    private String Pazartesi;
    private String PazartesiSiraNo;
    private String Sali;
    private String SaliSiraNo;
    private String Carsamba;
    private String CarsambaSiraNo;
    private String Persembe;
    private String PersembeSiraNo;
    private String Cuma;
    private String CumaSiraNo;
    private String Cumartesi;
    private String CumartesiSiraNo;
    private String Pazar;
    private String PazarSiraNo;
    private Double SiparisGunleriToplami;
    private Double RiskLimiti;
    private Double NetSatis;
    private Double NetTahsilat;
    private Double Bakiye;
    private String WhatsappId;
    private String TelegramId;
    private String KartTipi;
    private String Foto1;
    private String Foto2;
    private String Foto3;
    private Integer SiparisBeklemede;
    private Integer SiparisGonderildi;
    private Integer OdemeSekli;
    private Integer DovizKayitNo;
    private String DovizIsareti;

    public ClCard() {
    }

    public int getKayitNo() {
        return KayitNo;
    }

    public void setKayitNo(int kayitNo) {
        KayitNo = kayitNo;
    }

    public String getKod() {
        return Kod;
    }

    public void setKod(String kod) {
        Kod = kod;
    }

    public String getUnvani1() {
        return Unvani1;
    }

    public void setUnvani1(String unvani1) {
        Unvani1 = unvani1;
    }

    public String getUnvani2() {
        return Unvani2;
    }

    public void setUnvani2(String unvani2) {
        Unvani2 = unvani2;
    }

    public String getFiyatGrubu() {
        return FiyatGrubu;
    }

    public void setFiyatGrubu(String fiyatGrubu) {
        FiyatGrubu = fiyatGrubu;
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

    public String getAdres1() {
        return Adres1;
    }

    public void setAdres1(String adres1) {
        Adres1 = adres1;
    }

    public String getAdres2() {
        return Adres2;
    }

    public void setAdres2(String adres2) {
        Adres2 = adres2;
    }

    public String getSehir() {
        return Sehir;
    }

    public void setSehir(String sehir) {
        Sehir = sehir;
    }

    public String getTicariIslemGrubu() {
        return TicariIslemGrubu;
    }

    public void setTicariIslemGrubu(String ticariIslemGrubu) {
        TicariIslemGrubu = ticariIslemGrubu;
    }

    public String getTelefon1() {
        return Telefon1;
    }

    public void setTelefon1(String telefon1) {
        Telefon1 = telefon1;
    }

    public String getTelefon2() {
        return Telefon2;
    }

    public void setTelefon2(String telefon2) {
        Telefon2 = telefon2;
    }

    public String getVergiNo() {
        return VergiNo;
    }

    public void setVergiNo(String vergiNo) {
        VergiNo = vergiNo;
    }

    public String getIndirimOrani() {
        return IndirimOrani;
    }

    public void setIndirimOrani(String indirimOrani) {
        IndirimOrani = indirimOrani;
    }

    public Double getKordinatLongitude() {
        return KordinatLongitude;
    }

    public void setKordinatLongitude(Double kordinatLongitude) {
        KordinatLongitude = kordinatLongitude;
    }

    public Double getKordinatLatitute() {
        return KordinatLatitute;
    }

    public void setKordinatLatitute(Double kordinatLatitute) {
        KordinatLatitute = kordinatLatitute;
    }

    public String getIlgiliKisi1() {
        return IlgiliKisi1;
    }

    public void setIlgiliKisi1(String ilgiliKisi1) {
        IlgiliKisi1 = ilgiliKisi1;
    }

    public String getIlgiliKisi2() {
        return IlgiliKisi2;
    }

    public void setIlgiliKisi2(String ilgiliKisi2) {
        IlgiliKisi2 = ilgiliKisi2;
    }

    public String getEmailAdresi1() {
        return EmailAdresi1;
    }

    public void setEmailAdresi1(String emailAdresi1) {
        EmailAdresi1 = emailAdresi1;
    }

    public String getEmailAdresi2() {
        return EmailAdresi2;
    }

    public void setEmailAdresi2(String emailAdresi2) {
        EmailAdresi2 = emailAdresi2;
    }

    public String getSiparisGrubuKullanimi() {
        return SiparisGrubuKullanimi;
    }

    public void setSiparisGrubuKullanimi(String siparisGrubuKullanimi) {
        SiparisGrubuKullanimi = siparisGrubuKullanimi;
    }

    public String getSonSatisTarihi() {
        return SonSatisTarihi;
    }

    public void setSonSatisTarihi(String sonSatisTarihi) {
        SonSatisTarihi = sonSatisTarihi;
    }

    public String getSonTahsilatTarihi() {
        return SonTahsilatTarihi;
    }

    public void setSonTahsilatTarihi(String sonTahsilatTarihi) {
        SonTahsilatTarihi = sonTahsilatTarihi;
    }

    public String getSonSatisGunSayisi() {
        return SonSatisGunSayisi;
    }

    public void setSonSatisGunSayisi(String sonSatisGunSayisi) {
        SonSatisGunSayisi = sonSatisGunSayisi;
    }

    public String getSonTahsilatGunSayisi() {
        return SonTahsilatGunSayisi;
    }

    public void setSonTahsilatGunSayisi(String sonTahsilatGunSayisi) {
        SonTahsilatGunSayisi = sonTahsilatGunSayisi;
    }

    public String getPazartesi() {
        return Pazartesi;
    }

    public void setPazartesi(String pazartesi) {
        Pazartesi = pazartesi;
    }

    public String getPazartesiSiraNo() {
        return PazartesiSiraNo;
    }

    public void setPazartesiSiraNo(String pazartesiSiraNo) {
        PazartesiSiraNo = pazartesiSiraNo;
    }

    public String getSali() {
        return Sali;
    }

    public void setSali(String sali) {
        Sali = sali;
    }

    public String getSaliSiraNo() {
        return SaliSiraNo;
    }

    public void setSaliSiraNo(String saliSiraNo) {
        SaliSiraNo = saliSiraNo;
    }

    public String getCarsamba() {
        return Carsamba;
    }

    public void setCarsamba(String carsamba) {
        Carsamba = carsamba;
    }

    public String getCarsambaSiraNo() {
        return CarsambaSiraNo;
    }

    public void setCarsambaSiraNo(String carsambaSiraNo) {
        CarsambaSiraNo = carsambaSiraNo;
    }

    public String getPersembe() {
        return Persembe;
    }

    public void setPersembe(String persembe) {
        Persembe = persembe;
    }

    public String getPersembeSiraNo() {
        return PersembeSiraNo;
    }

    public void setPersembeSiraNo(String persembeSiraNo) {
        PersembeSiraNo = persembeSiraNo;
    }

    public String getCuma() {
        return Cuma;
    }

    public void setCuma(String cuma) {
        Cuma = cuma;
    }

    public String getCumaSiraNo() {
        return CumaSiraNo;
    }

    public void setCumaSiraNo(String cumaSiraNo) {
        CumaSiraNo = cumaSiraNo;
    }

    public String getCumartesi() {
        return Cumartesi;
    }

    public void setCumartesi(String cumartesi) {
        Cumartesi = cumartesi;
    }

    public String getCumartesiSiraNo() {
        return CumartesiSiraNo;
    }

    public void setCumartesiSiraNo(String cumartesiSiraNo) {
        CumartesiSiraNo = cumartesiSiraNo;
    }

    public String getPazar() {
        return Pazar;
    }

    public void setPazar(String pazar) {
        Pazar = pazar;
    }

    public String getPazarSiraNo() {
        return PazarSiraNo;
    }

    public void setPazarSiraNo(String pazarSiraNo) {
        PazarSiraNo = pazarSiraNo;
    }

    public Double getSiparisGunleriToplami() {
        return SiparisGunleriToplami;
    }

    public void setSiparisGunleriToplami(Double siparisGunleriToplami) {
        SiparisGunleriToplami = siparisGunleriToplami;
    }

    public Double getRiskLimiti() {
        return RiskLimiti;
    }

    public void setRiskLimiti(Double riskLimiti) {
        RiskLimiti = riskLimiti;
    }

    public Double getNetSatis() {
        return NetSatis;
    }

    public void setNetSatis(Double netSatis) {
        NetSatis = netSatis;
    }

    public Double getNetTahsilat() {
        return NetTahsilat;
    }

    public void setNetTahsilat(Double netTahsilat) {
        NetTahsilat = netTahsilat;
    }

    public Double getBakiye() {
        return Bakiye;
    }

    public void setBakiye(Double bakiye) {
        Bakiye = bakiye;
    }

    public Integer getSiparisBeklemede() {
        return SiparisBeklemede;
    }

    public void setSiparisBeklemede(Integer siparisBeklemede) {
        SiparisBeklemede = siparisBeklemede;
    }

    public Integer getSiparisGonderildi() {
        return SiparisGonderildi;
    }

    public void setSiparisGonderildi(Integer siparisGonderildi) {
        SiparisGonderildi = siparisGonderildi;
    }

    public String getWhatsappId() {
        return WhatsappId;
    }

    public void setWhatsappId(String whatsappId) {
        WhatsappId = whatsappId;
    }

    public String getTelegramId() {
        return TelegramId;
    }

    public void setTelegramId(String telegramId) {
        TelegramId = telegramId;
    }

    public String getKartTipi() {
        return KartTipi;
    }

    public void setKartTipi(String kartTipi) {
        KartTipi = kartTipi;
    }

    public String getFoto1() {
        return Foto1;
    }

    public void setFoto1(String foto1) {
        Foto1 = foto1;
    }

    public String getFoto2() {
        return Foto2;
    }

    public void setFoto2(String foto2) {
        Foto2 = foto2;
    }

    public String getFoto3() {
        return Foto3;
    }

    public void setFoto3(String foto3) {
        Foto3 = foto3;
    }

    public Integer getOdemeSekli() {
        return OdemeSekli;
    }

    public void setOdemeSekli(Integer odemeSekli) {
        OdemeSekli = odemeSekli;
    }

    public Integer getDovizKayitNo() {
        return DovizKayitNo;
    }

    public void setDovizKayitNo(Integer dovizKayitNo) {
        DovizKayitNo = dovizKayitNo;
    }

    public String getDovizIsareti() {
        return DovizIsareti;
    }

    public void setDovizIsareti(String dovizIsareti) {
        DovizIsareti = dovizIsareti;
    }
}
