package kz.burhancakmak.aysoftmobile.Products;

import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Adapters.ClientsExtractAdapter;
import kz.burhancakmak.aysoftmobile.Adapters.ItemsExtractAdapter;
import kz.burhancakmak.aysoftmobile.Clients.ClientsExtractActivity;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientExtract;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsExtract;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsExtractQuery;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;

public class ProductsExtractActivity extends AppCompatActivity {
    SessionManagement session;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    DatabaseHandler databaseHandler;
    RecyclerView recyclerView;
    private String date1, date2;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    List<ItemsExtract> itemsExtractList = new ArrayList<>();
    ItemsExtractAdapter adapter;
    Calendar myCalendar;
    String kurusHaneSayisiStokMiktar, kurusHaneSayisiStokTutar, kayitKod, kayitAciklama;
    Toolbar toolbar;
    int kayitNo;
    TextView bottomGiren, bottomCikan, bottomKalan, bottomTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        webSettingsMap = session.getWebSettings();

        if (!(userSettingMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(userSettingMap.get(KEY_LANG));
        }

        setContentView(R.layout.activity_products_extract);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            initViews();
            new GetDataFromWeb().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clients_extract_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.client_extract_refresh) {
            new GetDataFromWeb().execute();
        }
        if (item.getItemId() == R.id.client_extract_filter) {
            showFilterDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        databaseHandler = DatabaseHandler.getInstance(this);

        if (date1 == null) {
            date1 = convertDateFormat(parametreGetir(FIRMA_NO, "FirmaDonemBaslangicTarihi", "02.02.2021"));
        }
        if (date2 == null) {
            date2 = convertDateFormat(parametreGetir(FIRMA_NO, "FirmaDonemBitisTarihi", "31.12.2021"));
        }

        Intent intent = getIntent();
        kayitNo = intent.getIntExtra("kayitNo", -1);
        kayitKod = intent.getStringExtra("kayitKod");
        kayitAciklama = intent.getStringExtra("kayitAciklama");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(kayitKod + " - " + kayitAciklama);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (kayitNo != -1) {
            kurusHaneSayisiStokMiktar = parametreGetir(FIRMA_NO, "kurusHaneSayisiStokMiktar", "0");
            kurusHaneSayisiStokTutar = parametreGetir(FIRMA_NO, "kurusHaneSayisiStokTutar", "0");
        }

        bottomKalan = findViewById(R.id.bottomKalan);
        bottomCikan = findViewById(R.id.bottomCikan);
        bottomGiren = findViewById(R.id.bottomGiren);
        bottomTitle = findViewById(R.id.bottomTitle);

        myCalendar = Calendar.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProductsExtractActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(1);
        adapter = new ItemsExtractAdapter(itemsExtractList, Integer.parseInt(kurusHaneSayisiStokMiktar), Integer.parseInt(kurusHaneSayisiStokTutar));
        recyclerView.setAdapter(adapter);
    }

    private class GetDataFromWeb extends AsyncTask<Void, String, Void> {
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        String login = userSettingMap.get(KEY_NAME);
        String password = userSettingMap.get(KEY_PASSWORD);
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);
        RetrofitApi retrofitApi;
        Call<ItemsExtractQuery> queryList;
        Double girenMiktar = 0.0, cikanMiktar = 0.0, kalanMiktar = 0.0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            itemsExtractList.clear();
            products_progressBar.setVisibility(View.VISIBLE);
            retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
            queryList = retrofitApi.getStockExtractList(
                    phoneId,
                    login,
                    password,
                    FIRMA_NO,
                    DONEM_NO,
                    "Tr",
                    date1,
                    date2,
                    0,
                    kayitKod,
                    kayitNo
            );
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Response<ItemsExtractQuery> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    ItemsExtractQuery query = response.body();

