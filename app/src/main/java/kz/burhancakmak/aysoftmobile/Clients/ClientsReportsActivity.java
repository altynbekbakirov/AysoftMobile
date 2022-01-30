package kz.burhancakmak.aysoftmobile.Clients;

import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.AylaraGoreCariSatisTahsilat;
import kz.burhancakmak.aysoftmobile.Models.Clients.CariHesapOzeti;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsDashboardQuery;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsReportsQuery;
import kz.burhancakmak.aysoftmobile.Models.Clients.GrupBazindaSatis;
import kz.burhancakmak.aysoftmobile.Models.Clients.MarkaBazindaSatisMiktarTutarGore;
import kz.burhancakmak.aysoftmobile.Models.Clients.OzelKodBazindaSatis;
import kz.burhancakmak.aysoftmobile.Models.Clients.SatilanMalListesi;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;

public class ClientsReportsActivity extends AppCompatActivity {
    SessionManagement session;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    DatabaseHandler databaseHandler;
    List<CihazlarFirmaParametreler> parametrelerList = new ArrayList<>();
    List<CariHesapOzeti> cariHesapOzetiSatisGrafik = new ArrayList<>();
    List<CariHesapOzeti> CariHesapOzetiTahsilatGrafik = new ArrayList<>();
    List<CariHesapOzeti> CariHesapOzetiSatisTahsilatGrafik = new ArrayList<>();
    List<CariHesapOzeti> CariHesapOzetiBorcGrafik = new ArrayList<>();
    List<CariHesapOzeti> CariHesapOzetiBakiyeGrafik = new ArrayList<>();
    List<CariHesapOzeti> CariHesapOzetiBorcTahsilatBakiyeGrafik = new ArrayList<>();
    List<CariHesapOzeti> CariHesapOzetiSatisTablo = new ArrayList<>();
    List<CariHesapOzeti> CariHesapOzetiTahsilatTablo = new ArrayList<>();
    List<CariHesapOzeti> CariHesapOzetiSatisTahsilatTablo = new ArrayList<>();
    List<CariHesapOzeti> CariHesapOzetiBakiyeTablo = new ArrayList<>();
    List<CariHesapOzeti> CariHesapOzetiBorcTahsilatBakiyeTablo = new ArrayList<>();
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    int clientKayitNo;
    ClCard card;
    String kurusHaneSayisiStok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        webSettingsMap = session.getWebSettings();

