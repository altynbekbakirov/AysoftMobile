package kz.burhancakmak.aysoftmobile.Revision;

import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.depo1Aciklama1;
import static kz.burhancakmak.aysoftmobile.MainActivity.depo2Aciklama1;
import static kz.burhancakmak.aysoftmobile.MainActivity.ondegerFiyatGrubu1;
import static kz.burhancakmak.aysoftmobile.MainActivity.ondegerFiyatGrubu2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Adapters.ItemsAdapter;
import kz.burhancakmak.aysoftmobile.Adapters.SiparisProductsAdapter;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientSepet;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsWithPrices;
import kz.burhancakmak.aysoftmobile.R;

public class RevisionDetailsActivity extends AppCompatActivity
        implements SiparisProductsAdapter.OnOrderClickListener, NavigationView.OnNavigationItemSelectedListener {
    SessionManagement session;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    DatabaseHandler databaseHandler;
    Toolbar toolbar;
    RecyclerView recyclerView;
    SiparisProductsAdapter itemsAdapter;
    SearchView searchView;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    private static String NAV_FILTER;
    private static int VIEW_TYPE;
    String kurusHaneSayisiStokMiktar, kurusHaneSayisiStokTutar, ikiDepoKullanimi, ikiFiyatKullanimi;
    List<CihazlarFirmaParametreler> parametrelerList = new ArrayList<>();
    List<ItemsWithPrices> productItemList = new ArrayList<>();
    private int itemCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        webSettingsMap = session.getWebSettings();

        if (!(userSettingMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(userSettingMap.get(KEY_LANG));
        }
        setContentView(R.layout.activity_revision_details);
        initView();
    }

    private void initView() {
        databaseHandler = DatabaseHandler.getInstance(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.product_menu_search));
        searchView = findViewById(R.id.searchView);
        parametrelerList = databaseHandler.selectParametreList(FIRMA_NO);
        kurusHaneSayisiStokMiktar = getParameters(FIRMA_NO, "KurusHaneSayisiStokMiktar", "0");
        kurusHaneSayisiStokTutar = getParameters(FIRMA_NO, "KurusHaneSayisiStokTutar", "0");
        ikiFiyatKullanimi = getParameters(FIRMA_NO, "IkiFiyatKullanimi", "0");
        ikiDepoKullanimi = getParameters(FIRMA_NO, "IkiDepoKullanimi", "0");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        itemsAdapter = new SiparisProductsAdapter(
                this,
                productItemList,
                this,
                Integer.parseInt(kurusHaneSayisiStokMiktar),
                Integer.parseInt(kurusHaneSayisiStokTutar),
                Integer.parseInt(ikiDepoKullanimi)
        );
        recyclerView.setAdapter(itemsAdapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    String[] items = query.trim().split("\\*");
                    if (items.length > 1) {
                        try {
                            itemCount = Integer.parseInt(items[0]);
                        } catch (Exception e) {
                            itemCount = 1;
                        }
                        NAV_FILTER = items[1];
                    } else {
                        itemCount = 1;
                        NAV_FILTER = query.trim();
                    }
                    new GetDataFromDatabase().execute();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class GetDataFromDatabase extends AsyncTask<Void, Void, Void> {
        String errorMessage = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productItemList.clear();
        }

        @Override
        protected Void doInBackground(Void... items) {
            try {
                productItemList = databaseHandler.selectItemsByBarcode(ondegerFiyatGrubu1, ondegerFiyatGrubu2, NAV_FILTER);
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (errorMessage != null) {
                showFailDialog(errorMessage);
                return;
            }
            if (productItemList.size() > 0) {
                ItemsWithPrices item = productItemList.get(0);
                item.setMiktar(itemCount);
                productItemList.set(0, item);

                itemsAdapter.setItemsList(
                        productItemList,
                        VIEW_TYPE,
                        depo1Aciklama1 + " " + getString(R.string.items_kalan1_label),
                        depo2Aciklama1.isEmpty() ? "" : depo2Aciklama1 + " " + getString(R.string.items_kalan2_label),
                        ondegerFiyatGrubu1.isEmpty() ? "" : ondegerFiyatGrubu1 + " " + getString(R.string.items_fiyat1_label),
                        ondegerFiyatGrubu2.isEmpty() ? "" : ondegerFiyatGrubu2 + " " + getString(R.string.items_fiyat2_label));
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

    private void showFailDialog(String errorMessage) {
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }


}