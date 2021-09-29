package kz.burhancakmak.aysoftmobile.Models.Clients;

public class ClientZiyaret {
    private Integer KayitNo;
    private Integer CariKayitNo;
    private Double BeginKordinatLongitude;
    private Double BeginKordinatLatitute;
    private Double EndKordinatLongitude;
    private Double EndKordinatLatitute;
    private String BaslangicTarihi;
    private String BaslangicSaati;
    private String BitisTarihi;
    private String BitisSaati;
    private String Notlar;
    private Integer ErpGonderildi;
    private Integer Kapatildi;

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

    public Double getBeginKordinatLongitude() {
        return BeginKordinatLongitude;
    }

    public void setBeginKordinatLongitude(Double beginKordinatLongitude) {
        BeginKordinatLongitude = beginKordinatLongitude;
    }

    public Double getEndKordinatLongitude() {
        return EndKordinatLongitude;
    }

    public void setEndKordinatLongitude(Double endKordinatLongitude) {
        EndKordinatLongitude = endKordinatLongitude;
    }

    public Double getEndKordinatLatitute() {
        return EndKordinatLatitute;
    }

    public void setEndKordinatLatitute(Double endKordinatLatitute) {
        EndKordinatLatitute = endKordinatLatitute;
    }

    public Double getBeginKordinatLatitute() {
        return BeginKordinatLatitute;
    }

    public void setBeginKordinatLatitute(Double beginKordinatLatitute) {
        BeginKordinatLatitute = beginKordinatLatitute;
    }

    public String getBaslangicTarihi() {
        return BaslangicTarihi;
    }

    public void setBaslangicTarihi(String baslangicTarihi) {
        BaslangicTarihi = baslangicTarihi;
    }

    public String getBaslangicSaati() {
        return BaslangicSaati;
    }

    public void setBaslangicSaati(String baslangicSaati) {
        BaslangicSaati = baslangicSaati;
    }

    public String getBitisTarihi() {
        return BitisTarihi;
    }

    public void setBitisTarihi(String bitisTarihi) {
        BitisTarihi = bitisTarihi;
    }

    public String getBitisSaati() {
        return BitisSaati;
    }

    public void setBitisSaati(String bitisSaati) {
        BitisSaati = bitisSaati;
    }

    public String getNotlar() {
        return Notlar;
    }

    public void setNotlar(String notlar) {
        Notlar = notlar;
    }

    public Integer getErpGonderildi() {
        return ErpGonderildi;
    }

    public void setErpGonderildi(Integer erpGonderildi) {
        ErpGonderildi = erpGonderildi;
    }

    public Integer getKapatildi() {
        return Kapatildi;
    }

    public void setKapatildi(Integer kapatildi) {
        Kapatildi = kapatildi;
    }
}
