package kz.burhancakmak.aysoftmobile.Products;

import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.depo1Aciklama1;
import static kz.burhancakmak.aysoftmobile.MainActivity.depo2Aciklama1;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsPrclist;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsWithPrices;
import kz.burhancakmak.aysoftmobile.R;

public class ProductsInfoActivity extends AppCompatActivity {
    DatabaseHandler databaseHandler;
    SessionManagement session;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    private static final String KEY_LANG = "language";
    ItemsWithPrices items;
    Toolbar toolbar;
    int kayitNo;
    String KurusHaneSayisiStokMiktar, KurusHaneSayisiStokTutar, IkiFiyatKullanimi, IkiDepoKullanimi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        if (!(userSettingMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(userSettingMap.get(KEY_LANG));
        }
        setContentView(R.layout.activity_products_info);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            initViews();
        }

    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.products_info_toolbar_title);
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

        new GetDataFromDatabase().execute();
    }

    private class GetDataFromDatabase extends AsyncTask<Void, Void, Void> {
        RelativeLayout progress_layout = findViewById(R.id.progress_layout);
        List<ItemsPrclist> prclists = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_layout.setVisibility(View.VISIBLE);
            prclists.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (kayitNo != -1) {
                items = databaseHandler.selectProductInfo(kayitNo, "NP-PAZAR");
                KurusHaneSayisiStokMiktar = parametreGetir(FIRMA_NO, "KurusHaneSayisiStokMiktar", "0");
                KurusHaneSayisiStokTutar = parametreGetir(FIRMA_NO, "KurusHaneSayisiStokTutar", "0");
                IkiFiyatKullanimi = parametreGetir(FIRMA_NO, "IkiFiyatKullanimi", "0");
                IkiDepoKullanimi = parametreGetir(FIRMA_NO, "IkiDepoKullanimi", "0");
                prclists = databaseHandler.selectProductPrices(kayitNo);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (items != null) {
                toolbar.setSubtitle(items.getStokAdi1());
                ImageView itemsImage = findViewById(R.id.itemsImage);
                TextView itemsName = findViewById(R.id.itemsName);
                TextView itemsCode = findViewById(R.id.itemsCode);
                TextView infoStok1 = findViewById(R.id.infoStok1);
                TextView infoStok2 = findViewById(R.id.infoStok2);
                TextView infoOzellik1 = findViewById(R.id.infoOzellik1);
                TextView infoOzellik2 = findViewById(R.id.infoOzellik2);
                TextView infoOzellik3 = findViewById(R.id.infoOzellik3);
                TextView infoOzellik4 = findViewById(R.id.infoOzellik4);
                TextView infoOzellik5 = findViewById(R.id.infoOzellik5);
                TextView infoFiyatTitle = findViewById(R.id.infoFiyatTitle);
                LinearLayout infoFiyatLayout1 = findViewById(R.id.infoFiyatLayout1);
                LinearLayout infoFiyatLayout2 = findViewById(R.id.infoFiyatLayout2);
                LinearLayout infoFiyatLayout3 = findViewById(R.id.infoFiyatLayout3);
                LinearLayout infoFiyatLayout4 = findViewById(R.id.infoFiyatLayout4);
                LinearLayout infoFiyatLayout5 = findViewById(R.id.infoFiyatLayout5);
                TextView infoFiyatLabel1 = findViewById(R.id.infoFiyatLabel1);
                TextView infoFiyatLabel2 = findViewById(R.id.infoFiyatLabel2);
                TextView infoFiyatLabel3 = findViewById(R.id.infoFiyatLabel3);
                TextView infoFiyatLabel4 = findViewById(R.id.infoFiyatLabel4);
                TextView infoFiyatLabel5 = findViewById(R.id.infoFiyatLabel5);
                TextView infoFiyat1 = findViewById(R.id.infoFiyat1);
                TextView infoFiyat2 = findViewById(R.id.infoFiyat2);
                TextView infoFiyat3 = findViewById(R.id.infoFiyat3);
                TextView infoFiyat4 = findViewById(R.id.infoFiyat4);
                TextView infoFiyat5 = findViewById(R.id.infoFiyat5);
                TextView infoUretim = findViewById(R.id.infoUretim);
                TextView infoSiparisSatin = findViewById(R.id.infoSiparisSatin);
                TextView infoSiparisSatis = findViewById(R.id.infoSiparisSatis);
                TextView infoUnit1 = findViewById(R.id.infoUnit1);
                TextView infoUnit2 = findViewById(R.id.infoUnit2);
                TextView infoBarcode = findViewById(R.id.infoBarcode);
                TextView infoBarcode2 = findViewById(R.id.infoBarcode2);
                TextView infoStok1Label = findViewById(R.id.infoStok1Label);
                TextView infoStok2Label = findViewById(R.id.infoStok2Label);
                LinearLayout layoutBarcode2 = findViewById(R.id.layoutBarcode2);

                itemsName.setText(items.getStokAdi1());
                itemsCode.setText(items.getStokKodu());
                infoStok1.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokMiktar) + "f", items.getKalan1()));

                infoStok1Label.setText(depo1Aciklama1 + " " + getString(R.string.items_kalan1_label));
                if (Integer.parseInt(IkiDepoKullanimi) == 1) {
                    if (!depo2Aciklama1.isEmpty()) {
                        infoStok2Label.setText(depo2Aciklama1 + " " + getString(R.string.items_kalan2_label));
                        infoStok2.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokMiktar) + "f", items.getKalan2()));
                    } else {
                        infoStok2Label.setVisibility(View.GONE);
                        infoStok2.setVisibility(View.GONE);
                    }
                } else {
                    infoStok2Label.setVisibility(View.GONE);
                    infoStok2.setVisibility(View.GONE);
                }
                infoOzellik1.setText(items.getOzelKod1());
                infoOzellik2.setText(items.getOzelKod2());
                infoOzellik3.setText(items.getOzelKod3());
                infoOzellik4.setText(items.getOzelKod4());
                infoOzellik5.setText(items.getOzelKod5());
