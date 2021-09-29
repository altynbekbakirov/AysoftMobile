package kz.burhancakmak.aysoftmobile.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.MainActivity;
import kz.burhancakmak.aysoftmobile.Models.Clients.CihazlarFirmaDepolar;
import kz.burhancakmak.aysoftmobile.Models.Clients.CihazlarFirmaOdemeSekli;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirma;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarMenu;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarQuery;
import kz.burhancakmak.aysoftmobile.Models.Firms.Doviz;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import kz.burhancakmak.aysoftmobile.Settings.SettingsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private TextInputEditText username_Text, password_text;
    private ArrayList<Spinner_Country> spinnerCountryList;
    private Spinner spinnerCountries;
    private CheckBox checkBoxRemember;
    HashMap<String, String> userSettingMap;
    private static final int PERMISSIONS_REQUEST_ALL = 101;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private boolean phonePermissionGranted;
    private String firmaNo;
    private String resimAdresi;
    SessionManagement session;
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(this, Locale.getDefault().getLanguage());
        setContentView(R.layout.activity_login);

        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        getPhonePermission();

        username_Text = findViewById(R.id.login_username);
        password_text = findViewById(R.id.login_password);
        spinnerCountries = findViewById(R.id.login_country);
        Button btnLogin = findViewById(R.id.login_button_enter);
        ImageButton btnSettings = findViewById(R.id.login_button_settings);
        checkBoxRemember = findViewById(R.id.login_remember);

        username_Text.setText(userSettingMap.get(KEY_NAME));
        password_text.setText(userSettingMap.get(KEY_PASSWORD));

        spinnerInitList();
        getPhoneDefaultLanguage();
        databaseHandler = DatabaseHandler.getInstance(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected(LoginActivity.this)) {
                    showInternetDialogConnection();
                    return;
                }
                if (!login_validate_name()) {
                    return;
                }
                if (!login_validate_password()) {
                    return;
                }
                loginCheck();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phonePermissionGranted) {
                    startActivityForResult(new Intent(LoginActivity.this, SettingsActivity.class), 1);
                } else {
                    getPhonePermission();
                }
            }
        });
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void getPhonePermission() {
        String[] permissions = {
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        };

        if (hasPermissions(this, permissions)) {
            phonePermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_ALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        phonePermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ALL) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                phonePermissionGranted = true;
            }
        }

    }

    private void spinnerInitList() {
        spinnerCountryList = new ArrayList<>();
        spinnerCountryList.add(new Spinner_Country(getString(R.string.login_lang_tr), R.drawable.flag_tr));
        spinnerCountryList.add(new Spinner_Country(getString(R.string.login_lang_ru), R.drawable.flag_ru));
        spinnerCountryList.add(new Spinner_Country(getString(R.string.login_lang_us), R.drawable.flag_us));

        Spinner_Country_Adapter spinnerCountryAdapter = new Spinner_Country_Adapter(this, spinnerCountryList);
        spinnerCountries.setAdapter(spinnerCountryAdapter);

        spinnerCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner_Country clickedCountry = (Spinner_Country) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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

    private void showInternetDialogConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.login_internet_connection_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.login_internet_connection_btnConnect), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.login_internet_connection_btnCancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    private boolean isConnected(LoginActivity loginActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) loginActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }

    private void getPhoneDefaultLanguage() {
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
        spinnerCountries.setSelection(spinnerGetIndex(countryCode));
    }

    private Boolean login_validate_name() {
        String val = username_Text.getEditableText().toString().trim();
        if (val.isEmpty()) {
            username_Text.setError(getString(R.string.login_username_empty_error));
            username_Text.requestFocus();
            return false;
        } else {
            username_Text.setError(null);
            return true;
        }
    }

    private Boolean login_validate_password() {
        String val = password_text.getEditableText().toString().trim();
        if (val.isEmpty()) {
            password_text.setError(getString(R.string.login_password_empty_error));
            password_text.requestFocus();
            return false;
        } else {
            password_text.setError(null);
            return true;
        }
    }

    private void loginCheck() {
        String username = username_Text.getEditableText().toString().trim();
        String password = password_text.getEditableText().toString().trim();
        HashMap<String, String> map = session.getWebSettings();
        String webAddress = map.get("web");
        String phoneId = map.get("uuid");

        /*String webAddress = "http://5.199.138.36:34444/NewMobil/";
        String phoneId = "869780039775405";*/

        if (webAddress == null || webAddress.isEmpty()) {
            Toast.makeText(this, R.string.login_web_url_undefined, Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneId == null || phoneId.isEmpty()) {
            Toast.makeText(this, R.string.login_phoneid_is_not_defined, Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(getString(R.string.login_progress_loading));
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        RetrofitApi retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
        Call<CihazlarQuery> call = retrofitApi.getUser(phoneId, username, password);

        call.enqueue(new Callback<CihazlarQuery>() {

            @Override
            public void onResponse(@NotNull Call<CihazlarQuery> call, @NotNull Response<CihazlarQuery> response) {

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
                                firmaNo = firmalar[3];
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
                                resimAdresi = firmalar[19];
                                firma.setResimAdresi(resimAdresi);
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
                                databaseHandler.createTables(firmaNo);
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

                        if (checkBoxRemember.isChecked()) {
                            session.createLoginSession(username, password, spinnerCountryList.get(spinnerCountries.getSelectedItemPosition()).getCountryName());
                        }
                        pd.dismiss();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    } else {
                        username_Text.setError(getString(R.string.login_validate_error));
                        password_text.setError(getString(R.string.login_validate_error));
                        username_Text.requestFocus();
                        pd.dismiss();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, R.string.login_user_is_not_found, Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }

            @Override
            public void onFailure(Call<CihazlarQuery> call, Throwable t) {
                Toast.makeText(LoginActivity.this, R.string.login_connection_failed, Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });


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
