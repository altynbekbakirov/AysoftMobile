package kz.burhancakmak.aysoftmobile.Clients;

public class SatilanMalListesi {

    private String StokKodu;
    private String StokAdi;
    private Integer Miktar;
    private Integer Tutar;

    public String getStokKodu() {
        return StokKodu;
    }

    public void setStokKodu(String stokKodu) {
        StokKodu = stokKodu;
    }

    public String getStokAdi() {
        return StokAdi;
    }

    public void setStokAdi(String stokAdi) {
        StokAdi = stokAdi;
    }

    public Integer getMiktar() {
        return Miktar;
    }

    public void setMiktar(Integer miktar) {
        Miktar = miktar;
    }

    public Integer getTutar() {
        return Tutar;
    }

    public void setTutar(Integer tutar) {
        Tutar = tutar;
    }
}
