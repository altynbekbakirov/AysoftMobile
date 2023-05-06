package kz.burhancakmak.aysoftmobile.DataImport;

import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Adapters.DataImportAdapter;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.CihazlarFirmaDepolar;
import kz.burhancakmak.aysoftmobile.Models.Clients.CihazlarFirmaOdemeSekli;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsQuery;
import kz.burhancakmak.aysoftmobile.Models.Clients.ShipInfo;
import kz.burhancakmak.aysoftmobile.Models.Dataimport.DataImportCount;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirma;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarMenu;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarQuery;
import kz.burhancakmak.aysoftmobile.Models.Firms.Doviz;
import kz.burhancakmak.aysoftmobile.Models.Products.Items;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsDepoStokYerleri;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsDepolar;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsDepolarAdresler;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsItmunita;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsPrclist;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsQuery;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsToplamlar;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsUnitBarcode;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsWithPrices;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DataImportActivity extends AppCompatActivity {

    SessionManagement session;
    CheckBox dataimport_clients, dataimport_products, dataimport_pictures, dataimport_pictures_not_existed;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    List<ItemsWithPrices> itemsList = new ArrayList<>();
    List<String> imagesList = new ArrayList<>();
    List<DataImportCount> infoList = new ArrayList<>();
    DatabaseHandler databaseHandler;
    CihazlarFirma firma;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        webSettingsMap = session.getWebSettings();

        if (!(userSettingMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(userSettingMap.get(KEY_LANG));
        }

        setContentView(R.layout.activity_data_import);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            databaseHandler = DatabaseHandler.getInstance(this);
            initViews();
            new DatabaseDefaultValues().execute();
        }

    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.main_nav_dataImport);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dataimport_clients = findViewById(R.id.dataimport_clients);
        dataimport_products = findViewById(R.id.dataimport_products);
        dataimport_pictures = findViewById(R.id.dataimport_pictures);
        dataimport_pictures_not_existed = findViewById(R.id.dataimport_pictures_not_existed);

        findViewById(R.id.dataimport_button_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoList.clear();
                new DownloadCihazlarMenu().execute();
                if (dataimport_clients.isChecked()) {
                    new DownloadClients().execute();
                } else if (dataimport_products.isChecked()) {
                    new DownloadProducts().execute();
                } else if (dataimport_pictures.isChecked()) {
                    if (dataimport_pictures_not_existed.isChecked()) {
                        new DownloadNewImagesRetrofit().execute();
                    } else {
                        new DownloadAllImagesRetrofit().execute();
                    }
                } else if (dataimport_pictures_not_existed.isChecked()) {
                    new DownloadNewImagesRetrofit().execute();
                }
            }
        });
    }

    private class DatabaseDefaultValues extends AsyncTask<Void, Void, Void> {
        RelativeLayout products_progressBar = findViewById(R.id.progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            firma = databaseHandler.selectCihazlarFirma(FIRMA_NO);
            DONEM_NO = firma.getDonemNo();
            if (databaseHandler.tableExists("AY_" + FIRMA_NO + "_Items")) {
                itemsList = databaseHandler.selectAllItems(firma.getOndegerFiyatGrubu1(), firma.getOndegerFiyatGrubu2(), "AND (1=1)");
                for (int i = 0; i < itemsList.size(); i++) {
                    ItemsWithPrices items = itemsList.get(i);
                    if (!items.getStokResim().trim().isEmpty()) {
                        imagesList.add(items.getStokResim());
                    }
                    if (!items.getStokResim1().trim().isEmpty()) {
                        imagesList.add(items.getStokResim1());
                    }
                    if (!items.getStokResim2().trim().isEmpty()) {
                        imagesList.add(items.getStokResim2());
                    }
                    if (!items.getStokResim3().trim().isEmpty()) {
                        imagesList.add(items.getStokResim3());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            products_progressBar.setVisibility(View.GONE);
        }
    }

    private class DownloadCihazlarMenu extends AsyncTask<Void, Void, Void> {
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        ProgressDialog pd;
        RetrofitApi retrofitApi;
        Call<CihazlarQuery> queryList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(DataImportActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage(getString(R.string.data_import_progressbar_parametre_downloading));
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();

            retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
            queryList = retrofitApi.getUser(
                    phoneId,
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Response<CihazlarQuery> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    CihazlarQuery cihazlarQuery = response.body();

                    if (!cihazlarQuery.getHata()) {
                        databaseHandler.deleteParametreTables();

                        if (cihazlarQuery.getCihazlarFirmalar().size() > 2) {

                            for (int i = 2; i < cihazlarQuery.getCihazlarFirmalar().size(); i++) {
                                CihazlarFirma firma = new CihazlarFirma();
                                String[] firmalar = cihazlarQuery.getCihazlarFirmalar().get(i).split("\\|");
                                firma.setKayitNo(Integer.parseInt(firmalar[0]));
                                firma.setCihazKayitNo(Integer.parseInt(firmalar[1]));
                                firma.setMenuGrupKayitNo(Integer.parseInt(firmalar[2]));
                                firma.setFirmaNo(firmalar[3]);
                                firma.setDonemNo(firmalar[4]);
                                firma.setSatisElemaniKodu(firmalar[5]);
                                firma.setTicariIslemGrubu(firmalar[6]);
                                firma.setIsyeri(Integer.parseInt(firmalar[7]));
                                firma.setDepo(Integer.parseInt(firmalar[8]));
                                firma.setOndegerFiyatGrubu1(firmalar[9]);
                                firma.setOndegerFiyatGrubu2(firmalar[10]);
                                firma.setOndegerKasaKodu(firmalar[11]);
                                firma.setOndegerAciklamaAlani(firmalar[12]);
                                firma.setDepoListesi1(firmalar[13]);
                                firma.setDepoListesi2(firmalar[14]);
                                firma.setCariFiltre(firmalar[15]);
                                firma.setCariSevkiyatAdresiFiltre(firmalar[16]);
                                firma.setStokFiltre(firmalar[17]);
                                firma.setFiyatFiltre(firmalar[18]);
                                firma.setResimAdresi(firmalar[19]);
                                firma.setDepo1Aciklama1(firmalar[20]);
                                firma.setDepo1Aciklama2(firmalar[21]);
                                firma.setDepo2Aciklama1(firmalar[22]);
                                firma.setDepo2Aciklama2(firmalar[23]);
                                firma.setStokEkraniGorunumSekli(firmalar[24]);
                                firma.setKullanim(firmalar[25]);
                                firma.setGecmisFirmaNo(firmalar[26]);
                                firma.setGecmisDonemNo(firmalar[27]);
                                firma.setFirmaAdi1(firmalar[28]);
                                firma.setFirmaAdi2(firmalar[29]);
                                firma.setFirmaAdi3(firmalar[30]);
                                firma.setYerelDovizKayitNo(Integer.parseInt(firmalar[31]));
                                firma.setRaporlamaDoviziKayitNo(Integer.parseInt(firmalar[32]));
                                databaseHandler.insertCihazlarFirma(firma);
                            }
                        }

                        if (cihazlarQuery.getCihazlarMenu().size() > 2) {
                            for (int i = 2; i < cihazlarQuery.getCihazlarMenu().size(); i++) {
                                CihazlarMenu menu = new CihazlarMenu();
                                String[] menuler = cihazlarQuery.getCihazlarMenu().get(i).split("\\|");
                                menu.setKayitNo(Integer.parseInt(menuler[0]));
                                menu.setMenuGrupKayitNo(Integer.parseInt(menuler[1]));
                                menu.setTip(Integer.parseInt(menuler[2]));
                                menu.setAciklama1(menuler[3]);
                                menu.setAciklama2(menuler[4]);
                                menu.setFiltre(menuler[5]);
                                menu.setSiralama(menuler[6]);
                                menu.setOndeger(Integer.parseInt(menuler[7]));
                                menu.setKullanim(menuler[8]);
                                if (!menuler[9].isEmpty()) menu.setSiraNo(menuler[9]);
                                if (!menuler[10].isEmpty())
                                    menu.setUstMenuKayitNo(Integer.parseInt(menuler[10]));
                                if (!menuler[11].isEmpty())
                                    menu.setMenuTipi(Integer.parseInt(menuler[11]));
                                databaseHandler.insertCihazlarMenu(menu);
                            }
                        }

                        if (cihazlarQuery.getCihazlarFirmaParametreler().size() > 2) {
                            for (int i = 2; i < cihazlarQuery.getCihazlarFirmaParametreler().size(); i++) {
                                CihazlarFirmaParametreler parametreler = new CihazlarFirmaParametreler();
                                String[] params = cihazlarQuery.getCihazlarFirmaParametreler().get(i).split("\\|");
                                parametreler.setKayitNo(Integer.parseInt(params[0]));
                                parametreler.setCihazlarFirmaKayitNo(Integer.parseInt(params[1]));
                                parametreler.setParametreTipi(params[2]);
                                parametreler.setParametreAdi(params[3]);
                                parametreler.setParametreDegeri(params[4]);
                                parametreler.setAciklama(params[5]);
                                parametreler.setMobilCihazdaDegistirebilir(Integer.parseInt(params[6]));
                                parametreler.setGrup(params[7]);
                                databaseHandler.insertCihazlarFirmaParametreler(parametreler);
                            }
                        }

                        if (cihazlarQuery.getDoviz().size() > 2) {
                            for (int i = 2; i < cihazlarQuery.getDoviz().size(); i++) {
                                Doviz doviz = new Doviz();
                                String[] params = cihazlarQuery.getDoviz().get(i).split("\\|");
                                doviz.setKayitNo(Integer.parseInt(params[0]));
                                doviz.setDovizKayitNo(Integer.parseInt(params[1]));
                                doviz.setDovizIsareti(params[2]);
                                doviz.setAciklama(params[3]);
                                databaseHandler.insertDoviz(doviz);
                            }
                        }

                        if (cihazlarQuery.getCihazlarFirmaOdemeSekli().size() > 2) {
                            for (int i = 2; i < cihazlarQuery.getCihazlarFirmaOdemeSekli().size(); i++) {
                                CihazlarFirmaOdemeSekli odemeSekli = new CihazlarFirmaOdemeSekli();
                                String[] params = cihazlarQuery.getCihazlarFirmaOdemeSekli().get(i).split("\\|");
                                odemeSekli.setKayitNo(Integer.parseInt(params[0]));
                                odemeSekli.setCihazlarFirmaKayitNo(Integer.parseInt(params[1]));
                                odemeSekli.setIslemYonu(params[2]);
                                odemeSekli.setAciklama1(params[3]);
                                odemeSekli.setAciklama2(params[4]);
                                odemeSekli.setAciklama3(params[5]);
                                databaseHandler.insertCihazlarFirmaOdemeSekli(odemeSekli);
                            }
                        }

                        if (cihazlarQuery.getCihazlarFirmaDepolar().size() > 2) {
                            for (int i = 2; i < cihazlarQuery.getCihazlarFirmaDepolar().size(); i++) {
                                CihazlarFirmaDepolar depolar = new CihazlarFirmaDepolar();
                                String[] params = cihazlarQuery.getCihazlarFirmaDepolar().get(i).split("\\|");
                                depolar.setKayitNo(Integer.parseInt(params[0]));
                                depolar.setFirmaKayitNo(Integer.parseInt(params[1]));
                                depolar.setDepoNo(Integer.parseInt(params[2]));
                                depolar.setDepoIsmi(params[3]);
                                depolar.setDepoAciklama1(params[4]);
                                depolar.setDepoAciklama2(params[5]);
                                depolar.setDepoAciklama3(params[6]);
                                databaseHandler.insertCihazlarFirmaDepolar(depolar);
                            }
                        }

                    } else {
                        pd.dismiss();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            pd.dismiss();
        }
    }

    private class DownloadClients extends AsyncTask<Void, String, Void> {
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        ProgressDialog pd;
        RetrofitApi retrofitApi;
        Call<ClientsQuery> queryList;
        String hata = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(DataImportActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage(getString(R.string.data_import_progressbar_clients_dowloading));
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();

            retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
            queryList = retrofitApi.getClientList(
                    phoneId,
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD),
                    DONEM_NO,
                    FIRMA_NO);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pd.setMessage(getString(R.string.data_import_progressbar_products) + " " + values[0]);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Response<ClientsQuery> response = queryList.execute();

                if (response.isSuccessful() && response.body() != null) {

                    if (databaseHandler.tableExists("AY_" + FIRMA_NO + "_ClCard")) {
                        databaseHandler.deleteClients();
                    } else {
                        databaseHandler.createClientsTable(FIRMA_NO);
                    }

                    if (databaseHandler.tableExists("AY_" + FIRMA_NO + "_ShipInfo")) {
                        databaseHandler.deleteShipInfo();
                    } else {
                        databaseHandler.createShipInfoTable(FIRMA_NO);
                    }

                    ClientsQuery clientsQuery = response.body();

                    if (clientsQuery.getClCard().size() > 2) {
                        publishProgress(getString(R.string.data_import_progressbar_clients));
                        for (int i = 2; i < clientsQuery.getClCard().size(); i++) {
                            String[] clients = clientsQuery.getClCard().get(i).split("\\|");
                            ClCard clCard = new ClCard();
                            clCard.setKayitNo(Integer.parseInt(clients[0]));
                            clCard.setKod(clients[1]);
                            clCard.setUnvani1(clients[2]);
                            clCard.setUnvani2(clients[3]);
                            clCard.setFiyatGrubu(clients[4]);
                            clCard.setOzelKod1(clients[5]);
                            clCard.setOzelKod2(clients[6]);
                            clCard.setOzelKod3(clients[7]);
                            clCard.setOzelKod4(clients[8]);
                            clCard.setOzelKod5(clients[9]);
                            clCard.setAdres1(clients[10]);
                            clCard.setAdres2(clients[11]);
                            clCard.setSehir(clients[12]);
                            clCard.setTicariIslemGrubu(clients[13]);
                            clCard.setTelefon1(clients[14]);
                            clCard.setTelefon2(clients[15]);
                            clCard.setVergiNo(clients[16]);
                            clCard.setIndirimOrani(clients[17]);
                            if (clients[18].isEmpty()) {
                                clCard.setKordinatLongitude(0.0);
                            } else {
                                clCard.setKordinatLongitude(Double.parseDouble(clients[18]));
                            }
                            if (clients[19].isEmpty()) {
                                clCard.setKordinatLatitute(0.0);
                            } else {
                                clCard.setKordinatLatitute(Double.parseDouble(clients[19]));
                            }
                            clCard.setIlgiliKisi1(clients[20]);
                            clCard.setIlgiliKisi2(clients[21]);
                            clCard.setEmailAdresi1(clients[22]);
                            clCard.setEmailAdresi2(clients[23]);
                            clCard.setSiparisGrubuKullanimi(clients[24]);
                            clCard.setSonSatisTarihi(clients[25]);
                            clCard.setSonTahsilatTarihi(clients[26]);
                            clCard.setSonSatisGunSayisi(clients[27]);
                            clCard.setSonTahsilatGunSayisi(clients[28]);
                            clCard.setPazartesi(clients[29]);
                            clCard.setPazartesiSiraNo(clients[30]);
                            clCard.setSali(clients[31]);
                            clCard.setSaliSiraNo(clients[32]);
                            clCard.setCarsamba(clients[33]);
                            clCard.setCarsambaSiraNo(clients[34]);
                            clCard.setPersembe(clients[35]);
                            clCard.setPersembeSiraNo(clients[36]);
                            clCard.setCuma(clients[37]);
                            clCard.setCumaSiraNo(clients[38]);
                            clCard.setCumartesi(clients[39]);
                            clCard.setCumartesiSiraNo(clients[40]);
                            clCard.setPazar(clients[41]);
                            clCard.setPazarSiraNo(clients[42]);
                            clCard.setSiparisGunleriToplami(Double.parseDouble(clients[43]));
                            clCard.setRiskLimiti(Double.parseDouble(clients[44]));
                            clCard.setNetSatis(Double.parseDouble(clients[45]));
                            clCard.setNetTahsilat(Double.parseDouble(clients[46]));
                            clCard.setBakiye(Double.parseDouble(clients[47]));
                            clCard.setWhatsappId(clients[48]);
                            clCard.setTelegramId(clients[49]);
                            clCard.setKartTipi(clients[50]);
                            clCard.setFoto1(clients[51]);
                            clCard.setFoto2(clients[52]);
                            clCard.setFoto3(clients[53]);
                            clCard.setOdemeSekli(Integer.parseInt(clients[54]));
                            clCard.setDovizKayitNo(Integer.parseInt(clients[55]));
                            clCard.setDovizIsareti(clients[56]);
                            databaseHandler.insertClients(clCard);
                        }
                        infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_clients), clientsQuery.getClCard().size() - 2));
                    }


                    if (clientsQuery.getShipInfo().size() > 2) {
                        publishProgress(getString(R.string.data_import_progressbar_shipinfo));
                        for (int i = 2; i < clientsQuery.getShipInfo().size(); i++) {
                            String[] itmunitas = clientsQuery.getShipInfo().get(i).split("\\|");
                            ShipInfo shipInfo = new ShipInfo();
                            shipInfo.setKayitNo(Integer.parseInt(itmunitas[0]));
                            shipInfo.setCariKayitNo(Integer.parseInt(itmunitas[1]));
                            shipInfo.setKod(itmunitas[2]);
                            shipInfo.setUnvani1(itmunitas[3]);
                            shipInfo.setUnvani2(itmunitas[4]);
                            shipInfo.setFiyatGrubu(itmunitas[5]);
                            shipInfo.setOzelKod1(itmunitas[6]);
                            shipInfo.setOzelKod2(itmunitas[7]);
                            shipInfo.setOzelKod3(itmunitas[8]);
                            shipInfo.setOzelKod4(itmunitas[9]);
                            shipInfo.setOzelKod5(itmunitas[10]);
                            shipInfo.setAdres1(itmunitas[11]);
                            shipInfo.setAdres2(itmunitas[12]);
                            shipInfo.setSehir(itmunitas[13]);
                            shipInfo.setTicariIslemGrubu(itmunitas[14]);
                            shipInfo.setTelefon1(itmunitas[15]);
                            shipInfo.setTelefon2(itmunitas[16]);
                            shipInfo.setVergiNo(itmunitas[17]);
                            shipInfo.setKordinatLongitude(itmunitas[18]);
                            shipInfo.setKordinatLatitute(itmunitas[19]);
                            shipInfo.setIligiliKisi1(itmunitas[20]);
                            shipInfo.setIligiliKisi2(itmunitas[21]);
                            shipInfo.setEmailAdresi1(itmunitas[22]);
                            shipInfo.setEmailAdresi2(itmunitas[23]);
                            shipInfo.setPazartesi(itmunitas[24]);
                            shipInfo.setPazartesiSiraNo(itmunitas[25]);
                            shipInfo.setSali(itmunitas[26]);
                            shipInfo.setSaliSiraNo(itmunitas[27]);
                            shipInfo.setCarsamba(itmunitas[28]);
                            shipInfo.setCarsambaSiraNo(itmunitas[29]);
                            shipInfo.setPersembe(itmunitas[30]);
                            shipInfo.setPersembeSiraNo(itmunitas[31]);
                            shipInfo.setCuma(itmunitas[32]);
                            shipInfo.setCumaSiraNo(itmunitas[33]);
                            shipInfo.setCumartesi(itmunitas[34]);
                            shipInfo.setCumartesiSiraNo(itmunitas[35]);
                            shipInfo.setPazar(itmunitas[36]);
                            shipInfo.setPazarSiraNo(itmunitas[37]);
                            shipInfo.setResim1(itmunitas[38]);
                            shipInfo.setResim2(itmunitas[39]);
                            shipInfo.setResim3(itmunitas[40]);
                            /*shipInfo.setWhatsappId(itmunitas[41]);
                            shipInfo.setTelegramId(itmunitas[42]);*/
                            databaseHandler.insertShipInfo(shipInfo);
                        }
                        infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_shipinfo), clientsQuery.getShipInfo().size() - 2));
                    }
                }
            } catch (NullPointerException | IllegalStateException | JsonSyntaxException | IOException e) {
                hata = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            if (hata != null) {
                sendDataToServerFailDialog("Clients " + hata);
                return;
            }
            if (dataimport_products.isChecked()) {
                new DownloadProducts().execute();
            } else if (dataimport_pictures.isChecked()) {
                if (dataimport_pictures_not_existed.isChecked()) {
                    new DownloadNewImagesRetrofit().execute();
                } else {
                    new DownloadAllImagesRetrofit().execute();
                }
            } else if (dataimport_pictures_not_existed.isChecked()) {
                new DownloadNewImagesRetrofit().execute();
            } else {
                showInfo();
            }
        }
    }

    private class DownloadProducts extends AsyncTask<Void, String, Void> {
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        ProgressDialog pd;
        Call<ItemsQuery> queryList;
        RetrofitApi retrofitApi;
        String hata = null;
        boolean addPrice = false;

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pd.setMessage(getString(R.string.data_import_progressbar_products) + " " + values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(DataImportActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage(getString(R.string.data_import_progressbar_products_dowloading));
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();

            retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
            queryList = retrofitApi.getStockList(
                    phoneId,
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD),
                    DONEM_NO,
                    FIRMA_NO,
                    1);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Response<ItemsQuery> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    if (databaseHandler.tableExists("AY_" + FIRMA_NO + "_Items")) {
                        databaseHandler.deleteItems();
                    } else {
                        databaseHandler.createItemsTable(FIRMA_NO);
                    }
                    if (databaseHandler.tableExists("AY_" + FIRMA_NO + "_ItemsItmunita")) {
                        databaseHandler.deleteItmunita();
                    } else {
                        databaseHandler.createItemsItmunitaTable(FIRMA_NO);
                    }
                    if (databaseHandler.tableExists("AY_" + FIRMA_NO + "_ItemsPrclist")) {
                        databaseHandler.deletePrclist();
                    } else {
                        databaseHandler.createItemsPrclistTable(FIRMA_NO);
                    }
                    if (databaseHandler.tableExists("AY_" + FIRMA_NO + "_ItemsUnitBarcode")) {
                        databaseHandler.deleteBarkod();
                    } else {
                        databaseHandler.createItemsBarkodTable(FIRMA_NO);
                    }
                    if (databaseHandler.tableExists("AY_" + FIRMA_NO + "_ItemsToplamlar")) {
                        databaseHandler.deleteToplam();
                    } else {
                        databaseHandler.createItemsToplamTable(FIRMA_NO);
                    }
                    if (databaseHandler.tableExists("AY_" + FIRMA_NO + "_Depolar")) {
                        databaseHandler.deleteDepolar();
                    } else {
                        databaseHandler.createDepolarTable(FIRMA_NO);
                    }
                    if (databaseHandler.tableExists("AY_" + FIRMA_NO + "_DepolarAdresler")) {
                        databaseHandler.deleteDepolarAdresler();
                    } else {
                        databaseHandler.createDepolarAdreslerTable(FIRMA_NO);
                    }
                    if (databaseHandler.tableExists("AY_" + FIRMA_NO + "_DepoStokYerleri")) {
                        databaseHandler.deleteDepoStokYerleri();
                    } else {
                        databaseHandler.createDepoStokYerleriTable(FIRMA_NO);
                    }
                    imagesList.clear();
                    ItemsQuery itemsQuery = response.body();

                    if (itemsQuery.getItems().size() > 2) {
                        publishProgress(getString(R.string.data_import_progressbar_products_items));
                        for (int i = 2; i < itemsQuery.getItems().size(); i++) {
                            String[] products = itemsQuery.getItems().get(i).split("\\|");
                            Items items = new Items();
                            items.setKayitNo(Integer.parseInt(products[0]));
                            items.setStokKodu(products[1]);
                            items.setUreticiKodu(products[2]);
                            items.setStokAdi1(products[3]);
                            items.setStokAdi2(products[4]);
                            items.setStokAdi3(products[5]);
                            items.setVKod(products[6]);
                            items.setVAciklama1(products[7]);
                            items.setVAciklama2(products[8]);
                            items.setVAciklama3(products[9]);
                            items.setResimDosyasiKucuk(products[10]);
                            items.setResimDosyasiBuyuk1(products[11]);
                            items.setResimDosyasiBuyuk2(products[12]);
                            items.setResimDosyasiBuyuk3(products[13]);

                            if (!products[10].trim().isEmpty()) {
                                imagesList.add(products[10]);
                            }
                            if (!products[11].trim().isEmpty()) {
                                imagesList.add(products[11]);
                            }
                            if (!products[12].trim().isEmpty()) {
                                imagesList.add(products[12]);
                            }
                            if (!products[13].trim().isEmpty()) {
                                imagesList.add(products[13]);
                            }

                            items.setGrupKodu(products[14]);
                            items.setOzelKod1(products[15]);
                            items.setOzelKod2(products[16]);
                            items.setOzelKod3(products[17]);
                            items.setOzelKod4(products[18]);
                            items.setOzelKod5(products[19]);
                            items.setMarkaKodu(products[20]);
                            items.setYetkiKodu(products[21]);
                            if (!products[22].isEmpty())
                                items.setSiparisGrubu(Integer.parseInt(products[22]));
                            if (!products[23].isEmpty())
                                items.setIndirimYapilamaz(Integer.parseInt(products[23]));
                            items.setKalan1(Double.parseDouble(products[24]));
                            items.setKalan2(Double.parseDouble(products[25]));
                            items.setNewIcon(products[26]);
                            items.setIndirimIcon(products[27]);
                            items.setKampanyaIcon(products[28]);
                            items.setSiparisCarpani(Integer.parseInt(products[29]));
                            databaseHandler.insertItems(items);
                        }
                        infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_products_items), itemsQuery.getItems().size() - 2));
                    }

                    if (itemsQuery.getItemsItmunita().size() > 2) {
                        publishProgress(getString(R.string.data_import_progressbar_products_items_itmunita));
                        for (int i = 2; i < itemsQuery.getItemsItmunita().size(); i++) {
                            String[] itmunitas = itemsQuery.getItemsItmunita().get(i).split("\\|");
                            ItemsItmunita itmunita = new ItemsItmunita();
                            itmunita.setKayitNo(Integer.parseInt(itmunitas[0]));
                            itmunita.setStokKayitNo(Integer.parseInt(itmunitas[1]));
                            itmunita.setSiraNo(Integer.parseInt(itmunitas[2]));
                            itmunita.setBirimSiraKayitNo(Integer.parseInt(itmunitas[3]));
                            itmunita.setBirim(itmunitas[4]);
                            itmunita.setHacim(Double.parseDouble(itmunitas[5]));
                            itmunita.setAgirlik(Double.parseDouble(itmunitas[6]));
                            itmunita.setCarpan1(Integer.parseInt(itmunitas[7]));
                            itmunita.setCarpan2(Integer.parseInt(itmunitas[8]));
                            databaseHandler.insertItmunita(itmunita);
                        }
                        infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_products_items_itmunita), itemsQuery.getItemsItmunita().size() - 2));
                    }

                    if (itemsQuery.getItemsUnitBarcode().size() > 2) {
                        publishProgress(getString(R.string.data_import_progressbar_products_barkod));
                        for (int i = 2; i < itemsQuery.getItemsUnitBarcode().size(); i++) {
                            String[] barcodes = itemsQuery.getItemsUnitBarcode().get(i).split("\\|");
                            ItemsUnitBarcode unitBarcode = new ItemsUnitBarcode();
                            unitBarcode.setKayitNo(Integer.parseInt(barcodes[0]));
                            unitBarcode.setItmunitaref(Integer.parseInt(barcodes[1]));
                            unitBarcode.setStokKayitNo(Integer.parseInt(barcodes[2]));
                            unitBarcode.setSatirNo(Integer.parseInt(barcodes[3]));
                            if (barcodes.length <= 4) {
                                unitBarcode.setBarkod("");
                            } else {
                                unitBarcode.setBarkod(barcodes[4]);
                            }
                            databaseHandler.insertBarkod(unitBarcode);
                        }
                        infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_products_barkod), itemsQuery.getItemsUnitBarcode().size() - 2));
                    }

                    if (itemsQuery.getItemsPrclist().size() > 2) {
                        publishProgress(getString(R.string.data_import_progressbar_products_prclist));
                        for (int i = 2; i < itemsQuery.getItemsPrclist().size(); i++) {
                            String[] prices = itemsQuery.getItemsPrclist().get(i).split("\\|");
                            ItemsPrclist prclist = new ItemsPrclist();
                            prclist.setKayitNo(Integer.parseInt(prices[0]));
                            prclist.setStokKayitNo(Integer.parseInt(prices[1]));
                            prclist.setBirimKayitNo(Integer.parseInt(prices[2]));
                            prclist.setFiyatGrubu(prices[3]);
                            prclist.setCariHesapKodu(prices[4]);
                            prclist.setBaslangicTarih(prices[6]);
                            prclist.setBitisTarih(prices[7]);
                            prclist.setDovizTipiKayitNo(Integer.parseInt(prices[8]));
                            prclist.setDovizIsareti(prices[9]);
                            if (prices[5] != null && Double.parseDouble(prices[5]) > 0) {
                                prclist.setFiyat(Double.parseDouble(prices[5]));
                                databaseHandler.insertPrclist(prclist);
                                addPrice = true;
                            } else {
                                addPrice = false;
                            }
                        }
//                        if (addPrice) {
                        infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_products_prclist), itemsQuery.getItemsPrclist().size() - 2));
