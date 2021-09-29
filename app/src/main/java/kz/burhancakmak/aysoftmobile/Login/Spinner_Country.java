package kz.burhancakmak.aysoftmobile.Login;

public class Spinner_Country {
    private String countryName;
    private int countryImage;

    public Spinner_Country(String countryName, int countryImage) {
        this.countryName = countryName;
        this.countryImage = countryImage;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getCountryImage() {
        return countryImage;
    }
}
