package kz.burhancakmak.aysoftmobile.Products;


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
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsSearch;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsSearchQuery;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;


public class ProductSearchActivity extends AppCompatActivity {
    SessionManagement session;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    DatabaseHandler databaseHandler;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    String kurusHaneSayisiStokMiktar, kurusHaneSayisiStokTutar;
    Toolbar toolbar;
    SearchView searchView;
    TextView itemsName, itemsNameRu, itemsCode, itemsBarcode, itemsCarpan1, itemsCarpan2, itemsUnit, itemsPrice, itemsPriceCevrim,
            itemsPriceEndDate, itemsFiyatBirimKod, itemsDovizKod, itemsSalePrice, itemsSalePriceUnit, itemsSalePriceUnitCarpan,
            itemsDailyRate, searchResult, itemsAmbarNo, itemsAmbarAdi, itemsAmbarMiktar, itemsAmbarBirim, itemsAmbarBirimCarpan, itemsAmbarSatilacakMiktar, itemsAmbarLocation;
    NestedScrollView netScroll;
    String aranan = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        webSettingsMap = session.getWebSettings();

        if (!(userSettingMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(userSettingMap.get(KEY_LANG));
        }

        setContentView(R.layout.activity_products_search);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            initViews();
        }
    }

    private void initViews() {
        databaseHandler = DatabaseHandler.getInstance(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.main_nav_price));

        netScroll = findViewById(R.id.netScroll);
        searchView = findViewById(R.id.searchView);
        itemsName = findViewById(R.id.itemsName);
        itemsNameRu = findViewById(R.id.itemsNameRu);
        itemsBarcode = findViewById(R.id.itemsBarcode);
        itemsCode = findViewById(R.id.itemsCode);
        itemsCarpan1 = findViewById(R.id.itemsCarpan1);
        itemsCarpan2 = findViewById(R.id.itemsCarpan2);
        itemsUnit = findViewById(R.id.itemsUnit);
        itemsPrice = findViewById(R.id.itemsPrice);
        itemsPriceCevrim = findViewById(R.id.itemsPriceCevrim);
        itemsPriceEndDate = findViewById(R.id.itemsPriceEndDate);
        itemsFiyatBirimKod = findViewById(R.id.itemsFiyatBirimKod);
        itemsDovizKod = findViewById(R.id.itemsDovizKod);
        itemsSalePrice = findViewById(R.id.itemsSalePrice);
        itemsSalePriceUnit = findViewById(R.id.itemsSalePriceUnit);
        itemsSalePriceUnitCarpan = findViewById(R.id.itemsSalePriceUnitCarpan);
        itemsDailyRate = findViewById(R.id.itemsDailyRate);
        searchResult = findViewById(R.id.searchResult);
        itemsAmbarNo = findViewById(R.id.itemsAmbarNo);
        itemsAmbarAdi = findViewById(R.id.itemsAmbarAdi);
        itemsAmbarMiktar = findViewById(R.id.itemsAmbarMiktar);
        itemsAmbarBirim = findViewById(R.id.itemsAmbarBirim);
        itemsAmbarBirimCarpan = findViewById(R.id.itemsAmbarBirimCarpan);
        itemsAmbarSatilacakMiktar = findViewById(R.id.itemsAmbarSatilacakMiktar);
        itemsAmbarLocation = findViewById(R.id.itemsAmbarLocation);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        kurusHaneSayisiStokMiktar = getParameters(FIRMA_NO, "kurusHaneSayisiStokMiktar", "0");
        kurusHaneSayisiStokTutar = getParameters(FIRMA_NO, "kurusHaneSayisiStokTutar", "0");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    aranan = query.trim().toLowerCase();
                    new GetDataFromWeb().execute();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private class GetDataFromWeb extends AsyncTask<Void, String, Void> {
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        String login = userSettingMap.get(KEY_NAME);
        String password = userSettingMap.get(KEY_PASSWORD);
        RetrofitApi retrofitApi;
        ProgressDialog pd;
        ItemsSearch itemsSearch;
        Call<ItemsSearchQuery> queryList;
        String errorMessage = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(ProductSearchActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage(getString(R.string.product_menu_search));
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();

            retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
            queryList = retrofitApi.getStockSearch(
                    phoneId,
                    login,
                    password,
                    FIRMA_NO,
                    DONEM_NO,
                    aranan
            );
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Response<ItemsSearchQuery> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    ItemsSearchQuery query = response.body();
                    if (!query.getHata()) {
                        itemsSearch = new ItemsSearch();
                        if (query.get0().size() > 1) {
                            for (int i = 1; i < query.get0().size(); i++) {
                                String[] items = query.get0().get(i).split("\\|");
                                itemsSearch.setStokKodu(items[0]);
                                itemsSearch.setStokAdiRu(items[1]);
                                itemsSearch.setStokAdiTr(items[2]);
                                itemsSearch.setBarkod(items[3]);
                                itemsSearch.setCarpan1(Integer.parseInt(items[4]));
                                itemsSearch.setCarpan2(Integer.parseInt(items[5]));
                                itemsSearch.setBirim(items[6]);
                                itemsSearch.setFiyat(Double.parseDouble(items[7]));
                                itemsSearch.setBirimCevrim(Integer.parseInt(items[8]));
                                itemsSearch.setSonTarih(items[9]);
                                itemsSearch.setFiyatBirimKod(items[10]);
                                itemsSearch.setDovizKod(items[11]);
                                itemsSearch.setSatilacakFiyat(Double.parseDouble(items[12]));
                                itemsSearch.setSatilacakBirimKod(items[13]);
                                itemsSearch.setSatilacakBirimCarpan(Integer.parseInt(items[14]));
                                itemsSearch.setGunlukKur(Double.parseDouble(items[14]));
                            }
                        }

                        if (query.get1().size() > 1) {
                            for (int i = 1; i < query.get1().size(); i++) {
                                String[] items = query.get1().get(i).split("\\|");
                                itemsSearch.setDepono(items[0]);
                                itemsSearch.setDepoadi(items[1]);
                                itemsSearch.setMiktar(Integer.parseInt(items[2]));
                                itemsSearch.setSatilacakbirim(items[3]);
                                itemsSearch.setSatilacakbirimcarpan(Integer.parseInt(items[4]));
                                itemsSearch.setSatilacakmiktar(Integer.parseInt(items[5]));
                                itemsSearch.setStoklokasyon(items[6]);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();

            if (errorMessage != null) {
                sendDataToServerFailDialog(errorMessage);
                return;
            }

            if (itemsSearch != null) {
                searchResult.setVisibility(View.GONE);
                netScroll.setVisibility(View.VISIBLE);
                if (itemsSearch.getStokAdiTr() != null) {
                    itemsName.setText(itemsSearch.getStokAdiTr());
                }
                if (itemsSearch.getStokAdiRu() != null) {
                    itemsNameRu.setText(itemsSearch.getStokAdiRu());
                }
                if (itemsSearch.getStokKodu() != null) itemsCode.setText(itemsSearch.getStokKodu());
                if (itemsSearch.getBarkod() != null) {
                    itemsBarcode.setText(itemsSearch.getBarkod());
                }
                if (itemsSearch.getCarpan1() != null) {
                    itemsCarpan1.setText(String.valueOf(itemsSearch.getCarpan1()));
                }
                if (itemsSearch.getCarpan2() != null) {
                    itemsCarpan2.setText(String.valueOf(itemsSearch.getCarpan2()));
                }
                if (itemsSearch.getBirim() != null) {
                    itemsUnit.setText(itemsSearch.getBirim());
                }
                if (itemsSearch.getFiyat() != null) {
                    itemsPrice.setText(String.format("%,." + Integer.parseInt(kurusHaneSayisiStokTutar) + "f", itemsSearch.getFiyat()));
                }
                if (itemsSearch.getBirimCevrim() != null) {
                    itemsPriceCevrim.setText(String.valueOf(itemsSearch.getBirimCevrim()));
                }
                if (itemsSearch.getSonTarih() != null) {
                    itemsPriceEndDate.setText(itemsSearch.getSonTarih());
                }
                if (itemsSearch.getFiyatBirimKod() != null) {
                    itemsFiyatBirimKod.setText(itemsSearch.getFiyatBirimKod());
                }
                if (itemsSearch.getDovizKod() != null) {
                    itemsDovizKod.setText(itemsSearch.getDovizKod());
                }
                if (itemsSearch.getSatilacakFiyat() != null) {
                    itemsSalePrice.setText(String.format("%,." + Integer.parseInt(kurusHaneSayisiStokTutar) + "f", itemsSearch.getSatilacakFiyat()));
                }
                if (itemsSearch.getSatilacakBirimKod() != null) {
                    itemsSalePriceUnit.setText(itemsSearch.getSatilacakBirimKod());
                }
                if (itemsSearch.getSatilacakBirimCarpan() != null) {
                    itemsSalePriceUnitCarpan.setText(String.valueOf(itemsSearch.getSatilacakBirimCarpan()));
                }
                if (itemsSearch.getGunlukKur() != null) {
                    itemsDailyRate.setText(String.format("%,." + Integer.parseInt(kurusHaneSayisiStokTutar) + "f", itemsSearch.getGunlukKur()));
                }
                if (itemsSearch.getDepono() != null) itemsAmbarNo.setText(itemsSearch.getDepono());
                if (itemsSearch.getDepoadi() != null) {
                    itemsAmbarAdi.setText(itemsSearch.getDepoadi());
                }
                if (itemsSearch.getMiktar() != null) {
                    itemsAmbarMiktar.setText(String.valueOf(itemsSearch.getMiktar()));
                }
                if (itemsSearch.getSatilacakbirim() != null) {
                    itemsAmbarBirim.setText(itemsSearch.getSatilacakbirim());
                }
                if (itemsSearch.getSatilacakBirimCarpan() != null) {
                    itemsAmbarBirimCarpan.setText(String.valueOf(itemsSearch.getSatilacakBirimCarpan()));
                }
                if (itemsSearch.getSatilacakmiktar() != null) {
                    itemsAmbarSatilacakMiktar.setText(String.valueOf(itemsSearch.getSatilacakmiktar()));
                }
                if (itemsSearch.getStoklokasyon() != null) {
                    itemsAmbarLocation.setText(itemsSearch.getStoklokasyon());
                }
            } else {
                netScroll.setVisibility(View.GONE);
                searchResult.setVisibility(View.VISIBLE);
            }

        }
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

    private String getParameters(String firmaNo, String parametre, String deger) {
        List<CihazlarFirmaParametreler> parametrelerList = databaseHandler.selectParametreGetir(firmaNo, parametre);
        String param;
        if (parametrelerList.size() == 1) {
            param = parametrelerList.get(0).getParametreDegeri();
        } else {
            param = deger;
        }
        return param;
    }

    private String convertDateFormat(String tarih) {
        DateFormat originalFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = originalFormat.parse(tarih);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return targetFormat.format(date);
    }

}