        if (!(userSettingMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(userSettingMap.get(KEY_LANG));
        }

        setContentView(R.layout.activity_clients_reports);

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
        parametrelerList = databaseHandler.selectParametreList(FIRMA_NO);

        if (clientKayitNo != -1) {
            card = databaseHandler.selectClientById(clientKayitNo);
            kurusHaneSayisiStok = parametreGetir("KurusHaneSayisiCari", "0");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(card.getUnvani1());
        toolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(toolbar);

        new GetDataFromWeb().execute();
    }

    private class GetDataFromWeb extends AsyncTask<Void, String, Void> {
        RelativeLayout products_progressBar = findViewById(R.id.reports_progressBar_layout);
        RetrofitApi retrofitApi;
        Call<ClientsReportsQuery> queryList;
        String hata = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
            retrofitApi = RetrofitClient.getInstance(webSettingsMap.get("web")).create(RetrofitApi.class);
            queryList = retrofitApi.getClientsReports(
                    webSettingsMap.get("uuid"),
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD),
                    FIRMA_NO,
                    DONEM_NO,
                    99,
                    card.getKayitNo(),
                    card.getKod()
            );
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Response<ClientsReportsQuery> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    ClientsReportsQuery query = response.body();
                    if (!query.getHata()) {
                        if (query.getCariHesapOzetiSatisGrafik() != null &&
                                query.getCariHesapOzetiSatisGrafik().size() > 2) {
                            publishProgress(getString(R.string.data_import_progressbar_clients));
                            for (int i = 2; i < query.getCariHesapOzetiSatisGrafik().size(); i++) {
                                String[] clients = query.getCariHesapOzetiSatisGrafik().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Double.parseDouble(clients[3]));
                                ozeti.setSubat(Double.parseDouble(clients[4]));
                                ozeti.setMart(Double.parseDouble(clients[5]));
                                ozeti.setNisan(Double.parseDouble(clients[6]));
                                ozeti.setMayis(Double.parseDouble(clients[7]));
                                ozeti.setHaziran(Double.parseDouble(clients[8]));
                                ozeti.setTemmuz(Double.parseDouble(clients[9]));
                                ozeti.setAgustos(Double.parseDouble(clients[10]));
                                ozeti.setEylul(Double.parseDouble(clients[11]));
                                ozeti.setEkim(Double.parseDouble(clients[12]));
                                ozeti.setKasim(Double.parseDouble(clients[13]));
                                ozeti.setAralik(Double.parseDouble(clients[14]));
                                ozeti.setToplamSatis(Double.parseDouble(clients[15]));
                                ozeti.setToplamTahsilat(Double.parseDouble(clients[16]));
                                ozeti.setToplamBorc(Double.parseDouble(clients[17]));
                                ozeti.setToplamAlacak(Double.parseDouble(clients[18]));
                                ozeti.setToplamBakiye(Double.parseDouble(clients[19]));
                                cariHesapOzetiSatisGrafik.add(ozeti);
                            }
                        }
                        if (query.getCariHesapOzetiTahsilatGrafik() != null &&
                                query.getCariHesapOzetiTahsilatGrafik().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiTahsilatGrafik().size(); i++) {
                                String[] clients = query.getCariHesapOzetiTahsilatGrafik().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Double.parseDouble(clients[3]));
                                ozeti.setSubat(Double.parseDouble(clients[4]));
                                ozeti.setMart(Double.parseDouble(clients[5]));
                                ozeti.setNisan(Double.parseDouble(clients[6]));
                                ozeti.setMayis(Double.parseDouble(clients[7]));
                                ozeti.setHaziran(Double.parseDouble(clients[8]));
                                ozeti.setTemmuz(Double.parseDouble(clients[9]));
                                ozeti.setAgustos(Double.parseDouble(clients[10]));
                                ozeti.setEylul(Double.parseDouble(clients[11]));
                                ozeti.setEkim(Double.parseDouble(clients[12]));
                                ozeti.setKasim(Double.parseDouble(clients[13]));
                                ozeti.setAralik(Double.parseDouble(clients[14]));
                                ozeti.setToplamSatis(Double.parseDouble(clients[15]));
                                ozeti.setToplamTahsilat(Double.parseDouble(clients[16]));
                                ozeti.setToplamBorc(Double.parseDouble(clients[17]));
                                ozeti.setToplamAlacak(Double.parseDouble(clients[18]));
                                ozeti.setToplamBakiye(Double.parseDouble(clients[19]));
                                CariHesapOzetiTahsilatGrafik.add(ozeti);
                            }
                        }
                        if (query.getCariHesapOzetiSatisTahsilatGrafik() != null
                                && query.getCariHesapOzetiSatisTahsilatGrafik().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiSatisTahsilatGrafik().size(); i++) {
                                String[] clients = query.getCariHesapOzetiSatisTahsilatGrafik().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Double.parseDouble(clients[3]));
                                ozeti.setSubat(Double.parseDouble(clients[4]));
                                ozeti.setMart(Double.parseDouble(clients[5]));
                                ozeti.setNisan(Double.parseDouble(clients[6]));
                                ozeti.setMayis(Double.parseDouble(clients[7]));
                                ozeti.setHaziran(Double.parseDouble(clients[8]));
                                ozeti.setTemmuz(Double.parseDouble(clients[9]));
                                ozeti.setAgustos(Double.parseDouble(clients[10]));
                                ozeti.setEylul(Double.parseDouble(clients[11]));
                                ozeti.setEkim(Double.parseDouble(clients[12]));
                                ozeti.setKasim(Double.parseDouble(clients[13]));
                                ozeti.setAralik(Double.parseDouble(clients[14]));
                                ozeti.setToplamSatis(Double.parseDouble(clients[15]));
                                ozeti.setToplamTahsilat(Double.parseDouble(clients[16]));
                                ozeti.setToplamBorc(Double.parseDouble(clients[17]));
                                ozeti.setToplamAlacak(Double.parseDouble(clients[18]));
                                ozeti.setToplamBakiye(Double.parseDouble(clients[19]));
                                CariHesapOzetiSatisTahsilatGrafik.add(ozeti);
                            }
                        }
                        if (query.getCariHesapOzetiBorcGrafik() != null
                                && query.getCariHesapOzetiBorcGrafik().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiBorcGrafik().size(); i++) {
                                String[] clients = query.getCariHesapOzetiBorcGrafik().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Double.parseDouble(clients[3]));
                                ozeti.setSubat(Double.parseDouble(clients[4]));
                                ozeti.setMart(Double.parseDouble(clients[5]));
                                ozeti.setNisan(Double.parseDouble(clients[6]));
                                ozeti.setMayis(Double.parseDouble(clients[7]));
                                ozeti.setHaziran(Double.parseDouble(clients[8]));
                                ozeti.setTemmuz(Double.parseDouble(clients[9]));
                                ozeti.setAgustos(Double.parseDouble(clients[10]));
                                ozeti.setEylul(Double.parseDouble(clients[11]));
                                ozeti.setEkim(Double.parseDouble(clients[12]));
                                ozeti.setKasim(Double.parseDouble(clients[13]));
                                ozeti.setAralik(Double.parseDouble(clients[14]));
                                ozeti.setToplamSatis(Double.parseDouble(clients[15]));
                                ozeti.setToplamTahsilat(Double.parseDouble(clients[16]));
                                ozeti.setToplamBorc(Double.parseDouble(clients[17]));
                                ozeti.setToplamAlacak(Double.parseDouble(clients[18]));
                                ozeti.setToplamBakiye(Double.parseDouble(clients[19]));
                                CariHesapOzetiBorcGrafik.add(ozeti);
                            }
                        }

                        if (query.getCariHesapOzetiBakiyeGrafik() != null
                                && query.getCariHesapOzetiBakiyeGrafik().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiBakiyeGrafik().size(); i++) {
                                String[] clients = query.getCariHesapOzetiBakiyeGrafik().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Double.parseDouble(clients[3]));
                                ozeti.setSubat(Double.parseDouble(clients[4]));
                                ozeti.setMart(Double.parseDouble(clients[5]));
                                ozeti.setNisan(Double.parseDouble(clients[6]));
                                ozeti.setMayis(Double.parseDouble(clients[7]));
                                ozeti.setHaziran(Double.parseDouble(clients[8]));
                                ozeti.setTemmuz(Double.parseDouble(clients[9]));
                                ozeti.setAgustos(Double.parseDouble(clients[10]));
                                ozeti.setEylul(Double.parseDouble(clients[11]));
                                ozeti.setEkim(Double.parseDouble(clients[12]));
                                ozeti.setKasim(Double.parseDouble(clients[13]));
                                ozeti.setAralik(Double.parseDouble(clients[14]));
                                ozeti.setToplamSatis(Double.parseDouble(clients[15]));
                                ozeti.setToplamTahsilat(Double.parseDouble(clients[16]));
                                ozeti.setToplamBorc(Double.parseDouble(clients[17]));
                                ozeti.setToplamAlacak(Double.parseDouble(clients[18]));
                                ozeti.setToplamBakiye(Double.parseDouble(clients[19]));
                                CariHesapOzetiBakiyeGrafik.add(ozeti);
                            }
                        }

                        if (query.getCariHesapOzetiBorcTahsilatBakiyeGrafik() != null
                                && query.getCariHesapOzetiBorcTahsilatBakiyeGrafik().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiBorcTahsilatBakiyeGrafik().size(); i++) {
                                String[] clients = query.getCariHesapOzetiBorcTahsilatBakiyeGrafik().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Double.parseDouble(clients[3]));
                                ozeti.setSubat(Double.parseDouble(clients[4]));
                                ozeti.setMart(Double.parseDouble(clients[5]));
                                ozeti.setNisan(Double.parseDouble(clients[6]));
                                ozeti.setMayis(Double.parseDouble(clients[7]));
                                ozeti.setHaziran(Double.parseDouble(clients[8]));
                                ozeti.setTemmuz(Double.parseDouble(clients[9]));
                                ozeti.setAgustos(Double.parseDouble(clients[10]));
                                ozeti.setEylul(Double.parseDouble(clients[11]));
                                ozeti.setEkim(Double.parseDouble(clients[12]));
                                ozeti.setKasim(Double.parseDouble(clients[13]));
                                ozeti.setAralik(Double.parseDouble(clients[14]));
                                ozeti.setToplamSatis(Double.parseDouble(clients[15]));
                                ozeti.setToplamTahsilat(Double.parseDouble(clients[16]));
                                ozeti.setToplamBorc(Double.parseDouble(clients[17]));
                                ozeti.setToplamAlacak(Double.parseDouble(clients[18]));
                                ozeti.setToplamBakiye(Double.parseDouble(clients[19]));
                                CariHesapOzetiBorcTahsilatBakiyeGrafik.add(ozeti);
                            }
                        }

                        if (query.getCariHesapOzetiSatisTablo() != null
                                && query.getCariHesapOzetiSatisTablo().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiSatisTablo().size(); i++) {
                                String[] clients = query.getCariHesapOzetiSatisTablo().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Double.parseDouble(clients[3]));
                                ozeti.setSubat(Double.parseDouble(clients[4]));
                                ozeti.setMart(Double.parseDouble(clients[5]));
                                ozeti.setNisan(Double.parseDouble(clients[6]));
                                ozeti.setMayis(Double.parseDouble(clients[7]));
                                ozeti.setHaziran(Double.parseDouble(clients[8]));
                                ozeti.setTemmuz(Double.parseDouble(clients[9]));
                                ozeti.setAgustos(Double.parseDouble(clients[10]));
                                ozeti.setEylul(Double.parseDouble(clients[11]));
                                ozeti.setEkim(Double.parseDouble(clients[12]));
                                ozeti.setKasim(Double.parseDouble(clients[13]));
                                ozeti.setAralik(Double.parseDouble(clients[14]));
                                ozeti.setToplamSatis(Double.parseDouble(clients[15]));
                                ozeti.setToplamTahsilat(Double.parseDouble(clients[16]));
                                ozeti.setToplamBorc(Double.parseDouble(clients[17]));
                                ozeti.setToplamAlacak(Double.parseDouble(clients[18]));
                                ozeti.setToplamBakiye(Double.parseDouble(clients[19]));
                                CariHesapOzetiSatisTablo.add(ozeti);
                            }
                        }

                        if (query.getCariHesapOzetiTahsilatTablo() != null
                                && query.getCariHesapOzetiTahsilatTablo().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiTahsilatTablo().size(); i++) {
                                String[] clients = query.getCariHesapOzetiTahsilatTablo().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Double.parseDouble(clients[3]));
                                ozeti.setSubat(Double.parseDouble(clients[4]));
                                ozeti.setMart(Double.parseDouble(clients[5]));
                                ozeti.setNisan(Double.parseDouble(clients[6]));
                                ozeti.setMayis(Double.parseDouble(clients[7]));
                                ozeti.setHaziran(Double.parseDouble(clients[8]));
                                ozeti.setTemmuz(Double.parseDouble(clients[9]));
                                ozeti.setAgustos(Double.parseDouble(clients[10]));
                                ozeti.setEylul(Double.parseDouble(clients[11]));
                                ozeti.setEkim(Double.parseDouble(clients[12]));
                                ozeti.setKasim(Double.parseDouble(clients[13]));
                                ozeti.setAralik(Double.parseDouble(clients[14]));
                                ozeti.setToplamSatis(Double.parseDouble(clients[15]));
                                ozeti.setToplamTahsilat(Double.parseDouble(clients[16]));
                                ozeti.setToplamBorc(Double.parseDouble(clients[17]));
                                ozeti.setToplamAlacak(Double.parseDouble(clients[18]));
                                ozeti.setToplamBakiye(Double.parseDouble(clients[19]));
                                CariHesapOzetiTahsilatTablo.add(ozeti);
                            }
                        }

                        if (query.getCariHesapOzetiSatisTahsilatTablo() != null
                                && query.getCariHesapOzetiSatisTahsilatTablo().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiSatisTahsilatTablo().size(); i++) {
                                String[] clients = query.getCariHesapOzetiSatisTahsilatTablo().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Double.parseDouble(clients[3]));
                                ozeti.setSubat(Double.parseDouble(clients[4]));
                                ozeti.setMart(Double.parseDouble(clients[5]));
                                ozeti.setNisan(Double.parseDouble(clients[6]));
                                ozeti.setMayis(Double.parseDouble(clients[7]));
                                ozeti.setHaziran(Double.parseDouble(clients[8]));
                                ozeti.setTemmuz(Double.parseDouble(clients[9]));
                                ozeti.setAgustos(Double.parseDouble(clients[10]));
                                ozeti.setEylul(Double.parseDouble(clients[11]));
                                ozeti.setEkim(Double.parseDouble(clients[12]));
                                ozeti.setKasim(Double.parseDouble(clients[13]));
                                ozeti.setAralik(Double.parseDouble(clients[14]));
                                ozeti.setToplamSatis(Double.parseDouble(clients[15]));
                                ozeti.setToplamTahsilat(Double.parseDouble(clients[16]));
                                ozeti.setToplamBorc(Double.parseDouble(clients[17]));
                                ozeti.setToplamAlacak(Double.parseDouble(clients[18]));
                                ozeti.setToplamBakiye(Double.parseDouble(clients[19]));
                                CariHesapOzetiSatisTahsilatTablo.add(ozeti);
                            }
                        }

                        if (query.getCariHesapOzetiBakiyeTablo() != null
                                && query.getCariHesapOzetiBakiyeTablo().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiBakiyeTablo().size(); i++) {
                                String[] clients = query.getCariHesapOzetiBakiyeTablo().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Double.parseDouble(clients[3]));
                                ozeti.setSubat(Double.parseDouble(clients[4]));
                                ozeti.setMart(Double.parseDouble(clients[5]));
                                ozeti.setNisan(Double.parseDouble(clients[6]));
                                ozeti.setMayis(Double.parseDouble(clients[7]));
                                ozeti.setHaziran(Double.parseDouble(clients[8]));
                                ozeti.setTemmuz(Double.parseDouble(clients[9]));
                                ozeti.setAgustos(Double.parseDouble(clients[10]));
                                ozeti.setEylul(Double.parseDouble(clients[11]));
                                ozeti.setEkim(Double.parseDouble(clients[12]));
                                ozeti.setKasim(Double.parseDouble(clients[13]));
                                ozeti.setAralik(Double.parseDouble(clients[14]));
                                ozeti.setToplamSatis(Double.parseDouble(clients[15]));
                                ozeti.setToplamTahsilat(Double.parseDouble(clients[16]));
                                ozeti.setToplamBorc(Double.parseDouble(clients[17]));
                                ozeti.setToplamAlacak(Double.parseDouble(clients[18]));
                                ozeti.setToplamBakiye(Double.parseDouble(clients[19]));
                                CariHesapOzetiBakiyeTablo.add(ozeti);
                            }
                        }

                        if (query.getCariHesapOzetiBorcTahsilatBakiyeTablo() != null
                                && query.getCariHesapOzetiBorcTahsilatBakiyeTablo().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiBorcTahsilatBakiyeTablo().size(); i++) {
                                String[] clients = query.getCariHesapOzetiBorcTahsilatBakiyeTablo().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Double.parseDouble(clients[3]));
                                ozeti.setSubat(Double.parseDouble(clients[4]));
                                ozeti.setMart(Double.parseDouble(clients[5]));
                                ozeti.setNisan(Double.parseDouble(clients[6]));
                                ozeti.setMayis(Double.parseDouble(clients[7]));
                                ozeti.setHaziran(Double.parseDouble(clients[8]));
                                ozeti.setTemmuz(Double.parseDouble(clients[9]));
                                ozeti.setAgustos(Double.parseDouble(clients[10]));
                                ozeti.setEylul(Double.parseDouble(clients[11]));
                                ozeti.setEkim(Double.parseDouble(clients[12]));
                                ozeti.setKasim(Double.parseDouble(clients[13]));
                                ozeti.setAralik(Double.parseDouble(clients[14]));
                                ozeti.setToplamSatis(Double.parseDouble(clients[15]));
                                ozeti.setToplamTahsilat(Double.parseDouble(clients[16]));
                                ozeti.setToplamBorc(Double.parseDouble(clients[17]));
                                ozeti.setToplamAlacak(Double.parseDouble(clients[18]));
                                ozeti.setToplamBakiye(Double.parseDouble(clients[19]));
                                CariHesapOzetiBorcTahsilatBakiyeTablo.add(ozeti);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                hata = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (hata.isEmpty()) {

            } else {
                sendDataToServerFailDialog(hata);
            }
            products_progressBar.setVisibility(View.GONE);
        }
    }

    private String parametreGetir(String param, String deger) {
        String parametreDeger = deger;
        for (CihazlarFirmaParametreler parametreler : parametrelerList) {
            if (parametreler.getParametreAdi().equals(param)) {
                parametreDeger = parametreler.getParametreDegeri();
            }
        }
        return parametreDeger;
    }

    private void sendDataToServerFailDialog(String errorMessage) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.info_warning_title);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_dangerous);
        builder.setMessage(errorMessage);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
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

}