                    if (query.getStokekstre().size() > 2) {
                        publishProgress(getString(R.string.data_import_progressbar_clients));
                        for (int i = 2; i < query.getStokekstre().size(); i++) {
                            String[] items = query.getStokekstre().get(i).split("\\|");
                            ItemsExtract itemsExtract = new ItemsExtract();
                            itemsExtract.setStoKKodu(items[0]);
                            itemsExtract.setAciklama(items[1]);
                            itemsExtract.setTarih(items[2]);
                            itemsExtract.setIslemNo(items[3]);
                            itemsExtract.setMusteri(items[4]);
                            itemsExtract.setIslemTipi(items[5]);
                            itemsExtract.setAnaBirim(items[6]);
                            itemsExtract.setGirenMiktar(Double.parseDouble(items[7]));
                            itemsExtract.setCikanMiktar(Double.parseDouble(items[8]));
                            itemsExtract.setKalanMiktar(Double.parseDouble(items[9]));
                            itemsExtract.setFiyat(Double.parseDouble(items[10]));
                            itemsExtract.setToplam(Double.parseDouble(items[11]));
                            itemsExtract.setNetIskonto(Double.parseDouble(items[12]));
                            itemsExtract.setNetMasraf(Double.parseDouble(items[13]));
                            itemsExtract.setNetToplam(Double.parseDouble(items[14]));
                            itemsExtractList.add(itemsExtract);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.setList(itemsExtractList);
            products_progressBar.setVisibility(View.GONE);
            if (itemsExtractList.size() > 0) {
                for (int i = 0; i < itemsExtractList.size(); i++) {
                    if (itemsExtractList.get(i).getGirenMiktar() != null) {
                        girenMiktar += itemsExtractList.get(i).getGirenMiktar();
                    }
                    if (itemsExtractList.get(i).getCikanMiktar() != null) {
                        cikanMiktar += itemsExtractList.get(i).getCikanMiktar();
                    }
                    if (itemsExtractList.get(i).getKalanMiktar() != null) {
                        kalanMiktar += itemsExtractList.get(i).getKalanMiktar();
                    }
                }
                bottomGiren.setText(String.format("%,." + Integer.parseInt(kurusHaneSayisiStokMiktar) + "f", girenMiktar));
                bottomCikan.setText(String.format("%,." + Integer.parseInt(kurusHaneSayisiStokMiktar) + "f", cikanMiktar));
                bottomKalan.setText(String.format("%,." + Integer.parseInt(kurusHaneSayisiStokMiktar) + "f", girenMiktar - cikanMiktar));
                bottomTitle.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showFilterDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setCancelable(true);
        View view = getLayoutInflater().inflate(R.layout.client_extract_filter_layout, null);
        builder.setView(view);

        EditText editDate1 = view.findViewById(R.id.editDate1);
        editDate1.setText(date1);
        EditText editDate2 = view.findViewById(R.id.editDate2);
        editDate2.setText(date2);

        DatePickerDialog.OnDateSetListener datePicker1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                editDate1.setText(sdf.format(myCalendar.getTime()));
                date1 = sdf.format(myCalendar.getTime());
            }
        };
        DatePickerDialog.OnDateSetListener datePicker2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                editDate2.setText(sdf.format(myCalendar.getTime()));
                date2 = sdf.format(myCalendar.getTime());
            }
        };
        editDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] date1Array = date1.split("-");
                new DatePickerDialog(ProductsExtractActivity.this, datePicker1,
                        Integer.parseInt(date1Array[0]),
                        Integer.parseInt(date1Array[1]) - 1,
                        Integer.parseInt(date1Array[2])).show();
            }
        });
        editDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] date1Array = date2.split("-");
                new DatePickerDialog(ProductsExtractActivity.this, datePicker2,
                        Integer.parseInt(date1Array[0]),
                        Integer.parseInt(date1Array[1]) - 1,
                        Integer.parseInt(date1Array[2])).show();
            }
        });
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new GetDataFromWeb().execute();
            }
        });
        builder.show();
    }

    /*private void showFilterDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setCancelable(true);
        View view = getLayoutInflater().inflate(R.layout.client_extract_filter_layout, null);
        builder.setView(view);

        EditText editDate1 = view.findViewById(R.id.editDate1);
        editDate1.setText(date1);
        EditText editDate2 = view.findViewById(R.id.editDate2);
        editDate2.setText(date2);

        DatePickerDialog.OnDateSetListener datePicker1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                editDate1.setText(sdf.format(myCalendar.getTime()));
                date1 = sdf.format(myCalendar.getTime());
            }
        };
        DatePickerDialog.OnDateSetListener datePicker2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                editDate2.setText(sdf.format(myCalendar.getTime()));
                date2 = sdf.format(myCalendar.getTime());
            }
        };
        editDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] date1Array = date1.split("-");
                new DatePickerDialog(ClientsExtractActivity.this, datePicker1,
                        Integer.parseInt(date1Array[0]),
                        Integer.parseInt(date1Array[1]) - 1,
                        Integer.parseInt(date1Array[2])).show();
            }
        });
        editDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] date1Array = date2.split("-");
                new DatePickerDialog(ClientsExtractActivity.this, datePicker2,
                        Integer.parseInt(date1Array[0]),
                        Integer.parseInt(date1Array[1]) - 1,
                        Integer.parseInt(date1Array[2])).show();
            }
        });
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new ClientsExtractActivity.GetDataFromWeb().execute();
            }
        });
        builder.show();
    }*/

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