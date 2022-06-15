package kz.burhancakmak.aysoftmobile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Clients.ClientsActivity;
import kz.burhancakmak.aysoftmobile.DataExport.DataExportSiparisActivity;
import kz.burhancakmak.aysoftmobile.DataImport.DataImportActivity;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Login.Spinner_Country;
import kz.burhancakmak.aysoftmobile.Login.Spinner_Country_Adapter;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirma;
import kz.burhancakmak.aysoftmobile.Products.ProductSearchActivity;
import kz.burhancakmak.aysoftmobile.Products.ProductsActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    SessionManagement session;
    HashMap<String, String> hashMap;
    Intent starterIntent;
    private ArrayList<Spinner_Country> spinnerCountryList;
    private Spinner_Country_Adapter spinnerCountryAdapter;
    private static final String KEY_NAME = "name";
    private static final String KEY_LANG = "language";
    public static String FIRMA_NO = null;
    public static String DONEM_NO = null;
    public static String ondegerFiyatGrubu1 = null;
    public static String ondegerFiyatGrubu2 = null;
    public static int menuGrupKayitNo;
    public static long clientMin, clientMax, itemMin, itemMax;
    public static int clientFilterSelected, itemFilterSelected;
    public static String depo1Aciklama1;
    public static String depo2Aciklama1;
    List<CihazlarFirma> firmaList = new ArrayList<>();
    List<String> firmaAlertList = new ArrayList<>();
    List<ClCard> clientList = new ArrayList<>();
    CihazlarFirma firma;
    DatabaseHandler databaseHandler;
    TextView headerFirma, gidilecekToplam, gidilecekAlinan, gidilecekKalan, siparisMiktar, siparisTutar, kasaTahsilat;
    CardView mainProducts, mainClients, mainImport, mainReports;
    final int[] selectedItem = {0};
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManagement(getApplicationContext());
        hashMap = session.getUserDetails();

        if (!(hashMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(hashMap.get(KEY_LANG));
        }

        setContentView(R.layout.activity_main);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            databaseHandler = DatabaseHandler.getInstance(this);
            firmaList = databaseHandler.selectCihazlarFirma();
            initViews();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:

                break;
            case R.id.nav_products:
                startActivityForResult(new Intent(MainActivity.this, ProductsActivity.class), 2);
                break;
            case R.id.nav_clients:
                startActivityForResult(new Intent(MainActivity.this, ClientsActivity.class), 3);
                break;
            case R.id.nav_dataIn:
                startActivityForResult(new Intent(MainActivity.this, DataImportActivity.class), 4);
                break;
            case R.id.nav_dataOut:
                startActivityForResult(new Intent(MainActivity.this, DataExportSiparisActivity.class), 5);
                break;
            case R.id.nav_changeLang:
                alertOnLanguageChange();
                break;
            case R.id.nav_logout:
                alertOnProgramExit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel();
                super.onBackPressed();
            } else {
                backToast = Toast.makeText(this, R.string.main_click_again_to_exit, Toast.LENGTH_SHORT);
                backToast.show();
            }
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAnalytics();
    }

    private void initViews() {
        starterIntent = getIntent();

        spinnerInitList();
        spinnerCountryAdapter = new Spinner_Country_Adapter(this, spinnerCountryList);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainProducts = findViewById(R.id.mainProducts);
        mainClients = findViewById(R.id.mainClients);
        mainImport = findViewById(R.id.mainImport);
        mainReports = findViewById(R.id.mainReports);

        mainProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, ProductsActivity.class), 2);
            }
        });

        mainClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, ClientsActivity.class), 3);
            }
        });

        mainImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, DataImportActivity.class), 4);
            }
        });

        mainReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, ProductSearchActivity.class), 1);
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        TextView headerText = navigationView.getHeaderView(0).findViewById(R.id.menu_nav_header_username);
        headerText.setText(hashMap.get(KEY_NAME));
        headerFirma = navigationView.getHeaderView(0).findViewById(R.id.menu_nav_header_firma);

        if (firmaList.size() == 1) {
            firma = firmaList.get(0);
            FIRMA_NO = firma.getFirmaNo();
            headerFirma.setText(firmaList.get(0).getFirmaNo() + " - " + firmaList.get(0).getFirmaAdi1());
            setDefaultValues();
            getAnalytics();
        } else {
            for (int i = 0; i < firmaList.size(); i++) {
                firmaAlertList.add(firmaList.get(i).getFirmaNo() + "");
            }
            if (session.getKeyFirmaNo().isEmpty()) {
                firmaSelect();
            } else {
                FIRMA_NO = session.getKeyFirmaNo();
                setDefaultValues();
                for (int i = 0; i < firmaList.size(); i++) {
                    if (firmaList.get(i).getFirmaNo().equals(FIRMA_NO)) {
                        headerFirma.setText(firmaList.get(i).getFirmaNo() + " - " + firmaList.get(i).getFirmaAdi1());
                    }
                }
            }
            getAnalytics();
        }

        headerFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firmaList.size() > 1) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        firmaSelect();
                    }
                }
            }
        });
    }

    private void setDefaultValues() {
        for (int i = 0; i < firmaList.size(); i++) {
            if (FIRMA_NO.equals(firmaList.get(i).getFirmaNo())) {
                firma = firmaList.get(i);
                break;
            }
        }
        if (firma != null) {
            ondegerFiyatGrubu1 = firma.getOndegerFiyatGrubu1();
            ondegerFiyatGrubu2 = firma.getOndegerFiyatGrubu2();
            menuGrupKayitNo = firma.getMenuGrupKayitNo();
            DONEM_NO = firma.getDonemNo();

            switch (hashMap.get(KEY_LANG)) {
                case "Türkçe":
                    depo1Aciklama1 = firma.getDepo1Aciklama1();
                    depo2Aciklama1 = firma.getDepo2Aciklama1();
                    break;
                case "Русский":
                    depo1Aciklama1 = firma.getDepo1Aciklama2();
                    depo2Aciklama1 = firma.getDepo2Aciklama2();
                    break;
                case "English":
                    depo1Aciklama1 = firma.getDepo1Aciklama1();
                    depo2Aciklama1 = firma.getDepo2Aciklama1();
                    break;
                default:
                    depo1Aciklama1 = firma.getDepo1Aciklama1();
                    depo2Aciklama1 = firma.getDepo2Aciklama1();
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

    private void alertOnProgramExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.alert_exit_title));
        builder.setIcon(R.drawable.dialog_exit);
        builder.setMessage(getString(R.string.alert_exit_message));
        builder.setCancelable(false);

        builder.setPositiveButton(getString(R.string.alert_exit_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                session.logoutUserDetails();
                session.removeKeyFirmaNo();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.alert_exit_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void alertOnLanguageChange() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.alert_change_language_title));
        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.alert_change_language_layout, null);
        builder.setView(layout);
        Spinner spinner = layout.findViewById(R.id.alert_change_spinner);
        spinner.setAdapter(spinnerCountryAdapter);
        String countryCode;

        switch (Locale.getDefault().getLanguage()) {
            case "tr":
                countryCode = "Türkçe";
                break;
            case "ru":
                countryCode = "Русский";
                break;
            case "en":
                countryCode = "English";
                break;
            default:
                countryCode = "Türkçe";
        }
        spinner.setSelection(spinnerGetIndex(countryCode));

        builder.setPositiveButton(getString(R.string.alert_change_language_button_apply), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                session.changeLanguage(spinnerCountryList.get(spinner.getSelectedItemPosition()).getCountryName());
                setPhoneDefaultLanguage(spinnerCountryList.get(spinner.getSelectedItemPosition()).getCountryName());
                finish();
                startActivity(starterIntent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void spinnerInitList() {
        spinnerCountryList = new ArrayList<>();
        spinnerCountryList.add(new Spinner_Country(getString(R.string.login_lang_tr), R.drawable.flag_tr));
        spinnerCountryList.add(new Spinner_Country(getString(R.string.login_lang_ru), R.drawable.flag_ru));
        spinnerCountryList.add(new Spinner_Country(getString(R.string.login_lang_us), R.drawable.flag_us));
    }

    private int spinnerGetIndex(String myString) {
        int index = 0;
        for (int i = 0; i < spinnerCountryList.size(); i++) {
            String country = spinnerCountryList.get(i).getCountryName();
            if (country.equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    private void alertOnFirmSelect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.alert_change_firm_layout, null);
        builder.setTitle(getString(R.string.alert_select_firm_title));
        builder.setCancelable(false);
        builder.setView(layout);
        Spinner spinner = layout.findViewById(R.id.alert_firm_spinner);
        ArrayAdapter<String> firmaAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_layout, firmaAlertList);
        firmaAdapter.setDropDownViewResource(R.layout.spinner_dropdownitem_layout);
        spinner.setAdapter(firmaAdapter);

        builder.setPositiveButton(getString(R.string.alert_confirm_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FIRMA_NO = spinner.getSelectedItem().toString();
                setDefaultValues();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void firmaSelect() {
        String[] firms = new String[firmaList.size()];
        for (int i = 0; i < firmaList.size(); i++) {
            firms[i] = firmaList.get(i).getFirmaNo() + " - " + firmaList.get(i).getFirmaAdi1();
            if (firmaList.get(i).getFirmaNo().equals(FIRMA_NO)) {
                selectedItem[0] = i;
            }
        }
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(getString(R.string.alert_select_firm_title));
        builder.setIcon(R.drawable.ic_menu);
        builder.setCancelable(false);
        builder.setSingleChoiceItems(firms, selectedItem[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItem[0] = which;
            }
        });
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FIRMA_NO = firmaList.get(selectedItem[0]).getFirmaNo();
                session.createFirmaNo(FIRMA_NO);
                headerFirma.setText(firmaList.get(selectedItem[0]).getFirmaNo() + " - " + firmaList.get(selectedItem[0]).getFirmaAdi1());
                setDefaultValues();
                getAnalytics();
            }
        });
        builder.show();
    }

    private void getAnalytics() {
        if (FIRMA_NO != null) {
            if (clientList.size() > 0) clientList.clear();
            clientList = databaseHandler.selectAllClientsMain();

            gidilecekToplam = findViewById(R.id.gidilecekToplam);
            gidilecekAlinan = findViewById(R.id.gidilecekAlinan);
            gidilecekKalan = findViewById(R.id.gidilecekKalan);
            siparisMiktar = findViewById(R.id.siparisMiktar);
            siparisTutar = findViewById(R.id.siparisTutar);
            kasaTahsilat = findViewById(R.id.kasaTahsilat);

            if (clientList.size() == 0) {
                gidilecekToplam.setText("0");
                gidilecekAlinan.setText("0");
                gidilecekKalan.setText("0");
                siparisMiktar.setText("0");
                siparisTutar.setText("0");
                kasaTahsilat.setText("0");
            } else {
                double gidToplam = 0;
                double gidAlinan = 0;
                double sipToplam = 0;
                double sipMiktar = 0;
                double tahsilat = 0;

                String currentDay = getCurrentDay();
                for (ClCard client : clientList) {
                    switch (currentDay) {
                        case "Sunday":
                            if (client.getPazar().equals("1")) {
                                gidToplam += 1;
                                gidAlinan += client.getSiparisMiktar() != null ? client.getSiparisMiktar() : 0;
                            }
                            break;
                        case "Monday":
                            if (client.getPazartesi().equals("1")) {
                                gidToplam += 1;
                                gidAlinan += client.getSiparisMiktar() != null ? client.getSiparisMiktar() : 0;
                            }
                            break;
                        case "Tuesday":
                            if (client.getSali().equals("1")) {
                                gidToplam += 1;
                                gidAlinan += client.getSiparisMiktar() != null ? client.getSiparisMiktar() : 0;
                            }
                            break;
                        case "Wednesday":
                            if (client.getCarsamba().equals("1")) {
                                gidToplam += 1;
                                gidAlinan += client.getSiparisMiktar() != null ? client.getSiparisMiktar() : 0;
                            }
                            break;
                        case "Thursday":
                            if (client.getPersembe().equals("1")) {
                                gidToplam += 1;
                                gidAlinan += client.getSiparisMiktar() != null ? client.getSiparisMiktar() : 0;
                            }
                            break;
                        case "Friday":
                            if (client.getCuma().equals("1")) {
                                gidToplam += 1;
                                gidAlinan += client.getSiparisMiktar() != null ? client.getSiparisMiktar() : 0;
                            }
                            break;
                        case "Saturday":
                            if (client.getCumartesi().equals("1")) {
                                gidToplam += 1;
                                gidAlinan += client.getSiparisMiktar() != null ? client.getSiparisMiktar() : 0;
                            }
                            break;
                    }
                    sipMiktar += client.getSiparisMiktar() != null ? client.getSiparisMiktar() : 0;
                    sipToplam += client.getSiparisTutar() != null ? client.getSiparisTutar() : 0;
                    tahsilat += client.getKasaTutar() != null ? client.getKasaTutar() : 0;
                }
                gidilecekToplam.setText(String.format(Locale.getDefault(), "%,.0f", gidToplam));
                gidilecekAlinan.setText(String.format(Locale.getDefault(), "%,.0f", gidAlinan));
                gidilecekKalan.setText(String.format(Locale.getDefault(), "%,.0f", gidToplam - gidAlinan));
                siparisMiktar.setText(String.format(Locale.getDefault(), "%,.0f", sipMiktar));
                siparisTutar.setText(String.format(Locale.getDefault(), "%,.0f", sipToplam));
                kasaTahsilat.setText(String.format(Locale.getDefault(), "%,.0f", tahsilat));
            }
        }
    }

    public static String getCurrentDay() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        Calendar calendar = Calendar.getInstance();
        return dayFormat.format(calendar.getTime());

    }

}