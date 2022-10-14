package kz.burhancakmak.aysoftmobile.Products;

import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import kz.burhancakmak.aysoftmobile.Adapters.ItemsStockAdapter;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsDepolarAdresler;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsExtractQuery;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsToplamlar;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;

public class ProductsStockActivity extends AppCompatActivity implements ItemsStockAdapter.ItemsStockListener {
    DatabaseHandler databaseHandler;
    SessionManagement session;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    private static final String KEY_LANG = "language";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    List<ItemsToplamlar> toplamlarList = new ArrayList<>();
    List<CihazlarFirmaParametreler> parametrelerList = new ArrayList<>();
    RecyclerView recyclerView;
    ItemsStockAdapter adapter;
    int kayitNo, selectedItem;
    TextView stockPhysical, stockCode, stockName;
    String KurusHaneSayisiStokMiktar, KurusHaneSayisiStokTutar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        webSettingsMap = session.getWebSettings();

        if ((userSettingMap.get(KEY_LANG) != null)) {
            setPhoneDefaultLanguage(Objects.requireNonNull(userSettingMap.get(KEY_LANG)));
        }
        setContentView(R.layout.activity_products_stock);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.products_info_stock_status);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        kayitNo = intent.getIntExtra("kayitNo", -1);
        databaseHandler = DatabaseHandler.getInstance(this);
        parametrelerList = databaseHandler.selectParametreList(FIRMA_NO);
        KurusHaneSayisiStokMiktar = parametreGetir("KurusHaneSayisiStokMiktar");
        KurusHaneSayisiStokTutar = parametreGetir("KurusHaneSayisiStokTutar");

        stockCode = findViewById(R.id.stockCode);
        stockName = findViewById(R.id.stockName);
        stockPhysical = findViewById(R.id.stockPhysical);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemsStockAdapter(toplamlarList, Integer.parseInt(KurusHaneSayisiStokMiktar), this);
        recyclerView.setAdapter(adapter);

        new GetDataFromDatabase().execute();
    }

    @Override
    public void onItemClick(int position) {
        alertAddNewShelve(position);
    }

    private class GetDataFromDatabase extends AsyncTask<Void, Void, Void> {
        double total = 0;
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... items) {
            toplamlarList = databaseHandler.selectAllItemsByAmbar(kayitNo);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.setList(toplamlarList);
            for (int i = 0; i < toplamlarList.size(); i++) {
                total += toplamlarList.get(i).getToplam();
            }
            stockPhysical.setText(String.format("%,." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", total));
            stockCode.setText(toplamlarList.get(0).getStokKodu());
            stockName.setText(toplamlarList.get(0).getStokAciklamasi());
            products_progressBar.setVisibility(View.GONE);
        }
    }

    private class SendDataToWeb extends AsyncTask<String, Void, Void> {
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        String login = userSettingMap.get(KEY_NAME);
        String password = userSettingMap.get(KEY_PASSWORD);
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);
        RetrofitApi retrofitApi;
        Call<ItemsExtractQuery> queryList;
        String hata = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... values) {
            retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
            queryList = retrofitApi.updateStockShelvePlace(
                    phoneId,
                    FIRMA_NO,
                    DONEM_NO,
                    login,
                    password,
                    String.valueOf(toplamlarList.get(Integer.parseInt(values[0])).getStokKayitNo()), // StokKayitNo
                    toplamlarList.get(Integer.parseInt(values[0])).getStokKodu(), // StokKodu
                    String.valueOf(toplamlarList.get(Integer.parseInt(values[0])).getDepoNo()), // DepoNo
                    toplamlarList.get(Integer.parseInt(values[0])).getStokYeriKodu()// LokasyonKayitNo
            );
            try {
                Response<ItemsExtractQuery> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    ItemsExtractQuery query = response.body();
                    if (!query.getHata()) {
                        ItemsToplamlar itemsToplamlar = toplamlarList.get(Integer.parseInt(values[0]));
                        adapter.updateItem(Integer.parseInt(values[0]));
                        databaseHandler.updateShelveName(itemsToplamlar, String.valueOf(toplamlarList.get(Integer.parseInt(values[0])).getKayitNo()));
                    } else {
                        hata = query.getHataMesaj();
                    }
                }
            } catch (IOException e) {
                hata = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            products_progressBar.setVisibility(View.GONE);
            if (hata != null) {
                sendDataToServerFailDialog(hata);
            }
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

    private String parametreGetir(String param) {
        String parametreDeger = "0";
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

    private void alertAddNewShelve(int position) {
        List<ItemsDepolarAdresler> addresses = databaseHandler.selectDepolarAdresler(toplamlarList.get(position).getDepoNo());
        List<String> shelveList = new ArrayList<>();

        for (ItemsDepolarAdresler address : addresses) {
            shelveList.add(address.getLokasyonAdi());
        }
        shelveList.add(0, "");

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setCancelable(true);
        View view = getLayoutInflater().inflate(R.layout.items_stocks_shelve_update_layout, null);
        builder.setView(view);

        ArrayAdapter<String> shelveAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_layout, shelveList);
        Spinner spinner = view.findViewById(R.id.items_stock_combo);
        shelveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(shelveAdapter);
        shelveAdapter.notifyDataSetChanged();

        if (toplamlarList.get(position).getStokYeriKodu() != null) {
            for (int i = 0; i < addresses.size(); i++) {
                if (addresses.get(i).getLokasyonKodu().equals(toplamlarList.get(position).getStokYeriKodu())) {
                    selectedItem = i;
                    spinner.setSelection(selectedItem + 1);
                    break;
                }
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = i;
                if (selectedItem > 0) {
                    toplamlarList.get(position).setStokYeriKodu(String.valueOf(addresses.get(selectedItem - 1).getLokasyonKayitNo()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (selectedItem > 0)
                    new SendDataToWeb().execute(String.valueOf(position));
            }
        });
        builder.show();
    }

}