//                infoFiyat1.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", items.getFiyat1()) + " (" + items.getBirim() + ")");
                infoUretim.setText(items.getUreticiKodu());
                infoSiparisSatin.setText(String.valueOf(items.getSiparisSatinalma()));
                infoSiparisSatis.setText(String.valueOf(items.getSiparisSatis()));
                infoUnit1.setText(items.getBirim());
                if (items.getBirim2() != null) {
                    infoUnit2.setText(items.getBirim2() + " (" + items.getCarpan2() + " " + items.getBirim() + ")");
                }
                if (items.getBarkod() != null) {
                    infoBarcode.setText(items.getBarkod() + " (" + items.getBirim() + ")");
                }
                if (items.getBarkod2() != null) {
                    layoutBarcode2.setVisibility(View.VISIBLE);
                    infoBarcode2.setText(items.getBarkod2() + " (" + items.getBirim2() + ")");
                }

                if (!items.getStokResim().isEmpty()) {
                    String path = getExternalFilesDir("/aysoft") + File.separator + items.getStokResim();
                    File file = new File(path);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path);
                        itemsImage.setImageBitmap(image);
                    } else {
                        itemsImage.setImageResource(R.drawable.items_image);
                    }
                } else if (!items.getStokResim1().isEmpty()) {
                    String path = getExternalFilesDir("/aysoft") + File.separator + items.getStokResim1();
                    File file = new File(path);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path);
                        itemsImage.setImageBitmap(image);
                    } else {
                        itemsImage.setImageResource(R.drawable.items_image);
                    }
                } else if (!items.getStokResim2().isEmpty()) {
                    String path = getExternalFilesDir("/aysoft") + File.separator + items.getStokResim2();
                    File file = new File(path);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path);
                        itemsImage.setImageBitmap(image);
                    } else {
                        itemsImage.setImageResource(R.drawable.items_image);
                    }
                } else if (!items.getStokResim3().isEmpty()) {
                    String path = getExternalFilesDir("/aysoft") + File.separator + items.getStokResim3();
                    File file = new File(path);
                    if (file.exists()) {
                        Bitmap image = BitmapFactory.decodeFile(path);
                        itemsImage.setImageBitmap(image);
                    } else {
                        itemsImage.setImageResource(R.drawable.items_image);
                    }
                } else {
                    itemsImage.setImageResource(R.drawable.items_image);
                }

                if (prclists.size() > 0) {
                    infoFiyatTitle.setVisibility(View.VISIBLE);
                    for (int i = 0; i < prclists.size(); i++) {
                        if (i == 0) {
                            infoFiyatLayout1.setVisibility(View.VISIBLE);
                            infoFiyat1.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", prclists.get(i).getFiyat()) + " " + prclists.get(i).getDovizIsareti() + " (" + prclists.get(i).getBaslangicTarih() + ")");
                            infoFiyatLabel1.setText(prclists.get(i).getFiyatGrubu() + " " + getResources().getString(R.string.items_fiyat1_label));
                        } else if (i == 1) {
                            infoFiyatLayout2.setVisibility(View.VISIBLE);
                            infoFiyat2.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", prclists.get(i).getFiyat()) + " " + prclists.get(i).getDovizIsareti() + " (" + prclists.get(i).getBaslangicTarih() + ")");
                            infoFiyatLabel2.setText(prclists.get(i).getFiyatGrubu() + " " + getResources().getString(R.string.items_fiyat1_label));
                        } else if (i == 2) {
                            infoFiyatLayout3.setVisibility(View.VISIBLE);
                            infoFiyat3.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", prclists.get(i).getFiyat()) + " " + prclists.get(i).getDovizIsareti() + " (" + prclists.get(i).getBaslangicTarih() + ")");
                            infoFiyatLabel3.setText(prclists.get(i).getFiyatGrubu() + " " + getResources().getString(R.string.items_fiyat1_label));
                        } else if (i == 3) {
                            infoFiyatLayout4.setVisibility(View.VISIBLE);
                            infoFiyat3.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", prclists.get(i).getFiyat()) + " " + prclists.get(i).getDovizIsareti() + " (" + prclists.get(i).getBaslangicTarih() + ")");
                            infoFiyatLabel4.setText(prclists.get(i).getFiyatGrubu() + " " + getResources().getString(R.string.items_fiyat1_label));
                        } else if (i == 4) {
                            infoFiyatLayout5.setVisibility(View.VISIBLE);
                            infoFiyat5.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", prclists.get(i).getFiyat()) + " " + prclists.get(i).getDovizIsareti() + " (" + prclists.get(i).getBaslangicTarih() + ")");
                            infoFiyatLabel5.setText(prclists.get(i).getFiyatGrubu() + " " + getResources().getString(R.string.items_fiyat1_label));
                        }
                    }
                }
            }
            progress_layout.setVisibility(View.GONE);
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