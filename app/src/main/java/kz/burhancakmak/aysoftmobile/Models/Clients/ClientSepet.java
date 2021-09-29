package kz.burhancakmak.aysoftmobile.Models.Clients;

import java.io.Serializable;

public class ClientSepet implements Serializable {
    private long KayitNo;
    private Long SiparisKayitNo;
    private Integer StokKayitNo;
    private Integer VaryantKayitNo;
    private String VaryantKodu;
    private String VaryanAdi;
    private String StokKodu;
    private String StokAdi;
    private Integer StokMiktar;
    private Double StokFiyat;
    private Double StokTutar;
    private String StokBirim;
    private Double SatirIndirimOrani;
    private Double SatirIndirimTutari;
    private Double GenelIndirimTutari;
    private Double NetTutar;
    private String StokResim1;
    private String StokResim2;
    private String StokResim3;
    private String StokResim4;

    public ClientSepet() {
    }

    public Integer getStokKayitNo() {
        return StokKayitNo;
    }

    public String getVaryantKodu() {
        return VaryantKodu;
    }

    public void setVaryantKodu(String varyantKodu) {
        VaryantKodu = varyantKodu;
    }

    public String getVaryanAdi() {
        return VaryanAdi;
    }

    public void setVaryanAdi(String varyanAdi) {
        VaryanAdi = varyanAdi;
    }

    public void setStokKayitNo(Integer stokKayitNo) {
        StokKayitNo = stokKayitNo;
    }

    public Integer getVaryantKayitNo() {
        return VaryantKayitNo;
    }

    public void setVaryantKayitNo(Integer varyantKayitNo) {
        VaryantKayitNo = varyantKayitNo;
    }

    public Double getNetTutar() {
        return NetTutar;
    }

    public void setNetTutar(Double netTutar) {
        NetTutar = netTutar;
    }

    public Double getSatirIndirimOrani() {
        return SatirIndirimOrani;
    }

    public void setSatirIndirimOrani(Double satirIndirimOrani) {
        SatirIndirimOrani = satirIndirimOrani;
    }

    public Double getSatirIndirimTutari() {
        return SatirIndirimTutari;
    }

    public void setSatirIndirimTutari(Double satirIndirimTutari) {
        SatirIndirimTutari = satirIndirimTutari;
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
        KayitNo = kayitNo;
    }

    public Long getSiparisKayitNo() {
        return SiparisKayitNo;
    }

    public void setSiparisKayitNo(Long siparisKayitNo) {
        SiparisKayitNo = siparisKayitNo;
    }

    public String getStokResim1() {
        return StokResim1;
    }

    public void setStokResim1(String stokResim1) {
        this.StokResim1 = stokResim1;
    }

    public String getStokResim2() {
        return StokResim2;
    }

    public void setStokResim2(String stokResim2) {
        this.StokResim2 = stokResim2;
    }

    public String getStokResim3() {
        return StokResim3;
    }

    public void setStokResim3(String stokResim3) {
        this.StokResim3 = stokResim3;
    }

    public String getStokResim4() {
        return StokResim4;
    }

    public void setStokResim4(String stokResim4) {
        this.StokResim4 = stokResim4;
    }

    public String getStokBirim() {
        return StokBirim;
    }

    public void setStokBirim(String stokBirim) {
        this.StokBirim = stokBirim;
    }

    public String getStokAdi() {
        return StokAdi;
    }

    public void setStokAdi(String stokAdi) {
        this.StokAdi = stokAdi;
    }

    public Double getStokFiyat() {
        return StokFiyat;
    }

    public void setStokFiyat(Double stokFiyat) {
        this.StokFiyat = stokFiyat;
    }

    public Double getStokTutar() {
        return StokTutar;
    }

    public void setStokTutar(Double stokTutar) {
        this.StokTutar = stokTutar;
    }

    public String getStokKodu() {
        return StokKodu;
    }

    public void setStokKodu(String stokKodu) {
        this.StokKodu = stokKodu;
    }

    public Integer getStokMiktar() {
        return StokMiktar;
    }

    public void setStokMiktar(Integer stokMiktar) {
        this.StokMiktar = stokMiktar;
    }
}
