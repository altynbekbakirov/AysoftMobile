package kz.burhancakmak.aysoftmobile.Clients;

import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.CariHesapOzeti;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsReportsQuery;
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
    CariHesapOzeti cari1 = null;
    CariHesapOzeti cari2 = null;
    CariHesapOzeti cari3 = null;
    CariHesapOzeti cari4 = null;
    CariHesapOzeti cari5 = null;
    CariHesapOzeti cari6 = null;
    CariHesapOzeti cari7 = null;
    CariHesapOzeti cari8 = null;
    CariHesapOzeti cari9 = null;
    CariHesapOzeti cari10 = null;
    CariHesapOzeti cari11 = null;
    CariHesapOzeti cari12 = null;
    CariHesapOzeti cari13 = null;
    CariHesapOzeti cari14 = null;
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
        toolbar.setTitle(R.string.main_nav_reports);
        toolbar.setSubtitle(card.getUnvani1());
        toolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setSubat(Float.parseFloat(clients[4]));
                                ozeti.setMart(Float.parseFloat(clients[5]));
                                ozeti.setNisan(Float.parseFloat(clients[6]));
                                ozeti.setMayis(Float.parseFloat(clients[7]));
                                ozeti.setHaziran(Float.parseFloat(clients[8]));
                                ozeti.setTemmuz(Float.parseFloat(clients[9]));
                                ozeti.setAgustos(Float.parseFloat(clients[10]));
                                ozeti.setEylul(Float.parseFloat(clients[11]));
                                ozeti.setEkim(Float.parseFloat(clients[12]));
                                ozeti.setKasim(Float.parseFloat(clients[13]));
                                ozeti.setAralik(Float.parseFloat(clients[14]));
                                ozeti.setToplamSatis(Float.parseFloat(clients[15]));
                                cari1 = ozeti;
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
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setSubat(Float.parseFloat(clients[4]));
                                ozeti.setMart(Float.parseFloat(clients[5]));
                                ozeti.setNisan(Float.parseFloat(clients[6]));
                                ozeti.setMayis(Float.parseFloat(clients[7]));
                                ozeti.setHaziran(Float.parseFloat(clients[8]));
                                ozeti.setTemmuz(Float.parseFloat(clients[9]));
                                ozeti.setAgustos(Float.parseFloat(clients[10]));
                                ozeti.setEylul(Float.parseFloat(clients[11]));
                                ozeti.setEkim(Float.parseFloat(clients[12]));
                                ozeti.setKasim(Float.parseFloat(clients[13]));
                                ozeti.setAralik(Float.parseFloat(clients[14]));
                                ozeti.setToplamSatis(Float.parseFloat(clients[15]));
                                cari2 = ozeti;
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
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setOcakTahsilat(Float.parseFloat(clients[4]));
                                ozeti.setSubat(Float.parseFloat(clients[5]));
                                ozeti.setSubatTahsilat(Float.parseFloat(clients[6]));
                                ozeti.setMart(Float.parseFloat(clients[7]));
                                ozeti.setMartTahsilat(Float.parseFloat(clients[8]));
                                ozeti.setNisan(Float.parseFloat(clients[9]));
                                ozeti.setNisanTahsilat(Float.parseFloat(clients[10]));
                                ozeti.setMayis(Float.parseFloat(clients[11]));
                                ozeti.setMayisTahsilat(Float.parseFloat(clients[12]));
                                ozeti.setHaziran(Float.parseFloat(clients[13]));
                                ozeti.setHaziranTahsilat(Float.parseFloat(clients[14]));
                                ozeti.setTemmuz(Float.parseFloat(clients[15]));
                                ozeti.setTemmuzTahsilat(Float.parseFloat(clients[16]));
                                ozeti.setAgustos(Float.parseFloat(clients[17]));
                                ozeti.setAgustosTahsilat(Float.parseFloat(clients[18]));
                                ozeti.setEylul(Float.parseFloat(clients[19]));
                                ozeti.setEylulTahsilat(Float.parseFloat(clients[20]));
                                ozeti.setEkim(Float.parseFloat(clients[21]));
                                ozeti.setEkimTahsilat(Float.parseFloat(clients[22]));
                                ozeti.setKasim(Float.parseFloat(clients[23]));
                                ozeti.setKasimTahsilat(Float.parseFloat(clients[24]));
                                ozeti.setAralik(Float.parseFloat(clients[25]));
                                ozeti.setAralikTahsilat(Float.parseFloat(clients[26]));
                                ozeti.setToplamSatis(Float.parseFloat(clients[27]));
                                ozeti.setToplamTahsilat(Float.parseFloat(clients[28]));
                                cari3 = ozeti;
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
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setSubat(Float.parseFloat(clients[4]));
                                ozeti.setMart(Float.parseFloat(clients[5]));
                                ozeti.setNisan(Float.parseFloat(clients[6]));
                                ozeti.setMayis(Float.parseFloat(clients[7]));
                                ozeti.setHaziran(Float.parseFloat(clients[8]));
                                ozeti.setTemmuz(Float.parseFloat(clients[9]));
                                ozeti.setAgustos(Float.parseFloat(clients[10]));
                                ozeti.setEylul(Float.parseFloat(clients[11]));
                                ozeti.setEkim(Float.parseFloat(clients[12]));
                                ozeti.setKasim(Float.parseFloat(clients[13]));
                                ozeti.setAralik(Float.parseFloat(clients[14]));
                                ozeti.setToplamSatis(Float.parseFloat(clients[15]));
                                cari4 = ozeti;
                            }
                        }

                        if (query.getCariHesapOzetiAlacakGrafik() != null
                                && query.getCariHesapOzetiAlacakGrafik().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiAlacakGrafik().size(); i++) {
                                String[] clients = query.getCariHesapOzetiAlacakGrafik().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setSubat(Float.parseFloat(clients[4]));
                                ozeti.setMart(Float.parseFloat(clients[5]));
                                ozeti.setNisan(Float.parseFloat(clients[6]));
                                ozeti.setMayis(Float.parseFloat(clients[7]));
                                ozeti.setHaziran(Float.parseFloat(clients[8]));
                                ozeti.setTemmuz(Float.parseFloat(clients[9]));
                                ozeti.setAgustos(Float.parseFloat(clients[10]));
                                ozeti.setEylul(Float.parseFloat(clients[11]));
                                ozeti.setEkim(Float.parseFloat(clients[12]));
                                ozeti.setKasim(Float.parseFloat(clients[13]));
                                ozeti.setAralik(Float.parseFloat(clients[14]));
                                ozeti.setToplamSatis(Float.parseFloat(clients[15]));
                                cari5 = ozeti;
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
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setSubat(Float.parseFloat(clients[4]));
                                ozeti.setMart(Float.parseFloat(clients[5]));
                                ozeti.setNisan(Float.parseFloat(clients[6]));
                                ozeti.setMayis(Float.parseFloat(clients[7]));
                                ozeti.setHaziran(Float.parseFloat(clients[8]));
                                ozeti.setTemmuz(Float.parseFloat(clients[9]));
                                ozeti.setAgustos(Float.parseFloat(clients[10]));
                                ozeti.setEylul(Float.parseFloat(clients[11]));
                                ozeti.setEkim(Float.parseFloat(clients[12]));
                                ozeti.setKasim(Float.parseFloat(clients[13]));
                                ozeti.setAralik(Float.parseFloat(clients[14]));
                                ozeti.setToplamSatis(Float.parseFloat(clients[15]));
                                cari6 = ozeti;
                            }
                        }

                        if (query.getCariHesapOzetiBorcAlacakBakiyeGrafik() != null
                                && query.getCariHesapOzetiBorcAlacakBakiyeGrafik().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiBorcAlacakBakiyeGrafik().size(); i++) {
                                String[] clients = query.getCariHesapOzetiBorcAlacakBakiyeGrafik().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setOcakTahsilat(Float.parseFloat(clients[4]));
                                ozeti.setOcakBakiye(Float.parseFloat(clients[5]));
                                ozeti.setSubat(Float.parseFloat(clients[6]));
                                ozeti.setSubatTahsilat(Float.parseFloat(clients[7]));
                                ozeti.setSubatBakiye(Float.parseFloat(clients[8]));
                                ozeti.setMart(Float.parseFloat(clients[9]));
                                ozeti.setMartTahsilat(Float.parseFloat(clients[10]));
                                ozeti.setMartBakiye(Float.parseFloat(clients[11]));
                                ozeti.setNisan(Float.parseFloat(clients[12]));
                                ozeti.setNisanTahsilat(Float.parseFloat(clients[13]));
                                ozeti.setNisanBakiye(Float.parseFloat(clients[14]));
                                ozeti.setMayis(Float.parseFloat(clients[15]));
                                ozeti.setMayisTahsilat(Float.parseFloat(clients[16]));
                                ozeti.setMayisBakiye(Float.parseFloat(clients[17]));
                                ozeti.setHaziran(Float.parseFloat(clients[18]));
                                ozeti.setHaziranTahsilat(Float.parseFloat(clients[19]));
                                ozeti.setHaziranBakiye(Float.parseFloat(clients[20]));
                                ozeti.setTemmuz(Float.parseFloat(clients[21]));
                                ozeti.setTemmuzTahsilat(Float.parseFloat(clients[22]));
                                ozeti.setTemmuzBakiye(Float.parseFloat(clients[23]));
                                ozeti.setAgustos(Float.parseFloat(clients[24]));
                                ozeti.setAgustosTahsilat(Float.parseFloat(clients[25]));
                                ozeti.setAgustosBakiye(Float.parseFloat(clients[26]));
                                ozeti.setEylul(Float.parseFloat(clients[27]));
                                ozeti.setEylulTahsilat(Float.parseFloat(clients[28]));
                                ozeti.setEylulBakiye(Float.parseFloat(clients[29]));
                                ozeti.setEkim(Float.parseFloat(clients[30]));
                                ozeti.setEkimTahsilat(Float.parseFloat(clients[31]));
                                ozeti.setEkimBakiye(Float.parseFloat(clients[32]));
                                ozeti.setKasim(Float.parseFloat(clients[33]));
                                ozeti.setKasimTahsilat(Float.parseFloat(clients[34]));
                                ozeti.setKasimBakiye(Float.parseFloat(clients[35]));
                                ozeti.setAralik(Float.parseFloat(clients[36]));
                                ozeti.setAralikTahsilat(Float.parseFloat(clients[37]));
                                ozeti.setAralikBakiye(Float.parseFloat(clients[38]));
                                ozeti.setToplamBorc(Float.parseFloat(clients[39]));
                                ozeti.setToplamAlacak(Float.parseFloat(clients[40]));
                                ozeti.setToplamBakiye(Float.parseFloat(clients[41]));
                                cari7 = ozeti;
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
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setSubat(Float.parseFloat(clients[4]));
                                ozeti.setMart(Float.parseFloat(clients[5]));
                                ozeti.setNisan(Float.parseFloat(clients[6]));
                                ozeti.setMayis(Float.parseFloat(clients[7]));
                                ozeti.setHaziran(Float.parseFloat(clients[8]));
                                ozeti.setTemmuz(Float.parseFloat(clients[9]));
                                ozeti.setAgustos(Float.parseFloat(clients[10]));
                                ozeti.setEylul(Float.parseFloat(clients[11]));
                                ozeti.setEkim(Float.parseFloat(clients[12]));
                                ozeti.setKasim(Float.parseFloat(clients[13]));
                                ozeti.setAralik(Float.parseFloat(clients[14]));
                                ozeti.setToplamSatis(Float.parseFloat(clients[15]));
                                cari8 = ozeti;
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
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setSubat(Float.parseFloat(clients[4]));
                                ozeti.setMart(Float.parseFloat(clients[5]));
                                ozeti.setNisan(Float.parseFloat(clients[6]));
                                ozeti.setMayis(Float.parseFloat(clients[7]));
                                ozeti.setHaziran(Float.parseFloat(clients[8]));
                                ozeti.setTemmuz(Float.parseFloat(clients[9]));
                                ozeti.setAgustos(Float.parseFloat(clients[10]));
                                ozeti.setEylul(Float.parseFloat(clients[11]));
                                ozeti.setEkim(Float.parseFloat(clients[12]));
                                ozeti.setKasim(Float.parseFloat(clients[13]));
                                ozeti.setAralik(Float.parseFloat(clients[14]));
                                ozeti.setToplamTahsilat(Float.parseFloat(clients[15]));
                                cari9 = ozeti;
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
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setOcakTahsilat(Float.parseFloat(clients[4]));
                                ozeti.setSubat(Float.parseFloat(clients[5]));
                                ozeti.setSubatTahsilat(Float.parseFloat(clients[6]));
                                ozeti.setMart(Float.parseFloat(clients[7]));
                                ozeti.setMartTahsilat(Float.parseFloat(clients[8]));
                                ozeti.setNisan(Float.parseFloat(clients[9]));
                                ozeti.setNisanTahsilat(Float.parseFloat(clients[10]));
                                ozeti.setMayis(Float.parseFloat(clients[11]));
                                ozeti.setMayisTahsilat(Float.parseFloat(clients[12]));
                                ozeti.setHaziran(Float.parseFloat(clients[13]));
                                ozeti.setHaziranTahsilat(Float.parseFloat(clients[14]));
                                ozeti.setTemmuz(Float.parseFloat(clients[15]));
                                ozeti.setTemmuzTahsilat(Float.parseFloat(clients[16]));
                                ozeti.setAgustos(Float.parseFloat(clients[17]));
                                ozeti.setAgustosTahsilat(Float.parseFloat(clients[18]));
                                ozeti.setEylul(Float.parseFloat(clients[19]));
                                ozeti.setEylulTahsilat(Float.parseFloat(clients[20]));
                                ozeti.setEkim(Float.parseFloat(clients[21]));
                                ozeti.setEkimTahsilat(Float.parseFloat(clients[22]));
                                ozeti.setKasim(Float.parseFloat(clients[23]));
                                ozeti.setKasimTahsilat(Float.parseFloat(clients[24]));
                                ozeti.setAralik(Float.parseFloat(clients[25]));
                                ozeti.setAralikTahsilat(Float.parseFloat(clients[26]));
                                ozeti.setToplamSatis(Float.parseFloat(clients[27]));
                                ozeti.setToplamTahsilat(Float.parseFloat(clients[28]));
                                cari10 = ozeti;
                            }
                        }

                        if (query.getCariHesapOzetiBorcTablo() != null
                                && query.getCariHesapOzetiBorcTablo().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiBorcTablo().size(); i++) {
                                String[] clients = query.getCariHesapOzetiBorcTablo().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setSubat(Float.parseFloat(clients[4]));
                                ozeti.setMart(Float.parseFloat(clients[5]));
                                ozeti.setNisan(Float.parseFloat(clients[6]));
                                ozeti.setMayis(Float.parseFloat(clients[7]));
                                ozeti.setHaziran(Float.parseFloat(clients[8]));
                                ozeti.setTemmuz(Float.parseFloat(clients[9]));
                                ozeti.setAgustos(Float.parseFloat(clients[10]));
                                ozeti.setEylul(Float.parseFloat(clients[11]));
                                ozeti.setEkim(Float.parseFloat(clients[12]));
                                ozeti.setKasim(Float.parseFloat(clients[13]));
                                ozeti.setAralik(Float.parseFloat(clients[14]));
                                ozeti.setToplamBorc(Float.parseFloat(clients[15]));
                                cari11 = ozeti;
                            }
                        }

                        if (query.getCariHesapOzetiAlacakTablo() != null
                                && query.getCariHesapOzetiAlacakTablo().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiAlacakTablo().size(); i++) {
                                String[] clients = query.getCariHesapOzetiAlacakTablo().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setSubat(Float.parseFloat(clients[4]));
                                ozeti.setMart(Float.parseFloat(clients[5]));
                                ozeti.setNisan(Float.parseFloat(clients[6]));
                                ozeti.setMayis(Float.parseFloat(clients[7]));
                                ozeti.setHaziran(Float.parseFloat(clients[8]));
                                ozeti.setTemmuz(Float.parseFloat(clients[9]));
                                ozeti.setAgustos(Float.parseFloat(clients[10]));
                                ozeti.setEylul(Float.parseFloat(clients[11]));
                                ozeti.setEkim(Float.parseFloat(clients[12]));
                                ozeti.setKasim(Float.parseFloat(clients[13]));
                                ozeti.setAralik(Float.parseFloat(clients[14]));
                                ozeti.setToplamAlacak(Float.parseFloat(clients[15]));
                                cari12 = ozeti;
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
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setSubat(Float.parseFloat(clients[4]));
                                ozeti.setMart(Float.parseFloat(clients[5]));
                                ozeti.setNisan(Float.parseFloat(clients[6]));
                                ozeti.setMayis(Float.parseFloat(clients[7]));
                                ozeti.setHaziran(Float.parseFloat(clients[8]));
                                ozeti.setTemmuz(Float.parseFloat(clients[9]));
                                ozeti.setAgustos(Float.parseFloat(clients[10]));
                                ozeti.setEylul(Float.parseFloat(clients[11]));
                                ozeti.setEkim(Float.parseFloat(clients[12]));
                                ozeti.setKasim(Float.parseFloat(clients[13]));
                                ozeti.setAralik(Float.parseFloat(clients[14]));
                                ozeti.setToplamBakiye(Float.parseFloat(clients[15]));
                                cari13 = ozeti;
                            }
                        }

                        if (query.getCariHesapOzetiBorcAlacakBakiyeTablo() != null
                                && query.getCariHesapOzetiBorcAlacakBakiyeTablo().size() > 2) {
                            for (int i = 2; i < query.getCariHesapOzetiBorcAlacakBakiyeTablo().size(); i++) {
                                String[] clients = query.getCariHesapOzetiBorcAlacakBakiyeTablo().get(i).split("\\|");
                                CariHesapOzeti ozeti = new CariHesapOzeti();
                                ozeti.setCariKayitNo(clients[0]);
                                ozeti.setCariKod(clients[1]);
                                ozeti.setCariUnvan(clients[2]);
                                ozeti.setOcak(Float.parseFloat(clients[3]));
                                ozeti.setOcakTahsilat(Float.parseFloat(clients[4]));
                                ozeti.setOcakBakiye(Float.parseFloat(clients[5]));
                                ozeti.setSubat(Float.parseFloat(clients[6]));
                                ozeti.setSubatTahsilat(Float.parseFloat(clients[7]));
                                ozeti.setSubatBakiye(Float.parseFloat(clients[8]));
                                ozeti.setMart(Float.parseFloat(clients[9]));
                                ozeti.setMartTahsilat(Float.parseFloat(clients[10]));
                                ozeti.setMartBakiye(Float.parseFloat(clients[11]));
                                ozeti.setNisan(Float.parseFloat(clients[12]));
                                ozeti.setNisanTahsilat(Float.parseFloat(clients[13]));
                                ozeti.setNisanBakiye(Float.parseFloat(clients[14]));
                                ozeti.setMayis(Float.parseFloat(clients[15]));
                                ozeti.setMayisTahsilat(Float.parseFloat(clients[16]));
                                ozeti.setMayisBakiye(Float.parseFloat(clients[17]));
                                ozeti.setHaziran(Float.parseFloat(clients[18]));
                                ozeti.setHaziranTahsilat(Float.parseFloat(clients[19]));
                                ozeti.setHaziranBakiye(Float.parseFloat(clients[20]));
                                ozeti.setTemmuz(Float.parseFloat(clients[21]));
                                ozeti.setTemmuzTahsilat(Float.parseFloat(clients[22]));
                                ozeti.setTemmuzBakiye(Float.parseFloat(clients[23]));
                                ozeti.setAgustos(Float.parseFloat(clients[24]));
                                ozeti.setAgustosTahsilat(Float.parseFloat(clients[25]));
                                ozeti.setAgustosBakiye(Float.parseFloat(clients[26]));
                                ozeti.setEylul(Float.parseFloat(clients[27]));
                                ozeti.setEylulTahsilat(Float.parseFloat(clients[28]));
                                ozeti.setEylulBakiye(Float.parseFloat(clients[29]));
                                ozeti.setEkim(Float.parseFloat(clients[30]));
                                ozeti.setEkimTahsilat(Float.parseFloat(clients[31]));
                                ozeti.setEkimBakiye(Float.parseFloat(clients[32]));
                                ozeti.setKasim(Float.parseFloat(clients[33]));
                                ozeti.setKasimTahsilat(Float.parseFloat(clients[34]));
                                ozeti.setKasimBakiye(Float.parseFloat(clients[35]));
                                ozeti.setAralik(Float.parseFloat(clients[36]));
                                ozeti.setAralikTahsilat(Float.parseFloat(clients[37]));
                                ozeti.setAralikBakiye(Float.parseFloat(clients[38]));
                                ozeti.setToplamSatis(Float.parseFloat(clients[39]));
                                ozeti.setToplamTahsilat(Float.parseFloat(clients[40]));
                                ozeti.setToplamBakiye(Float.parseFloat(clients[41]));
                                cari14 = ozeti;
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
                if (cari1 != null) {
                    cariHesapOzetiSatisGrafik(cari1);
                }
                if (cari2 != null) {
                    cariHesapOzetiTahsilatGrafik(cari2);
                }
                if (cari3 != null) {
                    cariHesapOzetiSatisTahsilatGrafik(cari3);
                }
                if (cari4 != null) {
                    cariHesapOzetiBorcGrafik(cari4);
                }
                if (cari5 != null) {
                    cariHesapOzetiAlacakGrafik(cari5);
                }
                if (cari6 != null) {
                    cariHesapOzetiBakiyeGrafik(cari6);
                }
                if (cari7 != null) {
                    cariHesapOzetiBorcAlacakBakiyeGrafik(cari7);
                }
                if (cari8 != null) {
                    cariHesapOzetiSatisTablo(cari8);
                }
                if (cari9 != null) {
                    cariHesapOzetiTahsilatTablo(cari9);
                }
                if (cari10 != null) {
                    cariHesapOzetiSatisTahsilatTablo(cari10);
                }
                if (cari11 != null) {
                    cariHesapOzetiBorcTablo(cari11);
                }
                if (cari12 != null) {
                    cariHesapOzetiAlacakTablo(cari12);
                }
                if (cari13 != null) {
                    cariHesapOzetiBakiyeTablo(cari13);
                }
                if (cari14 != null) {
                    cariHesapOzetiBorcTahsilatBakiyeTablo(cari14);
                }
            } else {
                sendDataToServerFailDialog(hata);
            }
            products_progressBar.setVisibility(View.GONE);
        }
    }

    private void cariHesapOzetiSatisGrafik(CariHesapOzeti list) {
        BarChart barChart = findViewById(R.id.barChart1);
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiSatisGrafik);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                List<BarEntry> entriesGroup1 = new ArrayList<>();

                entriesGroup1.add(new BarEntry(0, list.getOcak()));
                entriesGroup1.add(new BarEntry(1, list.getSubat()));
                entriesGroup1.add(new BarEntry(2, list.getMart()));
                entriesGroup1.add(new BarEntry(3, list.getNisan()));
                entriesGroup1.add(new BarEntry(4, list.getMayis()));
                entriesGroup1.add(new BarEntry(5, list.getHaziran()));
                entriesGroup1.add(new BarEntry(6, list.getTemmuz()));
                entriesGroup1.add(new BarEntry(7, list.getAgustos()));
                entriesGroup1.add(new BarEntry(8, list.getEylul()));
                entriesGroup1.add(new BarEntry(9, list.getEkim()));
                entriesGroup1.add(new BarEntry(10, list.getKasim()));
                entriesGroup1.add(new BarEntry(11, list.getAralik()));

                String[] days = new String[]{
                        getString(R.string.month_january),
                        getString(R.string.month_february),
                        getString(R.string.month_mart),
                        getString(R.string.month_april),
                        getString(R.string.month_may),
                        getString(R.string.month_june),
                        getString(R.string.month_july),
                        getString(R.string.month_august),
                        getString(R.string.month_september),
                        getString(R.string.month_october),
                        getString(R.string.month_november),
                        getString(R.string.month_december)
                };

                BarDataSet set1 = new BarDataSet(entriesGroup1, getString(R.string.client_reports_sales));
                set1.setColor(Color.parseColor("#f39c12"));
                set1.setValueTextSize(8f);
                set1.setValueTextColor(Color.BLACK);
                set1.setBarBorderWidth(2f);
                set1.setBarBorderColor(Color.parseColor("#e67e22"));

                BarData data = new BarData(set1);
                barChart.setData(data);

                XAxis axis = barChart.getXAxis();
                axis.setValueFormatter(new IndexAxisValueFormatter(days));
                axis.setGranularityEnabled(true);
                axis.setPosition(XAxis.XAxisPosition.BOTTOM);
                axis.setTextColor(Color.BLACK);
                axis.setTextSize(12f);
                axis.setLabelRotationAngle(270);
                axis.setLabelCount(12);

                barChart.getAxisLeft().setAxisMinimum(0);
                barChart.getAxisRight().setDrawLabels(false);

                barChart.getLegend().setTextSize(14f);
                barChart.getLegend().setTextColor(Color.BLACK);
                barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
                barChart.getLegend().setFormToTextSpace(5f);
                barChart.getLegend().setFormSize(10f);
                barChart.getLegend().setXEntrySpace(20);

                barChart.getDescription().setEnabled(false);
                barChart.invalidate();

            } catch (Exception e) {
            }
        }
    }

    private void cariHesapOzetiTahsilatGrafik(CariHesapOzeti list) {
        BarChart barChart = findViewById(R.id.barChart2);
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiTahsilatGrafik);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                List<BarEntry> entriesGroup1 = new ArrayList<>();

                entriesGroup1.add(new BarEntry(0, list.getOcak()));
                entriesGroup1.add(new BarEntry(1, list.getSubat()));
                entriesGroup1.add(new BarEntry(2, list.getMart()));
                entriesGroup1.add(new BarEntry(3, list.getNisan()));
                entriesGroup1.add(new BarEntry(4, list.getMayis()));
                entriesGroup1.add(new BarEntry(5, list.getHaziran()));
                entriesGroup1.add(new BarEntry(6, list.getTemmuz()));
                entriesGroup1.add(new BarEntry(7, list.getAgustos()));
                entriesGroup1.add(new BarEntry(8, list.getEylul()));
                entriesGroup1.add(new BarEntry(9, list.getEkim()));
                entriesGroup1.add(new BarEntry(10, list.getKasim()));
                entriesGroup1.add(new BarEntry(11, list.getAralik()));

                String[] days = new String[]{
                        getString(R.string.month_january),
                        getString(R.string.month_february),
                        getString(R.string.month_mart),
                        getString(R.string.month_april),
                        getString(R.string.month_may),
                        getString(R.string.month_june),
                        getString(R.string.month_july),
                        getString(R.string.month_august),
                        getString(R.string.month_september),
                        getString(R.string.month_october),
                        getString(R.string.month_november),
                        getString(R.string.month_december)
                };

                BarDataSet set1 = new BarDataSet(entriesGroup1, getString(R.string.client_reports_collections));
                set1.setColor(Color.parseColor("#5AC8FA"));
                set1.setValueTextSize(8f);
                set1.setValueTextColor(Color.BLACK);
                set1.setBarBorderWidth(2f);
                set1.setBarBorderColor(Color.parseColor("#34AADC"));

                BarData data = new BarData(set1);
                barChart.setData(data);

                XAxis axis = barChart.getXAxis();
                axis.setValueFormatter(new IndexAxisValueFormatter(days));
                axis.setGranularityEnabled(true);
                axis.setPosition(XAxis.XAxisPosition.BOTTOM);
                axis.setTextColor(Color.BLACK);
                axis.setTextSize(12f);
                axis.setLabelRotationAngle(270);
                axis.setLabelCount(12);

                barChart.getAxisLeft().setAxisMinimum(0);
                barChart.getAxisRight().setDrawLabels(false);

                barChart.getLegend().setTextSize(14f);
                barChart.getLegend().setTextColor(Color.BLACK);
                barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
                barChart.getLegend().setFormToTextSpace(5f);
                barChart.getLegend().setFormSize(10f);
                barChart.getLegend().setXEntrySpace(20);

                barChart.getDescription().setEnabled(false);
                barChart.invalidate();

            } catch (Exception e) {
            }
        }
    }

    private void cariHesapOzetiSatisTahsilatGrafik(CariHesapOzeti list) {
        BarChart barChart = findViewById(R.id.barChart3);
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiSatisTahsilatGrafik);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                List<BarEntry> entriesGroup1 = new ArrayList<>();
                List<BarEntry> entriesGroup2 = new ArrayList<>();

                entriesGroup1.add(new BarEntry(0, list.getOcak()));
                entriesGroup1.add(new BarEntry(1, list.getSubat()));
                entriesGroup1.add(new BarEntry(2, list.getMart()));
                entriesGroup1.add(new BarEntry(3, list.getNisan()));
                entriesGroup1.add(new BarEntry(4, list.getMayis()));
                entriesGroup1.add(new BarEntry(5, list.getHaziran()));
                entriesGroup1.add(new BarEntry(6, list.getTemmuz()));
                entriesGroup1.add(new BarEntry(7, list.getAgustos()));
                entriesGroup1.add(new BarEntry(8, list.getEylul()));
                entriesGroup1.add(new BarEntry(9, list.getEkim()));
                entriesGroup1.add(new BarEntry(10, list.getKasim()));
                entriesGroup1.add(new BarEntry(11, list.getAralik()));

                entriesGroup2.add(new BarEntry(1, list.getOcakTahsilat()));
                entriesGroup2.add(new BarEntry(2, list.getSubatTahsilat()));
                entriesGroup2.add(new BarEntry(3, list.getMartTahsilat()));
                entriesGroup2.add(new BarEntry(4, list.getNisanTahsilat()));
                entriesGroup2.add(new BarEntry(5, list.getMayisTahsilat()));
                entriesGroup2.add(new BarEntry(6, list.getHaziranTahsilat()));
                entriesGroup2.add(new BarEntry(7, list.getTemmuzTahsilat()));
                entriesGroup2.add(new BarEntry(8, list.getAgustosTahsilat()));
                entriesGroup2.add(new BarEntry(9, list.getEylulTahsilat()));
                entriesGroup2.add(new BarEntry(10, list.getEkimTahsilat()));
                entriesGroup2.add(new BarEntry(11, list.getKasimTahsilat()));
                entriesGroup2.add(new BarEntry(12, list.getAralikTahsilat()));

                String[] days = new String[]{
                        getString(R.string.month_january),
                        getString(R.string.month_february),
                        getString(R.string.month_mart),
                        getString(R.string.month_april),
                        getString(R.string.month_may),
                        getString(R.string.month_june),
                        getString(R.string.month_july),
                        getString(R.string.month_august),
                        getString(R.string.month_september),
                        getString(R.string.month_october),
                        getString(R.string.month_november),
                        getString(R.string.month_december)
                };

                BarDataSet set1 = new BarDataSet(entriesGroup1, getString(R.string.products_extract_sale_count));
                set1.setColor(Color.parseColor("#f39c12"));
                set1.setValueTextSize(8f);
                set1.setValueTextColor(Color.BLACK);
                set1.setBarBorderWidth(2f);
                set1.setBarBorderColor(Color.parseColor("#e67e22"));

                BarDataSet set2 = new BarDataSet(entriesGroup2, getString(R.string.client_dashboard_month_collections));
                set2.setValueTextSize(8f);
                set1.setValueTextColor(Color.BLACK);
                set2.setColor(Color.parseColor("#27ae60"));
                set2.setBarBorderWidth(2f);
                set2.setBarBorderColor(Color.parseColor("#16a085"));

                float groupSpace = 0.08f;
                float barSpace = 0.08f;
                float barWidth = 0.38f;

                BarData data = new BarData(set1, set2);
                barChart.setData(data);
                data.setBarWidth(barWidth);

                XAxis axis = barChart.getXAxis();
                axis.setValueFormatter(new IndexAxisValueFormatter(days));
                axis.setGranularityEnabled(true);
                axis.setCenterAxisLabels(true);
                axis.setPosition(XAxis.XAxisPosition.BOTTOM);
                axis.setTextColor(Color.BLACK);
                axis.setTextSize(12f);
                axis.setLabelRotationAngle(270);
                axis.setLabelCount(12);

                barChart.getXAxis().setAxisMinimum(0);
                barChart.getXAxis().setAxisMaximum(0 + barChart.getData().getGroupWidth(groupSpace, barSpace) * 12);
                barChart.getAxisLeft().setAxisMinimum(0);
                barChart.getAxisRight().setDrawLabels(false);

                barChart.getLegend().setTextSize(14f);
                barChart.getLegend().setTextColor(Color.BLACK);
                barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
                barChart.getLegend().setFormToTextSpace(5f);
                barChart.getLegend().setFormSize(10f);
                barChart.getLegend().setXEntrySpace(20);

                barChart.getDescription().setEnabled(false);
                barChart.groupBars(0, groupSpace, barSpace);
                barChart.setDragEnabled(true);
                barChart.invalidate();
            } catch (Exception e) {
            }
        }
    }

    private void cariHesapOzetiBorcGrafik(CariHesapOzeti list) {
        BarChart barChart = findViewById(R.id.barChart4);
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiBorcGrafik);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                List<BarEntry> entriesGroup1 = new ArrayList<>();

                entriesGroup1.add(new BarEntry(0, list.getOcak()));
                entriesGroup1.add(new BarEntry(1, list.getSubat()));
                entriesGroup1.add(new BarEntry(2, list.getMart()));
                entriesGroup1.add(new BarEntry(3, list.getNisan()));
                entriesGroup1.add(new BarEntry(4, list.getMayis()));
                entriesGroup1.add(new BarEntry(5, list.getHaziran()));
                entriesGroup1.add(new BarEntry(6, list.getTemmuz()));
                entriesGroup1.add(new BarEntry(7, list.getAgustos()));
                entriesGroup1.add(new BarEntry(8, list.getEylul()));
                entriesGroup1.add(new BarEntry(9, list.getEkim()));
                entriesGroup1.add(new BarEntry(10, list.getKasim()));
                entriesGroup1.add(new BarEntry(11, list.getAralik()));

                String[] days = new String[]{
                        getString(R.string.month_january),
                        getString(R.string.month_february),
                        getString(R.string.month_mart),
                        getString(R.string.month_april),
                        getString(R.string.month_may),
                        getString(R.string.month_june),
                        getString(R.string.month_july),
                        getString(R.string.month_august),
                        getString(R.string.month_september),
                        getString(R.string.month_october),
                        getString(R.string.month_november),
                        getString(R.string.month_december)
                };

                BarDataSet set1 = new BarDataSet(entriesGroup1, getString(R.string.client_reports_debts));
                set1.setColor(Color.parseColor("#FF5722"));
                set1.setValueTextSize(8f);
                set1.setValueTextColor(Color.BLACK);
                set1.setBarBorderWidth(2f);
                set1.setBarBorderColor(Color.parseColor("#F44336"));

                BarData data = new BarData(set1);
                barChart.setData(data);

                XAxis axis = barChart.getXAxis();
                axis.setValueFormatter(new IndexAxisValueFormatter(days));
                axis.setGranularityEnabled(true);
                axis.setPosition(XAxis.XAxisPosition.BOTTOM);
                axis.setTextColor(Color.BLACK);
                axis.setTextSize(12f);
                axis.setLabelRotationAngle(270);
                axis.setLabelCount(12);

                barChart.getAxisLeft().setAxisMinimum(0);
                barChart.getAxisRight().setDrawLabels(false);

                barChart.getLegend().setTextSize(14f);
                barChart.getLegend().setTextColor(Color.BLACK);
                barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
                barChart.getLegend().setFormToTextSpace(5f);
                barChart.getLegend().setFormSize(10f);
                barChart.getLegend().setXEntrySpace(20);

                barChart.getDescription().setEnabled(false);
                barChart.invalidate();

            } catch (Exception e) {
            }
        }
    }

    private void cariHesapOzetiAlacakGrafik(CariHesapOzeti list) {
        BarChart barChart = findViewById(R.id.barChart5);
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiAlacakGrafik);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                List<BarEntry> entriesGroup1 = new ArrayList<>();

                entriesGroup1.add(new BarEntry(0, list.getOcak()));
                entriesGroup1.add(new BarEntry(1, list.getSubat()));
                entriesGroup1.add(new BarEntry(2, list.getMart()));
                entriesGroup1.add(new BarEntry(3, list.getNisan()));
                entriesGroup1.add(new BarEntry(4, list.getMayis()));
                entriesGroup1.add(new BarEntry(5, list.getHaziran()));
                entriesGroup1.add(new BarEntry(6, list.getTemmuz()));
                entriesGroup1.add(new BarEntry(7, list.getAgustos()));
                entriesGroup1.add(new BarEntry(8, list.getEylul()));
                entriesGroup1.add(new BarEntry(9, list.getEkim()));
                entriesGroup1.add(new BarEntry(10, list.getKasim()));
                entriesGroup1.add(new BarEntry(11, list.getAralik()));

                String[] days = new String[]{
                        getString(R.string.month_january),
                        getString(R.string.month_february),
                        getString(R.string.month_mart),
                        getString(R.string.month_april),
                        getString(R.string.month_may),
                        getString(R.string.month_june),
                        getString(R.string.month_july),
                        getString(R.string.month_august),
                        getString(R.string.month_september),
                        getString(R.string.month_october),
                        getString(R.string.month_november),
                        getString(R.string.month_december)
                };

                BarDataSet set1 = new BarDataSet(entriesGroup1, getString(R.string.client_reports_credit));
                set1.setColor(Color.parseColor("#007AFF"));
                set1.setValueTextSize(8f);
                set1.setValueTextColor(Color.BLACK);
                set1.setBarBorderWidth(2f);
                set1.setBarBorderColor(Color.parseColor("#5856D6"));

                BarData data = new BarData(set1);
                barChart.setData(data);

                XAxis axis = barChart.getXAxis();
                axis.setValueFormatter(new IndexAxisValueFormatter(days));
                axis.setGranularityEnabled(true);
                axis.setPosition(XAxis.XAxisPosition.BOTTOM);
                axis.setTextColor(Color.BLACK);
                axis.setTextSize(12f);
                axis.setLabelRotationAngle(270);
                axis.setLabelCount(12);

                barChart.getAxisLeft().setAxisMinimum(0);
                barChart.getAxisRight().setDrawLabels(false);

                barChart.getLegend().setTextSize(14f);
                barChart.getLegend().setTextColor(Color.BLACK);
                barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
                barChart.getLegend().setFormToTextSpace(5f);
                barChart.getLegend().setFormSize(10f);
                barChart.getLegend().setXEntrySpace(20);

                barChart.getDescription().setEnabled(false);
                barChart.invalidate();

            } catch (Exception e) {
            }
        }
    }

    private void cariHesapOzetiBakiyeGrafik(CariHesapOzeti list) {
        BarChart barChart = findViewById(R.id.barChart6);
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiBakiyeGrafik);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                List<BarEntry> entriesGroup1 = new ArrayList<>();

                entriesGroup1.add(new BarEntry(0, list.getOcak()));
                entriesGroup1.add(new BarEntry(1, list.getSubat()));
                entriesGroup1.add(new BarEntry(2, list.getMart()));
                entriesGroup1.add(new BarEntry(3, list.getNisan()));
                entriesGroup1.add(new BarEntry(4, list.getMayis()));
                entriesGroup1.add(new BarEntry(5, list.getHaziran()));
                entriesGroup1.add(new BarEntry(6, list.getTemmuz()));
                entriesGroup1.add(new BarEntry(7, list.getAgustos()));
                entriesGroup1.add(new BarEntry(8, list.getEylul()));
                entriesGroup1.add(new BarEntry(9, list.getEkim()));
                entriesGroup1.add(new BarEntry(10, list.getKasim()));
                entriesGroup1.add(new BarEntry(11, list.getAralik()));

                String[] days = new String[]{
                        getString(R.string.month_january),
                        getString(R.string.month_february),
                        getString(R.string.month_mart),
                        getString(R.string.month_april),
                        getString(R.string.month_may),
                        getString(R.string.month_june),
                        getString(R.string.month_july),
                        getString(R.string.month_august),
                        getString(R.string.month_september),
                        getString(R.string.month_october),
                        getString(R.string.month_november),
                        getString(R.string.month_december)
                };

                BarDataSet set1 = new BarDataSet(entriesGroup1, getString(R.string.client_reports_balance));
                set1.setColor(Color.parseColor("#FFBB86FC"));
                set1.setValueTextSize(8f);
                set1.setValueTextColor(Color.BLACK);
                set1.setBarBorderWidth(2f);
                set1.setBarBorderColor(Color.parseColor("#8e44ad"));

                BarData data = new BarData(set1);
                barChart.setData(data);

                XAxis axis = barChart.getXAxis();
                axis.setValueFormatter(new IndexAxisValueFormatter(days));
                axis.setGranularityEnabled(true);
                axis.setPosition(XAxis.XAxisPosition.BOTTOM);
                axis.setTextColor(Color.BLACK);
                axis.setTextSize(12f);
                axis.setLabelRotationAngle(270);
                axis.setLabelCount(12);

                barChart.getAxisLeft().setAxisMinimum(0);
                barChart.getAxisRight().setDrawLabels(false);

                barChart.getLegend().setTextSize(14f);
                barChart.getLegend().setTextColor(Color.BLACK);
                barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
                barChart.getLegend().setFormToTextSpace(5f);
                barChart.getLegend().setFormSize(10f);
                barChart.getLegend().setXEntrySpace(20);

                barChart.getDescription().setEnabled(false);
                barChart.invalidate();

            } catch (Exception e) {
            }
        }
    }

    private void cariHesapOzetiBorcAlacakBakiyeGrafik(CariHesapOzeti list) {
        BarChart barChart = findViewById(R.id.barChart7);
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiBorcAlacakBakiyeGrafik);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                List<BarEntry> entriesGroup1 = new ArrayList<>();
                List<BarEntry> entriesGroup2 = new ArrayList<>();
                List<BarEntry> entriesGroup3 = new ArrayList<>();

                entriesGroup1.add(new BarEntry(0, list.getOcak()));
                entriesGroup1.add(new BarEntry(1, list.getSubat()));
                entriesGroup1.add(new BarEntry(2, list.getMart()));
                entriesGroup1.add(new BarEntry(3, list.getNisan()));
                entriesGroup1.add(new BarEntry(4, list.getMayis()));
                entriesGroup1.add(new BarEntry(5, list.getHaziran()));
                entriesGroup1.add(new BarEntry(6, list.getTemmuz()));
                entriesGroup1.add(new BarEntry(7, list.getAgustos()));
                entriesGroup1.add(new BarEntry(8, list.getEylul()));
                entriesGroup1.add(new BarEntry(9, list.getEkim()));
                entriesGroup1.add(new BarEntry(10, list.getKasim()));
                entriesGroup1.add(new BarEntry(11, list.getAralik()));

                entriesGroup2.add(new BarEntry(1, list.getOcakTahsilat()));
                entriesGroup2.add(new BarEntry(2, list.getSubatTahsilat()));
                entriesGroup2.add(new BarEntry(3, list.getMartTahsilat()));
                entriesGroup2.add(new BarEntry(4, list.getNisanTahsilat()));
                entriesGroup2.add(new BarEntry(5, list.getMayisTahsilat()));
                entriesGroup2.add(new BarEntry(6, list.getHaziranTahsilat()));
                entriesGroup2.add(new BarEntry(7, list.getTemmuzTahsilat()));
                entriesGroup2.add(new BarEntry(8, list.getAgustosTahsilat()));
                entriesGroup2.add(new BarEntry(9, list.getEylulTahsilat()));
                entriesGroup2.add(new BarEntry(10, list.getEkimTahsilat()));
                entriesGroup2.add(new BarEntry(11, list.getKasimTahsilat()));
                entriesGroup2.add(new BarEntry(12, list.getAralikTahsilat()));

                entriesGroup3.add(new BarEntry(1, list.getOcakBakiye()));
                entriesGroup3.add(new BarEntry(2, list.getSubatBakiye()));
                entriesGroup3.add(new BarEntry(3, list.getMartBakiye()));
                entriesGroup3.add(new BarEntry(4, list.getNisanBakiye()));
                entriesGroup3.add(new BarEntry(5, list.getMayisBakiye()));
                entriesGroup3.add(new BarEntry(6, list.getHaziranBakiye()));
                entriesGroup3.add(new BarEntry(7, list.getTemmuzBakiye()));
                entriesGroup3.add(new BarEntry(8, list.getAgustosBakiye()));
                entriesGroup3.add(new BarEntry(9, list.getEylulBakiye()));
                entriesGroup3.add(new BarEntry(10, list.getEkimBakiye()));
                entriesGroup3.add(new BarEntry(11, list.getKasimBakiye()));
                entriesGroup3.add(new BarEntry(12, list.getAralikBakiye()));

                String[] days = new String[]{
                        getString(R.string.month_january),
                        getString(R.string.month_february),
                        getString(R.string.month_mart),
                        getString(R.string.month_april),
                        getString(R.string.month_may),
                        getString(R.string.month_june),
                        getString(R.string.month_july),
                        getString(R.string.month_august),
                        getString(R.string.month_september),
                        getString(R.string.month_october),
                        getString(R.string.month_november),
                        getString(R.string.month_december)
                };

                BarDataSet set1 = new BarDataSet(entriesGroup1, getString(R.string.client_reports_debts));
                set1.setColor(Color.parseColor("#2980b9"));
                set1.setValueTextSize(8f);
                set1.setValueTextColor(Color.BLACK);
                set1.setBarBorderWidth(2f);
                set1.setBarBorderColor(Color.parseColor("#2980b9"));

                BarDataSet set2 = new BarDataSet(entriesGroup2, getString(R.string.client_reports_credit));
                set2.setValueTextSize(8f);
                set2.setValueTextColor(Color.BLACK);
                set2.setColor(Color.parseColor("#e67e22"));
                set2.setBarBorderWidth(2f);
                set2.setBarBorderColor(Color.parseColor("#e74c3c"));

                BarDataSet set3 = new BarDataSet(entriesGroup3, getString(R.string.client_reports_balance));
                set3.setValueTextSize(8f);
                set3.setValueTextColor(Color.BLACK);
                set3.setColor(Color.parseColor("#FFBB86FC"));
                set3.setBarBorderWidth(2f);
                set3.setBarBorderColor(Color.parseColor("#8e44ad"));

                float groupSpace = 0.1f;
                float barSpace = 0.05f;
                float barWidth = 0.25f;

                BarData data = new BarData(set1, set2, set3);
                barChart.setData(data);
                data.setBarWidth(barWidth);

                XAxis axis = barChart.getXAxis();
                axis.setValueFormatter(new IndexAxisValueFormatter(days));
                axis.setCenterAxisLabels(true);
                axis.setGranularityEnabled(true);
                axis.setPosition(XAxis.XAxisPosition.BOTTOM);
                axis.setTextColor(Color.BLACK);
                axis.setTextSize(12f);
                axis.setLabelRotationAngle(270);
                axis.setLabelCount(12);

                barChart.getXAxis().setAxisMinimum(0);
                barChart.getXAxis().setAxisMaximum(0 + barChart.getData().getGroupWidth(groupSpace, barSpace) * 12);
