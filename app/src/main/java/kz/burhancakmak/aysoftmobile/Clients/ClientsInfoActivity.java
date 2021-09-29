package kz.burhancakmak.aysoftmobile.Clients;

import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.R;

public class ClientsInfoActivity extends AppCompatActivity {
    SessionManagement session;
    HashMap<String, String> hashMap;
    HashMap<String, String> webSettingsMap;
    DatabaseHandler databaseHandler;
    private static final String KEY_LANG = "language";
    int clientKayitNo;
    ClCard card;
    TextView clientName, clientCode, clientAdress1, clientAdressLabel2, clientAdress2,
            clientTel1, clientTelLabel2, clientTel2, clientLocation, clientWarrantor1,
            clientWarrantorLabel2, clientWarrantor2, clientWhatsapp, clientTelegram,
            clientOzellik1, clientOzellik2, clientOzellik3, clientOzellik4,
            clientOzellik5, clientSale, clientCollection, clientBalance;
    LinearLayout telLayout2, warrantorLayout2, addressLayout2;
    String KurusHaneSayisiStokTutar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManagement(getApplicationContext());
        hashMap = session.getUserDetails();
        webSettingsMap = session.getWebSettings();
        if (!(hashMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(hashMap.get(KEY_LANG));
        }
        setContentView(R.layout.activity_clients_info);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            initViews();
        }
    }

    private void initViews() {
        Intent intent = getIntent();
        clientKayitNo = intent.getIntExtra("clientKayitNo", -1);
        databaseHandler = DatabaseHandler.getInstance(this);
        if (clientKayitNo != -1) {
            card = databaseHandler.selectClientInfo(clientKayitNo);
            KurusHaneSayisiStokTutar = parametreGetir(FIRMA_NO, "KurusHaneSayisiStokTutar", "0");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(card.getUnvani1());
        toolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(toolbar);

        clientName = findViewById(R.id.clientName);
        clientCode = findViewById(R.id.clientCode);
        clientAdress1 = findViewById(R.id.clientAdress1);
        clientAdressLabel2 = findViewById(R.id.clientAdressLabel2);
        clientAdress2 = findViewById(R.id.clientAdress2);
        clientTel1 = findViewById(R.id.clientTel1);
        clientTelLabel2 = findViewById(R.id.clientTelLabel2);
        clientTel2 = findViewById(R.id.clientTel2);
        clientLocation = findViewById(R.id.clientLocation);
        clientWarrantor1 = findViewById(R.id.clientWarrantor1);
        clientWarrantorLabel2 = findViewById(R.id.clientWarrantorLabel2);
        clientWarrantor2 = findViewById(R.id.clientWarrantor2);
        clientWhatsapp = findViewById(R.id.clientWhatsapp);
        clientTelegram = findViewById(R.id.clientTelegram);
        clientOzellik1 = findViewById(R.id.clientOzellik1);
        clientOzellik2 = findViewById(R.id.clientOzellik2);
        clientOzellik3 = findViewById(R.id.clientOzellik3);
        clientOzellik4 = findViewById(R.id.clientOzellik4);
        clientOzellik5 = findViewById(R.id.clientOzellik5);
        clientSale = findViewById(R.id.clientSale);
        clientCollection = findViewById(R.id.clientCollection);
        clientBalance = findViewById(R.id.clientBalance);
        addressLayout2 = findViewById(R.id.addressLayout2);
        warrantorLayout2 = findViewById(R.id.warrantorLayout2);
        telLayout2 = findViewById(R.id.telLayout2);

        clientName.setText(card.getUnvani1());
        clientCode.setText(card.getKod());
        clientAdress1.setText(card.getAdres1());
        if (card.getAdres2() == null || card.getAdres2().isEmpty()) {
            addressLayout2.setVisibility(View.GONE);
        } else {
            clientAdress2.setText(card.getAdres2());
        }
        clientTel1.setText(card.getTelefon1());
        if (card.getTelefon2().equals("-")) {
            telLayout2.setVisibility(View.GONE);
        } else {
            clientTel2.setText(card.getTelefon2());
        }
        clientLocation.setText(card.getKordinatLatitute() +", " + card.getKordinatLongitude());
        clientWarrantor1.setText(card.getIlgiliKisi1());
        if (card.getIlgiliKisi2() == null || card.getIlgiliKisi2().isEmpty()) {
            warrantorLayout2.setVisibility(View.GONE);
        } else {
            clientWarrantor2.setText(card.getIlgiliKisi2());
        }
        clientWhatsapp.setText(card.getWhatsappId());
        clientTelegram.setText(card.getTelegramId());
        clientOzellik1.setText(card.getOzelKod1());
        clientOzellik2.setText(card.getOzelKod2());
        clientOzellik3.setText(card.getOzelKod3());
        clientOzellik4.setText(card.getOzelKod4());
        clientOzellik5.setText(card.getOzelKod5());
        clientSale.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", card.getNetSatis()));
        clientCollection.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", card.getNetTahsilat()));
        clientBalance.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", card.getBakiye()));

    }

    private void setPhoneDefaultLanguage(String code) {
        String countryCode;

        switch (code) {
            case "Türkçe":
                countryCode = "tr";
                break;
            case "Русский":
                countryCode = "ru";
                break;
            case "English":
                countryCode = "en";
                break;
            default:
                countryCode = "en";
        }

        setLocale(this, countryCode);
    }

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private String parametreGetir(String firmaNo, String parametre, String deger) {
        List<CihazlarFirmaParametreler> parametrelerList = databaseHandler.selectParametreGetir(firmaNo, parametre);
        String parametreDeger;
        if (parametrelerList.size() == 1) {
            parametreDeger = parametrelerList.get(0).getParametreDegeri();
        } else {
            parametreDeger = deger;
        }
        return parametreDeger;
    }
}