package kz.burhancakmak.aysoftmobile.Models.Clients;

public class SatilanMalListesi {

    private String StokKodu;
    private String StokAdi;
    private Float Miktar;
    private Float Tutar;

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

    public Float getMiktar() {
        return Miktar;
    }

    public void setMiktar(Float miktar) {
        Miktar = miktar;
    }

    public Float getTutar() {
        return Tutar;
    }

    public void setTutar(Float tutar) {
        Tutar = tutar;
    }
}
