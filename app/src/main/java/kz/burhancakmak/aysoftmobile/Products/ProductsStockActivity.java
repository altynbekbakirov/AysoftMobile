package kz.burhancakmak.aysoftmobile.Products;

import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Adapters.ItemsStockAdapter;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsToplamlar;
import kz.burhancakmak.aysoftmobile.R;

public class ProductsStockActivity extends AppCompatActivity {
    DatabaseHandler databaseHandler;
    SessionManagement session;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    private static final String KEY_LANG = "language";
    List<ItemsToplamlar> toplamlarList = new ArrayList<>();
    RecyclerView recyclerView;
    ItemsStockAdapter adapter;
    int kayitNo;
    TextView stockPhysical, stockActual;
    String KurusHaneSayisiStokMiktar, KurusHaneSayisiStokTutar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        if (!(userSettingMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(userSettingMap.get(KEY_LANG));
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
        KurusHaneSayisiStokMiktar = parametreGetir(FIRMA_NO, "KurusHaneSayisiStokMiktar", "0");
        KurusHaneSayisiStokTutar = parametreGetir(FIRMA_NO, "KurusHaneSayisiStokTutar", "0");

        stockActual = findViewById(R.id.stockActual);
        stockPhysical = findViewById(R.id.stockPhysical);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemsStockAdapter(toplamlarList, Integer.parseInt(KurusHaneSayisiStokMiktar));
        recyclerView.setAdapter(adapter);

        new ProductsStockTask().execute();
    }


    private class ProductsStockTask extends AsyncTask<Void, Void, Void> {
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
            stockActual.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", total));
            stockPhysical.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", total));
            products_progressBar.setVisibility(View.GONE);
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