//                barChart.getAxisLeft().setAxisMinimum(0);
                barChart.getAxisRight().setDrawLabels(false);

                barChart.getLegend().setTextSize(14f);
                barChart.getLegend().setTextColor(Color.BLACK);
                barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
                barChart.getLegend().setFormToTextSpace(5f);
                barChart.getLegend().setFormSize(10f);
                barChart.getLegend().setXEntrySpace(20);

                barChart.getDescription().setEnabled(false);
                barChart.groupBars(0, groupSpace, barSpace);
                barChart.setDragEnabled(true);
                barChart.invalidate();
            } catch (Exception e) {
            }
        }
    }

    private void cariHesapOzetiSatisTablo(CariHesapOzeti list) {
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiSatisTablo);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                Map<String, Float> map = new LinkedHashMap<>();

                map.put(getResources().getString(R.string.month_january), list.getOcak());
                map.put(getResources().getString(R.string.month_february), list.getSubat());
                map.put(getResources().getString(R.string.month_mart), list.getMart());
                map.put(getResources().getString(R.string.month_april), list.getNisan());
                map.put(getResources().getString(R.string.month_may), list.getMayis());
                map.put(getResources().getString(R.string.month_june), list.getHaziran());
                map.put(getResources().getString(R.string.month_july), list.getTemmuz());
                map.put(getResources().getString(R.string.month_august), list.getAgustos());
                map.put(getResources().getString(R.string.month_september), list.getEylul());
                map.put(getResources().getString(R.string.month_october), list.getEkim());
                map.put(getResources().getString(R.string.month_november), list.getKasim());
                map.put(getResources().getString(R.string.month_december), list.getAralik());

                String[] colors = new String[12];
                colors[0] = "#f39c12";
                colors[1] = "#C3B091";
                colors[2] = "#8e44ad";
                colors[3] = "#FADA5E";
                colors[4] = "#16a085";
                colors[5] = "#FF0000";
                colors[6] = "#03DAC5";
                colors[7] = "#FFBB86FC";
                colors[8] = "#FADA5E";
                colors[9] = "#2980b9";
                colors[10] = "#f39c12";
                colors[11] = "#03DAC5";

                LayoutInflater inflater = getLayoutInflater();
                TableRow tr = (TableRow) inflater.inflate(R.layout.barchart_tablelayout_col2,
                        layoutBarChart, false);
                layoutBarChart.addView(tr);
                TextView tvLabel = (TextView) tr.getChildAt(1);
                TextView tvAmt = (TextView) tr.getChildAt(2);
                tvLabel.setPadding(0, 60, 0, 20);
                tvAmt.setPadding(0, 60, 0, 20);
                tvLabel.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel.setAllCaps(true);
                tvAmt.setAllCaps(true);
                tvAmt.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel.setText(getResources().getString(R.string.client_reports_moths));
                tvAmt.setText(getResources().getString(R.string.client_reports_sales));

                int i = 0;
                for (Map.Entry<String, Float> mapList : map.entrySet()) {
                    LayoutInflater inflater1 = getLayoutInflater();
                    TableRow tr1 = (TableRow) inflater1.inflate(R.layout.barchart_tablelayout_col2,
                            layoutBarChart, false);
                    layoutBarChart.addView(tr1);
                    LinearLayout linearLayoutColorContainer1 = (LinearLayout) tr1.getChildAt(0);
                    LinearLayout linearLayoutColor1 = (LinearLayout) linearLayoutColorContainer1.getChildAt(0);
                    TextView tvLabel1 = (TextView) tr1.getChildAt(1);
                    TextView tvAmt1 = (TextView) tr1.getChildAt(2);
                    linearLayoutColor1.setBackgroundColor(Color.parseColor(colors[i]));
                    tvLabel1.setText(mapList.getKey());
                    tvAmt1.setText(String.format("%,.0f", mapList.getValue()));
                    i++;
                }

                LayoutInflater inflater2 = getLayoutInflater();
                TableRow tr2 = (TableRow) inflater2.inflate(R.layout.barchart_tablelayout_col2,
                        layoutBarChart, false);
                layoutBarChart.addView(tr2);
                TextView tvLabel2 = (TextView) tr2.getChildAt(1);
                TextView tvAmt2 = (TextView) tr2.getChildAt(2);
                tvLabel2.setPadding(0, 20, 0, 0);
                tvAmt2.setPadding(0, 20, 0, 0);
                tvLabel2.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel2.setAllCaps(true);
                tvAmt2.setAllCaps(true);
                tvAmt2.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel2.setText(getResources().getString(R.string.client_reports_sum));
                tvAmt2.setText(String.format("%,.0f", list.getToplamSatis()));

            } catch (Exception e) {
            }
        }
    }

    private void cariHesapOzetiTahsilatTablo(CariHesapOzeti list) {
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiTahsilatTablo);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                Map<String, Float> map = new LinkedHashMap<>();

                map.put(getResources().getString(R.string.month_january), list.getOcak());
                map.put(getResources().getString(R.string.month_february), list.getSubat());
                map.put(getResources().getString(R.string.month_mart), list.getMart());
                map.put(getResources().getString(R.string.month_april), list.getNisan());
                map.put(getResources().getString(R.string.month_may), list.getMayis());
                map.put(getResources().getString(R.string.month_june), list.getHaziran());
                map.put(getResources().getString(R.string.month_july), list.getTemmuz());
                map.put(getResources().getString(R.string.month_august), list.getAgustos());
                map.put(getResources().getString(R.string.month_september), list.getEylul());
                map.put(getResources().getString(R.string.month_october), list.getEkim());
                map.put(getResources().getString(R.string.month_november), list.getKasim());
                map.put(getResources().getString(R.string.month_december), list.getAralik());

                String[] colors = new String[12];
                colors[0] = "#f39c12";
                colors[1] = "#C3B091";
                colors[2] = "#8e44ad";
                colors[3] = "#FADA5E";
                colors[4] = "#16a085";
                colors[5] = "#FF0000";
                colors[6] = "#03DAC5";
                colors[7] = "#FFBB86FC";
                colors[8] = "#FADA5E";
                colors[9] = "#2980b9";
                colors[10] = "#f39c12";
                colors[11] = "#03DAC5";

                LayoutInflater inflater = getLayoutInflater();
                TableRow tr = (TableRow) inflater.inflate(R.layout.barchart_tablelayout_col2,
                        layoutBarChart, false);
                layoutBarChart.addView(tr);
                TextView tvLabel = (TextView) tr.getChildAt(1);
                TextView tvAmt = (TextView) tr.getChildAt(2);
                tvLabel.setPadding(0, 60, 0, 20);
                tvAmt.setPadding(0, 60, 0, 20);
                tvLabel.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel.setAllCaps(true);
                tvAmt.setAllCaps(true);
                tvAmt.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel.setText(getResources().getString(R.string.client_reports_moths));
                tvAmt.setText(getResources().getString(R.string.client_reports_collections));

                int i = 0;
                for (Map.Entry<String, Float> mapList : map.entrySet()) {
                    LayoutInflater inflater1 = getLayoutInflater();
                    TableRow tr1 = (TableRow) inflater1.inflate(R.layout.barchart_tablelayout_col2,
                            layoutBarChart, false);
                    layoutBarChart.addView(tr1);
                    LinearLayout linearLayoutColorContainer1 = (LinearLayout) tr1.getChildAt(0);
                    LinearLayout linearLayoutColor1 = (LinearLayout) linearLayoutColorContainer1.getChildAt(0);
                    TextView tvLabel1 = (TextView) tr1.getChildAt(1);
                    TextView tvAmt1 = (TextView) tr1.getChildAt(2);
                    linearLayoutColor1.setBackgroundColor(Color.parseColor(colors[i]));
                    tvLabel1.setText(mapList.getKey());
                    tvAmt1.setText(String.format("%,.0f", mapList.getValue()));
                    i++;
                }

                LayoutInflater inflater2 = getLayoutInflater();
                TableRow tr2 = (TableRow) inflater2.inflate(R.layout.barchart_tablelayout_col2,
                        layoutBarChart, false);
                layoutBarChart.addView(tr2);
                TextView tvLabel2 = (TextView) tr2.getChildAt(1);
                TextView tvAmt2 = (TextView) tr2.getChildAt(2);
                tvLabel2.setPadding(0, 20, 0, 0);
                tvAmt2.setPadding(0, 20, 0, 0);
                tvLabel2.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel2.setAllCaps(true);
                tvAmt2.setAllCaps(true);
                tvAmt2.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel2.setText(getResources().getString(R.string.client_reports_sum));
                tvAmt2.setText(String.format("%,.0f", list.getToplamTahsilat()));

            } catch (Exception e) {
            }
        }
    }

    private void cariHesapOzetiSatisTahsilatTablo(CariHesapOzeti list) {
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiSatisTahsilatTablo);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                String[] months = new String[12];
                months[0] = getResources().getString(R.string.month_january);
                months[1] = getResources().getString(R.string.month_february);
                months[2] = getResources().getString(R.string.month_mart);
                months[3] = getResources().getString(R.string.month_april);
                months[4] = getResources().getString(R.string.month_may);
                months[5] = getResources().getString(R.string.month_june);
                months[6] = getResources().getString(R.string.month_july);
                months[7] = getResources().getString(R.string.month_august);
                months[8] = getResources().getString(R.string.month_september);
                months[9] = getResources().getString(R.string.month_october);
                months[10] = getResources().getString(R.string.month_november);
                months[11] = getResources().getString(R.string.month_december);

                String[] colors = new String[12];
                colors[0] = "#f39c12";
                colors[1] = "#C3B091";
                colors[2] = "#8e44ad";
                colors[3] = "#FADA5E";
                colors[4] = "#16a085";
                colors[5] = "#FF0000";
                colors[6] = "#03DAC5";
                colors[7] = "#FFBB86FC";
                colors[8] = "#FADA5E";
                colors[9] = "#2980b9";
                colors[10] = "#f39c12";
                colors[11] = "#03DAC5";

                LayoutInflater inflater = getLayoutInflater();
                TableRow tr = (TableRow) inflater.inflate(R.layout.barchart_tablelayout_col3,
                        layoutBarChart, false);
                layoutBarChart.addView(tr);
                TextView tvLabel = (TextView) tr.getChildAt(1);
                TextView tvAmt = (TextView) tr.getChildAt(2);
                TextView tvBmt = (TextView) tr.getChildAt(3);
                tvLabel.setPadding(0, 60, 0, 20);
                tvAmt.setPadding(0, 60, 0, 20);
                tvBmt.setPadding(0, 60, 0, 20);
                tvLabel.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel.setAllCaps(true);
                tvAmt.setAllCaps(true);
                tvAmt.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvBmt.setAllCaps(true);
                tvBmt.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel.setText(getResources().getString(R.string.client_reports_moths));
                tvAmt.setText(getResources().getString(R.string.client_reports_sales));
                tvBmt.setText(getResources().getString(R.string.client_reports_collections));

                for (int i = 0; i < 12; i++) {
                    LayoutInflater inflater1 = getLayoutInflater();
                    TableRow tr1 = (TableRow) inflater1.inflate(R.layout.barchart_tablelayout_col3,
                            layoutBarChart, false);
                    layoutBarChart.addView(tr1);
                    LinearLayout linearLayoutColorContainer1 = (LinearLayout) tr1.getChildAt(0);
                    LinearLayout linearLayoutColor1 = (LinearLayout) linearLayoutColorContainer1.getChildAt(0);
                    TextView tvLabel1 = (TextView) tr1.getChildAt(1);
                    TextView tvAmt1 = (TextView) tr1.getChildAt(2);
                    TextView tvBmt1 = (TextView) tr1.getChildAt(3);
                    linearLayoutColor1.setBackgroundColor(Color.parseColor(colors[i]));
                    tvLabel1.setText(months[i]);
                    switch (i) {
                        case 0:
                            tvAmt1.setText(String.format("%,.0f", list.getOcak()));
                            tvBmt1.setText(String.format("%,.0f", list.getOcakTahsilat()));
                            break;
                        case 1:
                            tvAmt1.setText(String.format("%,.0f", list.getSubat()));
                            tvBmt1.setText(String.format("%,.0f", list.getSubatTahsilat()));
                            break;
                        case 2:
                            tvAmt1.setText(String.format("%,.0f", list.getMart()));
                            tvBmt1.setText(String.format("%,.0f", list.getMartTahsilat()));
                            break;
                        case 3:
                            tvAmt1.setText(String.format("%,.0f", list.getNisan()));
                            tvBmt1.setText(String.format("%,.0f", list.getNisanTahsilat()));
                            break;
                        case 4:
                            tvAmt1.setText(String.format("%,.0f", list.getMayis()));
                            tvBmt1.setText(String.format("%,.0f", list.getMayisTahsilat()));
                            break;
                        case 5:
                            tvAmt1.setText(String.format("%,.0f", list.getHaziran()));
                            tvBmt1.setText(String.format("%,.0f", list.getHaziranTahsilat()));
                            break;
                        case 6:
                            tvAmt1.setText(String.format("%,.0f", list.getTemmuz()));
                            tvBmt1.setText(String.format("%,.0f", list.getTemmuzTahsilat()));
                            break;
                        case 7:
                            tvAmt1.setText(String.format("%,.0f", list.getAgustos()));
                            tvBmt1.setText(String.format("%,.0f", list.getAgustosTahsilat()));
                            break;
                        case 8:
                            tvAmt1.setText(String.format("%,.0f", list.getEylul()));
                            tvBmt1.setText(String.format("%,.0f", list.getEylulTahsilat()));
                            break;
                        case 9:
                            tvAmt1.setText(String.format("%,.0f", list.getEkim()));
                            tvBmt1.setText(String.format("%,.0f", list.getEkimTahsilat()));
                            break;
                        case 10:
                            tvAmt1.setText(String.format("%,.0f", list.getKasim()));
                            tvBmt1.setText(String.format("%,.0f", list.getKasimTahsilat()));
                            break;
                        case 11:
                            tvAmt1.setText(String.format("%,.0f", list.getAralik()));
                            tvBmt1.setText(String.format("%,.0f", list.getAralikTahsilat()));
                            break;
                    }

                }

                LayoutInflater inflater2 = getLayoutInflater();
                TableRow tr2 = (TableRow) inflater2.inflate(R.layout.barchart_tablelayout_col3,
                        layoutBarChart, false);
                layoutBarChart.addView(tr2);
                TextView tvLabel2 = (TextView) tr2.getChildAt(1);
                TextView tvAmt2 = (TextView) tr2.getChildAt(2);
                TextView tvAmt3 = (TextView) tr2.getChildAt(3);
                tvLabel2.setPadding(0, 20, 0, 0);
                tvAmt2.setPadding(0, 20, 0, 0);
                tvAmt3.setPadding(0, 20, 0, 0);
                tvLabel2.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel2.setAllCaps(true);
                tvAmt2.setAllCaps(true);
                tvAmt2.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvAmt3.setAllCaps(true);
                tvAmt3.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel2.setText(getResources().getString(R.string.client_reports_sum));
                tvAmt2.setText(String.format("%,.0f", list.getToplamSatis()));
                tvAmt3.setText(String.format("%,.0f", list.getToplamTahsilat()));

            } catch (Exception e) {
            }
        }
    }

    private void cariHesapOzetiBorcTablo(CariHesapOzeti list) {
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiBorcTablo);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                Map<String, Float> map = new LinkedHashMap<>();

                map.put(getResources().getString(R.string.month_january), list.getOcak());
                map.put(getResources().getString(R.string.month_february), list.getSubat());
                map.put(getResources().getString(R.string.month_mart), list.getMart());
                map.put(getResources().getString(R.string.month_april), list.getNisan());
                map.put(getResources().getString(R.string.month_may), list.getMayis());
                map.put(getResources().getString(R.string.month_june), list.getHaziran());
                map.put(getResources().getString(R.string.month_july), list.getTemmuz());
                map.put(getResources().getString(R.string.month_august), list.getAgustos());
                map.put(getResources().getString(R.string.month_september), list.getEylul());
                map.put(getResources().getString(R.string.month_october), list.getEkim());
                map.put(getResources().getString(R.string.month_november), list.getKasim());
                map.put(getResources().getString(R.string.month_december), list.getAralik());

                String[] colors = new String[12];
                colors[0] = "#f39c12";
                colors[1] = "#C3B091";
                colors[2] = "#8e44ad";
                colors[3] = "#FADA5E";
                colors[4] = "#16a085";
                colors[5] = "#FF0000";
                colors[6] = "#03DAC5";
                colors[7] = "#FFBB86FC";
                colors[8] = "#FADA5E";
                colors[9] = "#2980b9";
                colors[10] = "#f39c12";
                colors[11] = "#03DAC5";

                LayoutInflater inflater = getLayoutInflater();
                TableRow tr = (TableRow) inflater.inflate(R.layout.barchart_tablelayout_col2,
                        layoutBarChart, false);
                layoutBarChart.addView(tr);
                TextView tvLabel = (TextView) tr.getChildAt(1);
                TextView tvAmt = (TextView) tr.getChildAt(2);
                tvLabel.setPadding(0, 60, 0, 20);
                tvAmt.setPadding(0, 60, 0, 20);
                tvLabel.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel.setAllCaps(true);
                tvAmt.setAllCaps(true);
                tvAmt.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel.setText(getResources().getString(R.string.client_reports_moths));
                tvAmt.setText(getResources().getString(R.string.client_reports_debts));

                int i = 0;
                for (Map.Entry<String, Float> mapList : map.entrySet()) {
                    LayoutInflater inflater1 = getLayoutInflater();
                    TableRow tr1 = (TableRow) inflater1.inflate(R.layout.barchart_tablelayout_col2,
                            layoutBarChart, false);
                    layoutBarChart.addView(tr1);
                    LinearLayout linearLayoutColorContainer1 = (LinearLayout) tr1.getChildAt(0);
                    LinearLayout linearLayoutColor1 = (LinearLayout) linearLayoutColorContainer1.getChildAt(0);
                    TextView tvLabel1 = (TextView) tr1.getChildAt(1);
                    TextView tvAmt1 = (TextView) tr1.getChildAt(2);
                    linearLayoutColor1.setBackgroundColor(Color.parseColor(colors[i]));
                    tvLabel1.setText(mapList.getKey());
                    tvAmt1.setText(String.format("%,.0f", mapList.getValue()));
                    i++;
                }

                LayoutInflater inflater2 = getLayoutInflater();
                TableRow tr2 = (TableRow) inflater2.inflate(R.layout.barchart_tablelayout_col2,
                        layoutBarChart, false);
                layoutBarChart.addView(tr2);
                TextView tvLabel2 = (TextView) tr2.getChildAt(1);
                TextView tvAmt2 = (TextView) tr2.getChildAt(2);
                tvLabel2.setPadding(0, 20, 0, 0);
                tvAmt2.setPadding(0, 20, 0, 0);
                tvLabel2.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel2.setAllCaps(true);
                tvAmt2.setAllCaps(true);
                tvAmt2.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel2.setText(getResources().getString(R.string.client_reports_sum));
                tvAmt2.setText(String.format("%,.0f", list.getToplamBorc()));

            } catch (Exception e) {
            }
        }
    }

    private void cariHesapOzetiAlacakTablo(CariHesapOzeti list) {
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiAlacakTablo);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                Map<String, Float> map = new LinkedHashMap<>();

                map.put(getResources().getString(R.string.month_january), list.getOcak());
                map.put(getResources().getString(R.string.month_february), list.getSubat());
                map.put(getResources().getString(R.string.month_mart), list.getMart());
                map.put(getResources().getString(R.string.month_april), list.getNisan());
                map.put(getResources().getString(R.string.month_may), list.getMayis());
                map.put(getResources().getString(R.string.month_june), list.getHaziran());
                map.put(getResources().getString(R.string.month_july), list.getTemmuz());
                map.put(getResources().getString(R.string.month_august), list.getAgustos());
                map.put(getResources().getString(R.string.month_september), list.getEylul());
                map.put(getResources().getString(R.string.month_october), list.getEkim());
                map.put(getResources().getString(R.string.month_november), list.getKasim());
                map.put(getResources().getString(R.string.month_december), list.getAralik());

                String[] colors = new String[12];
                colors[0] = "#f39c12";
                colors[1] = "#C3B091";
                colors[2] = "#8e44ad";
                colors[3] = "#FADA5E";
                colors[4] = "#16a085";
                colors[5] = "#FF0000";
                colors[6] = "#03DAC5";
                colors[7] = "#FFBB86FC";
                colors[8] = "#FADA5E";
                colors[9] = "#2980b9";
                colors[10] = "#f39c12";
                colors[11] = "#03DAC5";

                LayoutInflater inflater = getLayoutInflater();
                TableRow tr = (TableRow) inflater.inflate(R.layout.barchart_tablelayout_col2,
                        layoutBarChart, false);
                layoutBarChart.addView(tr);
                TextView tvLabel = (TextView) tr.getChildAt(1);
                TextView tvAmt = (TextView) tr.getChildAt(2);
                tvLabel.setPadding(0, 60, 0, 20);
                tvAmt.setPadding(0, 60, 0, 20);
                tvLabel.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel.setAllCaps(true);
                tvAmt.setAllCaps(true);
                tvAmt.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel.setText(getResources().getString(R.string.client_reports_moths));
                tvAmt.setText(getResources().getString(R.string.client_reports_credit));

                int i = 0;
                for (Map.Entry<String, Float> mapList : map.entrySet()) {
                    LayoutInflater inflater1 = getLayoutInflater();
                    TableRow tr1 = (TableRow) inflater1.inflate(R.layout.barchart_tablelayout_col2,
                            layoutBarChart, false);
                    layoutBarChart.addView(tr1);
                    LinearLayout linearLayoutColorContainer1 = (LinearLayout) tr1.getChildAt(0);
                    LinearLayout linearLayoutColor1 = (LinearLayout) linearLayoutColorContainer1.getChildAt(0);
                    TextView tvLabel1 = (TextView) tr1.getChildAt(1);
                    TextView tvAmt1 = (TextView) tr1.getChildAt(2);
                    linearLayoutColor1.setBackgroundColor(Color.parseColor(colors[i]));
                    tvLabel1.setText(mapList.getKey());
                    tvAmt1.setText(String.format("%,.0f", mapList.getValue()));
                    i++;
                }

                LayoutInflater inflater2 = getLayoutInflater();
                TableRow tr2 = (TableRow) inflater2.inflate(R.layout.barchart_tablelayout_col2,
                        layoutBarChart, false);
                layoutBarChart.addView(tr2);
                TextView tvLabel2 = (TextView) tr2.getChildAt(1);
                TextView tvAmt2 = (TextView) tr2.getChildAt(2);
                tvLabel2.setPadding(0, 20, 0, 0);
                tvAmt2.setPadding(0, 20, 0, 0);
                tvLabel2.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel2.setAllCaps(true);
                tvAmt2.setAllCaps(true);
                tvAmt2.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel2.setText(getResources().getString(R.string.client_reports_sum));
                tvAmt2.setText(String.format("%,.0f", list.getToplamAlacak()));

            } catch (Exception e) {
            }
        }
    }

    private void cariHesapOzetiBakiyeTablo(CariHesapOzeti list) {
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiBakiyeTablo);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                Map<String, Float> map = new LinkedHashMap<>();

                map.put(getResources().getString(R.string.month_january), list.getOcak());
                map.put(getResources().getString(R.string.month_february), list.getSubat());
                map.put(getResources().getString(R.string.month_mart), list.getMart());
                map.put(getResources().getString(R.string.month_april), list.getNisan());
                map.put(getResources().getString(R.string.month_may), list.getMayis());
                map.put(getResources().getString(R.string.month_june), list.getHaziran());
                map.put(getResources().getString(R.string.month_july), list.getTemmuz());
                map.put(getResources().getString(R.string.month_august), list.getAgustos());
                map.put(getResources().getString(R.string.month_september), list.getEylul());
                map.put(getResources().getString(R.string.month_october), list.getEkim());
                map.put(getResources().getString(R.string.month_november), list.getKasim());
                map.put(getResources().getString(R.string.month_december), list.getAralik());

                String[] colors = new String[12];
                colors[0] = "#f39c12";
                colors[1] = "#C3B091";
                colors[2] = "#8e44ad";
                colors[3] = "#FADA5E";
                colors[4] = "#16a085";
                colors[5] = "#FF0000";
                colors[6] = "#03DAC5";
                colors[7] = "#FFBB86FC";
                colors[8] = "#FADA5E";
                colors[9] = "#2980b9";
                colors[10] = "#f39c12";
                colors[11] = "#03DAC5";

                LayoutInflater inflater = getLayoutInflater();
                TableRow tr = (TableRow) inflater.inflate(R.layout.barchart_tablelayout_col2,
                        layoutBarChart, false);
                layoutBarChart.addView(tr);
                TextView tvLabel = (TextView) tr.getChildAt(1);
                TextView tvAmt = (TextView) tr.getChildAt(2);
                tvLabel.setPadding(0, 60, 0, 20);
                tvAmt.setPadding(0, 60, 0, 20);
                tvLabel.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel.setAllCaps(true);
                tvAmt.setAllCaps(true);
                tvAmt.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel.setText(getResources().getString(R.string.client_reports_moths));
                tvAmt.setText(getResources().getString(R.string.client_reports_balance));

                int i = 0;
                for (Map.Entry<String, Float> mapList : map.entrySet()) {
                    LayoutInflater inflater1 = getLayoutInflater();
                    TableRow tr1 = (TableRow) inflater1.inflate(R.layout.barchart_tablelayout_col2,
                            layoutBarChart, false);
                    layoutBarChart.addView(tr1);
                    LinearLayout linearLayoutColorContainer1 = (LinearLayout) tr1.getChildAt(0);
                    LinearLayout linearLayoutColor1 = (LinearLayout) linearLayoutColorContainer1.getChildAt(0);
                    TextView tvLabel1 = (TextView) tr1.getChildAt(1);
                    TextView tvAmt1 = (TextView) tr1.getChildAt(2);
                    linearLayoutColor1.setBackgroundColor(Color.parseColor(colors[i]));
                    tvLabel1.setText(mapList.getKey());
                    tvAmt1.setText(String.format("%,.0f", mapList.getValue()));
                    i++;
                }

                LayoutInflater inflater2 = getLayoutInflater();
                TableRow tr2 = (TableRow) inflater2.inflate(R.layout.barchart_tablelayout_col2,
                        layoutBarChart, false);
                layoutBarChart.addView(tr2);
                TextView tvLabel2 = (TextView) tr2.getChildAt(1);
                TextView tvAmt2 = (TextView) tr2.getChildAt(2);
                tvLabel2.setPadding(0, 20, 0, 0);
                tvAmt2.setPadding(0, 20, 0, 0);
                tvLabel2.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel2.setAllCaps(true);
                tvAmt2.setAllCaps(true);
                tvAmt2.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel2.setText(getResources().getString(R.string.client_reports_sum));
                tvAmt2.setText(String.format("%,.0f", list.getToplamBakiye()));

            } catch (Exception e) {
            }
        }
    }

    private void cariHesapOzetiBorcTahsilatBakiyeTablo(CariHesapOzeti list) {
        LinearLayout layoutBarChart = findViewById(R.id.layoutCariHesapOzetiBorcTahsilatBakiyeTablo);

        if (list == null) {
            layoutBarChart.setVisibility(View.GONE);
        } else {
            layoutBarChart.setVisibility(View.VISIBLE);

            try {
                String[] months = new String[12];
                months[0] = getResources().getString(R.string.month_january);
                months[1] = getResources().getString(R.string.month_february);
                months[2] = getResources().getString(R.string.month_mart);
                months[3] = getResources().getString(R.string.month_april);
                months[4] = getResources().getString(R.string.month_may);
                months[5] = getResources().getString(R.string.month_june);
                months[6] = getResources().getString(R.string.month_july);
                months[7] = getResources().getString(R.string.month_august);
                months[8] = getResources().getString(R.string.month_september);
                months[9] = getResources().getString(R.string.month_october);
                months[10] = getResources().getString(R.string.month_november);
                months[11] = getResources().getString(R.string.month_december);

                String[] colors = new String[12];
                colors[0] = "#f39c12";
                colors[1] = "#C3B091";
                colors[2] = "#8e44ad";
                colors[3] = "#FADA5E";
                colors[4] = "#16a085";
                colors[5] = "#FF0000";
                colors[6] = "#03DAC5";
                colors[7] = "#FFBB86FC";
                colors[8] = "#FADA5E";
                colors[9] = "#2980b9";
                colors[10] = "#f39c12";
                colors[11] = "#03DAC5";

                LayoutInflater inflater = getLayoutInflater();
                TableRow tr = (TableRow) inflater.inflate(R.layout.barchart_tablelayout_col4,
                        layoutBarChart, false);
                layoutBarChart.addView(tr);
                TextView tvLabel = (TextView) tr.getChildAt(1);
                TextView tvAmt = (TextView) tr.getChildAt(2);
                TextView tvBmt = (TextView) tr.getChildAt(3);
                TextView tvCmt = (TextView) tr.getChildAt(4);
                tvLabel.setPadding(0, 60, 0, 20);
                tvAmt.setPadding(0, 60, 0, 20);
                tvBmt.setPadding(0, 60, 0, 20);
                tvCmt.setPadding(0, 60, 0, 20);
                tvLabel.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel.setAllCaps(true);
                tvAmt.setAllCaps(true);
                tvAmt.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvBmt.setAllCaps(true);
                tvBmt.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvCmt.setAllCaps(true);
                tvCmt.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel.setText(getResources().getString(R.string.client_reports_moths));
                tvAmt.setText(getResources().getString(R.string.client_reports_debts));
                tvBmt.setText(getResources().getString(R.string.client_reports_credit));
                tvCmt.setText(getResources().getString(R.string.client_reports_balance));

                for (int i = 0; i < 12; i++) {
                    LayoutInflater inflater1 = getLayoutInflater();
                    TableRow tr1 = (TableRow) inflater1.inflate(R.layout.barchart_tablelayout_col4,
                            layoutBarChart, false);
                    layoutBarChart.addView(tr1);
                    LinearLayout linearLayoutColorContainer1 = (LinearLayout) tr1.getChildAt(0);
                    LinearLayout linearLayoutColor1 = (LinearLayout) linearLayoutColorContainer1.getChildAt(0);
                    TextView tvLabel1 = (TextView) tr1.getChildAt(1);
                    TextView tvAmt1 = (TextView) tr1.getChildAt(2);
                    TextView tvBmt1 = (TextView) tr1.getChildAt(3);
                    TextView tvCmt1 = (TextView) tr1.getChildAt(4);
                    linearLayoutColor1.setBackgroundColor(Color.parseColor(colors[i]));
                    tvLabel1.setText(months[i]);
                    switch (i) {
                        case 0:
                            tvAmt1.setText(String.format("%,.0f", list.getOcak()));
                            tvBmt1.setText(String.format("%,.0f", list.getOcakTahsilat()));
                            tvCmt1.setText(String.format("%,.0f", list.getOcakBakiye()));
                            break;
                        case 1:
                            tvAmt1.setText(String.format("%,.0f", list.getSubat()));
                            tvBmt1.setText(String.format("%,.0f", list.getSubatTahsilat()));
                            tvCmt1.setText(String.format("%,.0f", list.getSubatBakiye()));
                            break;
                        case 2:
                            tvAmt1.setText(String.format("%,.0f", list.getMart()));
                            tvBmt1.setText(String.format("%,.0f", list.getMartTahsilat()));
                            tvCmt1.setText(String.format("%,.0f", list.getMartBakiye()));
                            break;
                        case 3:
                            tvAmt1.setText(String.format("%,.0f", list.getNisan()));
                            tvBmt1.setText(String.format("%,.0f", list.getNisanTahsilat()));
                            tvCmt1.setText(String.format("%,.0f", list.getNisanBakiye()));
                            break;
                        case 4:
                            tvAmt1.setText(String.format("%,.0f", list.getMayis()));
                            tvBmt1.setText(String.format("%,.0f", list.getMayisTahsilat()));
                            tvCmt1.setText(String.format("%,.0f", list.getMayisBakiye()));
                            break;
                        case 5:
                            tvAmt1.setText(String.format("%,.0f", list.getHaziran()));
                            tvBmt1.setText(String.format("%,.0f", list.getHaziranTahsilat()));
                            tvCmt1.setText(String.format("%,.0f", list.getHaziranBakiye()));
                            break;
                        case 6:
                            tvAmt1.setText(String.format("%,.0f", list.getTemmuz()));
                            tvBmt1.setText(String.format("%,.0f", list.getTemmuzTahsilat()));
                            tvCmt1.setText(String.format("%,.0f", list.getTemmuzBakiye()));
                            break;
                        case 7:
                            tvAmt1.setText(String.format("%,.0f", list.getAgustos()));
                            tvBmt1.setText(String.format("%,.0f", list.getAgustosTahsilat()));
                            tvCmt1.setText(String.format("%,.0f", list.getAgustosBakiye()));
                            break;
                        case 8:
                            tvAmt1.setText(String.format("%,.0f", list.getEylul()));
                            tvBmt1.setText(String.format("%,.0f", list.getEylulTahsilat()));
                            tvCmt1.setText(String.format("%,.0f", list.getEylulBakiye()));
                            break;
                        case 9:
                            tvAmt1.setText(String.format("%,.0f", list.getEkim()));
                            tvBmt1.setText(String.format("%,.0f", list.getEkimTahsilat()));
                            tvCmt1.setText(String.format("%,.0f", list.getEkimBakiye()));
                            break;
                        case 10:
                            tvAmt1.setText(String.format("%,.0f", list.getKasim()));
                            tvBmt1.setText(String.format("%,.0f", list.getKasimTahsilat()));
                            tvCmt1.setText(String.format("%,.0f", list.getKasimBakiye()));
                            break;
                        case 11:
                            tvAmt1.setText(String.format("%,.0f", list.getAralik()));
                            tvBmt1.setText(String.format("%,.0f", list.getAralikTahsilat()));
                            tvCmt1.setText(String.format("%,.0f", list.getAralikBakiye()));
                            break;
                    }

                }

                LayoutInflater inflater2 = getLayoutInflater();
                TableRow tr2 = (TableRow) inflater2.inflate(R.layout.barchart_tablelayout_col4,
                        layoutBarChart, false);
                layoutBarChart.addView(tr2);
                TextView tvLabel2 = (TextView) tr2.getChildAt(1);
                TextView tvAmt2 = (TextView) tr2.getChildAt(2);
                TextView tvAmt3 = (TextView) tr2.getChildAt(3);
                TextView tvAmt4 = (TextView) tr2.getChildAt(4);
                tvLabel2.setPadding(0, 20, 0, 0);
                tvAmt2.setPadding(0, 20, 0, 0);
                tvAmt3.setPadding(0, 20, 0, 0);
                tvAmt4.setPadding(0, 20, 0, 0);
                tvLabel2.setTypeface(tvLabel.getTypeface(), Typeface.BOLD);
                tvLabel2.setAllCaps(true);
                tvAmt2.setAllCaps(true);
                tvAmt2.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvAmt3.setAllCaps(true);
                tvAmt3.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvAmt4.setAllCaps(true);
                tvAmt4.setTypeface(tvAmt.getTypeface(), Typeface.BOLD);
                tvLabel2.setText(getResources().getString(R.string.client_reports_sum));
                tvAmt2.setText(String.format("%,.0f", list.getToplamSatis()));
                tvAmt3.setText(String.format("%,.0f", list.getToplamTahsilat()));
                tvAmt4.setText(String.format("%,.0f", list.getToplamBakiye()));

            } catch (Exception e) {
            }
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