package kz.burhancakmak.aysoftmobile.Models.Products;

public class ItemsDepoStokYerleri {
    private int DepoNo;
    private String DepoAdi;
    private String StokKodu;
    private int Toplam;
    private String StokYeriKodu;

    public int getDepoNo() {
        return DepoNo;
    }

    public void setDepoNo(int depoNo) {
        DepoNo = depoNo;
    }

    public String getDepoAdi() {
        return DepoAdi;
    }

    public void setDepoAdi(String depoAdi) {
        DepoAdi = depoAdi;
    }

    public String getStokKodu() {
        return StokKodu;
    }

    public void setStokKodu(String stokKodu) {
        StokKodu = stokKodu;
    }

    public int getToplam() {
        return Toplam;
    }

    public void setToplam(int toplam) {
        Toplam = toplam;
    }

    public String getStokYeriKodu() {
        return StokYeriKodu;
    }

    public void setStokYeriKodu(String stokYeriKodu) {
        StokYeriKodu = stokYeriKodu;
    }
}
