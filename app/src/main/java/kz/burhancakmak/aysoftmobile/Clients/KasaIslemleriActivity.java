package kz.burhancakmak.aysoftmobile.Clients;

import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.R;

public class KasaIslemleriActivity extends AppCompatActivity {

    EditText edtTarih, edtTutar, edtAciklama, edtMakbuzNo;
    SessionManagement session;
    HashMap<String, String> hashMap;
    DatabaseHandler databaseHandler;
    private static final String KEY_LANG = "language";
    int islemTuru, kayitNo, position, incele;
    String makbuzNo, aciklama, tarih;
    double tutar;
    String KurusHaneSayisiStokTutar;
    ClCard card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManagement(getApplicationContext());
        hashMap = session.getUserDetails();
        if (!(hashMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(hashMap.get(KEY_LANG));
        }
        setContentView(R.layout.activity_kasa_islemleri);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            databaseHandler = DatabaseHandler.getInstance(this);
            KurusHaneSayisiStokTutar = parametreGetir(FIRMA_NO, "KurusHaneSayisiStokTutar", "0");
            initViews();
        }
    }

    private void initViews() {
        Intent intent = getIntent();
        incele = intent.getIntExtra("incele", -1);
        islemTuru = intent.getIntExtra("islemTuru", -1);
        position = intent.getIntExtra("position", -1);
        kayitNo = intent.getIntExtra("kayitNo", -1);
        makbuzNo = intent.getStringExtra("makbuzNo");
        aciklama = intent.getStringExtra("aciklama");
        tarih = intent.getStringExtra("tarih");
        tutar = intent.getDoubleExtra("tutar", -1);

        card = databaseHandler.selectClientById(kayitNo);

        String toolbarTitle;
        if (islemTuru == 1) {
            toolbarTitle = getString(R.string.alert_kasa_payment);
        } else if (islemTuru == 0) {
            toolbarTitle = getString(R.string.alert_kasa_collection);
        } else {
            toolbarTitle = card.getUnvani1();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(toolbarTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        edtTarih = findViewById(R.id.edtTarih);
        edtTutar = findViewById(R.id.edtTutar);
        edtAciklama = findViewById(R.id.edtAciklama);
        edtMakbuzNo = findViewById(R.id.edtMakbuzNo);
        edtTutar.requestFocus();

        if (kayitNo != -1) {
            edtTarih.setText(tarih);
            edtAciklama.setText(aciklama);
            edtMakbuzNo.setText(makbuzNo);
            if (tutar < 0) {
                islemTuru = 1;
                tutar *= -1;
                edtTutar.setText(String.format("%,." + KurusHaneSayisiStokTutar + "f", tutar));
            } else {
                islemTuru = 0;
                edtTutar.setText(String.format("%,." + KurusHaneSayisiStokTutar + "f", tutar));
            }
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = new Date();
            edtTarih.setText(dateFormat.format(date1));
        }

        if (incele == 1) {
            edtAciklama.setEnabled(false);
            edtMakbuzNo.setEnabled(false);
            edtTarih.setEnabled(false);
            edtTutar.setEnabled(false);
        }

        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                edtTarih.setText(sdf.format(myCalendar.getTime()));
            }
        };
        edtTarih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(KasaIslemleriActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (incele != 1) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.clients_maps_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.client_maps_save) {
            if (edtTutar.getText().toString().isEmpty() | edtAciklama.getText().toString().isEmpty()) {
                if (edtTutar.getText().toString().isEmpty()) {
                    edtTutar.setError(getString(R.string.client_kasa_empty_value));
                }
                if (edtAciklama.getText().toString().isEmpty()) {
                    edtAciklama.setError(getString(R.string.client_kasa_empty_value));
                }
            } else {
                double tutar = Double.parseDouble(edtTutar.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("kayitNo", kayitNo);
                intent.putExtra("position", position);
                intent.putExtra("tarih", edtTarih.getText().toString());
                intent.putExtra("tutar", tutar);
                intent.putExtra("islemTuru", islemTuru);
                intent.putExtra("aciklama", edtAciklama.getText().toString());
                intent.putExtra("makbuzNo", edtMakbuzNo.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            finish();
        }
        return super.onKeyDown(keyCode, event);
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