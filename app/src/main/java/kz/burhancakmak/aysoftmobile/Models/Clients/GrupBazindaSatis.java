package kz.burhancakmak.aysoftmobile.Models.Clients;

public class GrupBazindaSatis {
    private String MarkaKodu;
    private String Marka;
    private Integer Miktar;
    private Integer Tutar;

    public String getMarkaKodu() {
        return MarkaKodu;
    }

    public void setMarkaKodu(String markaKodu) {
        MarkaKodu = markaKodu;
    }

    public String getMarka() {
        return Marka;
    }

    public void setMarka(String marka) {
        Marka = marka;
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