//                        }
                    }

                    if (itemsQuery.getItemsToplamlar().size() > 2) {
                        publishProgress(getString(R.string.data_import_progressbar_products_toplamlar));
                        for (int i = 2; i < itemsQuery.getItemsToplamlar().size(); i++) {
                            String[] toplams = itemsQuery.getItemsToplamlar().get(i).split("\\|+");
                            ItemsToplamlar toplamlar = new ItemsToplamlar();
                            toplamlar.setKayitNo(Integer.parseInt(toplams[0]));
                            toplamlar.setStokKayitNo(Integer.parseInt(toplams[1]));
                            toplamlar.setDepoNo(Integer.parseInt(toplams[2]));
                            toplamlar.setDepoAdi(toplams[3]);
                            toplamlar.setToplam(Double.parseDouble(toplams[4]));
                            toplamlar.setStokKodu(toplams[5]);
                            if (toplams.length <= 6) {
                                toplamlar.setStokYeriKodu("");
                            } else {
                                toplamlar.setStokYeriKodu(toplams[6]);
                            }
                            databaseHandler.insertToplam(toplamlar);
                        }
                        infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_products_toplamlar), itemsQuery.getItemsToplamlar().size() - 2));
                    }

                    if (itemsQuery.getDepolar().size() > 2) {
                        publishProgress(getString(R.string.data_import_progressbar_products_warehouse));
                        for (int i = 2; i < itemsQuery.getDepolar().size(); i++) {
                            String[] depo = itemsQuery.getDepolar().get(i).split("\\|");
                            ItemsDepolar depolar = new ItemsDepolar();
                            depolar.setSiraNo(Integer.parseInt(depo[0]));
                            depolar.setDepoKayitNo(Integer.parseInt(depo[1]));
                            depolar.setDepoNo(Integer.parseInt(depo[2]));
                            depolar.setDepoAdi(depo[3]);
                            databaseHandler.insertDepolar(depolar);
                        }
                        infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_products_warehouse), itemsQuery.getDepolar().size() - 2));
                    }

                    if (itemsQuery.getDepolarAdresler().size() > 2) {
                        publishProgress(getString(R.string.data_import_progressbar_products_warehouse_addresses));
                        for (int i = 2; i < itemsQuery.getDepolarAdresler().size(); i++) {
                            String[] adresler = itemsQuery.getDepolarAdresler().get(i).split("\\|");
                            ItemsDepolarAdresler depolarAdresler = new ItemsDepolarAdresler();
                            depolarAdresler.setLokasyonKayitNo(Integer.parseInt(adresler[0]));
                            depolarAdresler.setDepoNo(Integer.parseInt(adresler[1]));
                            depolarAdresler.setLokasyonKodu(adresler[2]);
                            if (adresler.length <= 3) {
                                depolarAdresler.setLokasyonAdi("");
                            } else {
                                depolarAdresler.setLokasyonAdi(adresler[3]);
                            }
                            databaseHandler.insertDepolarAdresler(depolarAdresler);
                        }
                        infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_products_warehouse_addresses), itemsQuery.getDepolarAdresler().size() - 2));
                    }

                    if (itemsQuery.getDepoStokYerleri().size() > 2) {
                        publishProgress(getString(R.string.data_import_progressbar_products_shelves));
                        for (int i = 2; i < itemsQuery.getDepoStokYerleri().size(); i++) {
                            String[] yerler = itemsQuery.getDepoStokYerleri().get(i).split("\\|");
                            ItemsDepoStokYerleri stokYerleri = new ItemsDepoStokYerleri();
                            stokYerleri.setDepoNo(Integer.parseInt(yerler[0]));
                            stokYerleri.setDepoAdi(yerler[1]);
                            stokYerleri.setStokKodu(yerler[2]);
                            stokYerleri.setToplam(Integer.parseInt(yerler[3]));
                            if (yerler.length <= 4) {
                                stokYerleri.setStokYeriKodu("");
                            } else {
                                stokYerleri.setStokYeriKodu(yerler[4]);
                            }
                            databaseHandler.insertDepoStokYerleri(stokYerleri);
                        }
                        infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_products_shelves), itemsQuery.getDepoStokYerleri().size() - 2));
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
            pd.dismiss();
            if (hata != null) {
                sendDataToServerFailDialog("Products " + hata);
                return;
            }
            if (dataimport_pictures.isChecked()) {
                if (dataimport_pictures_not_existed.isChecked()) {
                    new DownloadNewImagesRetrofit().execute();
                } else {
                    new DownloadAllImagesRetrofit().execute();
                }
            } else if (dataimport_pictures_not_existed.isChecked()) {
                new DownloadNewImagesRetrofit().execute();
            } else {
                showInfo();
            }
        }
    }

    private class DownloadAllImages extends AsyncTask<Void, Integer, Void> {
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        int counter = 0;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DataImportActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.data_import_progressbar_images_downloading));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(getString(R.string.data_import_progressbar_images_download_count) + ": " + values[0] + " / " + imagesList.size());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int count;
            try {
                for (int i = 0; i < imagesList.size(); i++) {
                    try {
                        File file = new File(getExternalFilesDir("/aysoft") + File.separator + imagesList.get(i));
                        String urlImage = webAddress + firma.getResimAdresi() + imagesList.get(i);
                        URL url = new URL(urlImage);
                        URLConnection connection = url.openConnection();
                        connection.connect();
                        int lengthOfFile = connection.getContentLength();
                        InputStream input = new BufferedInputStream(url.openStream(), 8192);
                        OutputStream output = new FileOutputStream(file);
                        byte[] data = new byte[1024];
                        long total = 0;
                        while ((count = input.read(data)) != -1) {
                            total += count;
                            output.write(data, 0, count);
                        }
                        output.flush();
                        output.close();
                        input.close();
                        publishProgress(++counter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_images_download_count), counter));
            progressDialog.dismiss();
            if (dataimport_pictures_not_existed.isChecked()) {
                new DownloadNewImagesRetrofit().execute();
            } else {
                showInfo();
            }
        }
    }

    private class DownloadNewImages extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(getString(R.string.data_import_progressbar_new_images_count) + ": " + values[0] + " / " + imagesList.size());
        }

        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        int counter = 0;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DataImportActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.data_import_progressbar_new_images));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int count;
            try {
                for (int i = 0; i < imagesList.size(); i++) {
                    try {
                        File file = new File(getExternalFilesDir("/aysoft") + File.separator + imagesList.get(i));
                        if (file.exists()) {
                            continue;
                        }
                        String urlImage = webAddress + firma.getResimAdresi() + imagesList.get(i);
                        URL url = new URL(urlImage);
                        URLConnection connection = url.openConnection();
                        connection.connect();
                        int lengthOfFile = connection.getContentLength();
                        InputStream input = new BufferedInputStream(url.openStream(), 8192);
                        OutputStream output = new FileOutputStream(file);
                        byte[] data = new byte[1024];
                        while ((count = input.read(data)) != -1) {
                            output.write(data, 0, count);
                        }
                        output.flush();
                        output.close();
                        input.close();

                        publishProgress(++counter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_new_images_count), counter));
            progressDialog.dismiss();
            showInfo();
        }
    }

    private class DownloadAllImagesRetrofit extends AsyncTask<Void, Integer, Void> {

        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        int counter = 0;
        RetrofitApi retrofitApi;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DataImportActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.data_import_progressbar_images_downloading));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
            retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(getString(R.string.data_import_progressbar_images_download_count) + ": " + values[0] + " / " + imagesList.size());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < imagesList.size(); i++) {
                Call<ResponseBody> call = retrofitApi.downloadFileWithDynamicUrlSync(phoneId,
                        userSettingMap.get(KEY_NAME),
                        userSettingMap.get(KEY_PASSWORD),
                        DONEM_NO,
                        FIRMA_NO,
                        webAddress + firma.getResimAdresi() + imagesList.get(i));

                try {
                    Response<ResponseBody> response = call.execute();
                    if (response.isSuccessful()) {
                        writeResponseBodyToDisk(response.body(), imagesList.get(i));
                        publishProgress(++counter);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_images_download_count), counter));
            progressDialog.dismiss();
            if (dataimport_pictures_not_existed.isChecked()) {
                new DownloadNewImagesRetrofit().execute();
            } else {
                showInfo();
            }
        }
    }

    private class DownloadNewImagesRetrofit extends AsyncTask<Void, Integer, Void> {

        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        int counter = 0;
        RetrofitApi retrofitApi;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(DataImportActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.data_import_progressbar_new_images));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(getString(R.string.data_import_progressbar_images_download_count) + ": " + values[0] + " / " + imagesList.size());
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < imagesList.size(); i++) {
                File file = new File(getExternalFilesDir("/aysoft") + File.separator + imagesList.get(i));
                if (!file.exists()) {
                    Call<ResponseBody> call = retrofitApi.downloadFileWithDynamicUrlSync(phoneId,
                            userSettingMap.get(KEY_NAME),
                            userSettingMap.get(KEY_PASSWORD),
                            DONEM_NO,
                            FIRMA_NO,
                            webAddress + firma.getResimAdresi() + imagesList.get(i));
                    try {
                        Response<ResponseBody> response = call.execute();
                        if (response.isSuccessful()) {
                            writeResponseBodyToDisk(response.body(), imagesList.get(i));
                            publishProgress(++counter);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            infoList.add(new DataImportCount(getString(R.string.data_import_progressbar_images_download_count), counter));
            progressDialog.dismiss();
            showInfo();
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String filename) {
        try {
            File file = new File(getExternalFilesDir("/aysoft") + File.separator + filename);

            if (file.exists()) {
                if (file.delete()) {
                    Log.d("deleteFile", "writeResponseBodyToDisk: " + filename + " - " + file.getName());
                }
            }

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
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

    private void showInfo() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.alertStyle);
        builder.setTitle(getString(R.string.data_import_alert_info_title));
        builder.setIcon(R.drawable.ic_info);
        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.data_import_alert_layout, null);
        builder.setView(layout);
        RecyclerView recyclerView = layout.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DataImportAdapter adapter = new DataImportAdapter(infoList);
        recyclerView.setAdapter(adapter);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static String fixedLengthString(String text, int length) {
        StringBuilder word = new StringBuilder();
        word.append(text);
        if (length > text.length()) {
            for (int i = 0; i < length - text.length(); i++) {
                word.append(".");
            }
        }
//        return String.format("%-" + length + "." + length + "s", text);
        return word.toString();
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
}
