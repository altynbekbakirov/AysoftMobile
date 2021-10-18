package kz.burhancakmak.aysoftmobile.Clients;

import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.menuGrupKayitNo;

import android.app.Activity;
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
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Adapters.ClientsAdapter;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsMap;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsQuery;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarMenu;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ClientsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ClientsAdapter.ClientClickListener {

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    SessionManagement session;
    HashMap<String, String> webSettingsMap;
    HashMap<String, String> userSettingMap;
    DatabaseHandler databaseHandler;
    RecyclerView recyclerView;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    private static String NAV_FILTER;
    private static String NAV_ORDER;
    List<CihazlarMenu> menuList = new ArrayList<>();
    List<ClCard> cardList = new ArrayList<>();
    ClientsAdapter clientsAdapter;
    final int[] selectedItem = {0};
    String kurusHaneSayisiStok;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        webSettingsMap = session.getWebSettings();

        if (!(userSettingMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(userSettingMap.get(KEY_LANG));
        }

        setContentView(R.layout.activity_clients);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            databaseHandler = DatabaseHandler.getInstance(this);
            kurusHaneSayisiStok = parametreGetir(FIRMA_NO, "KurusHaneSayisiCari", "0");
            initViews();
        }

    }

    @Override
    public void onItemClick(int position) {
        chooseMenuOptions(position);
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.main_nav_clients);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.products_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        clientsAdapter = new ClientsAdapter(this, cardList, this, Integer.parseInt(kurusHaneSayisiStok));
        recyclerView.setAdapter(clientsAdapter);

        navigationView = findViewById(R.id.products_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().clear();

        menuList = databaseHandler.selectCihazlarMenu(2, menuGrupKayitNo);
        if (menuList.size() > 0) {
            for (int i = 0; i < menuList.size(); i++) {
                CihazlarMenu menu = menuList.get(i);
                int sayi = databaseHandler.selectCihazlarCariSayisi(menu.getFiltre());
                navigationView.getMenu().add(i, menu.getKayitNo(), i, menu.getAciklama1() + " (" + sayi + ")").setIcon(R.drawable.menu_ic_reports);
                if (menu.getOndeger() == 1) {
                    NAV_FILTER = menu.getFiltre();
                    NAV_ORDER = menu.getSiralama();
                }
            }
            navigationView.getMenu().getItem(0).setChecked(true);
        }

        new GetDataFromDatabase().execute();
    }

    private void chooseMenuOptions(int position) {
        String[] items = new String[]{
                getString(R.string.client_popup_show_operations),
                getString(R.string.client_popup_info),
                getString(R.string.client_popup_show_map),
                getString(R.string.client_menu_extract),
                getString(R.string.client_popup_analyze),
                getString(R.string.main_nav_reports)};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.alert_kasa_title_choose);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_menu);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (selectedItem[0]) {
                    case 0:
                        Intent intent2 = new Intent(ClientsActivity.this, ClientsTasksActivity.class);
                        intent2.putExtra("clientKayitNo", cardList.get(position).getKayitNo());
                        intent2.putExtra("position", position);
                        startActivityForResult(intent2, 305);
                        break;
                    case 1:
                        Intent intent3 = new Intent(ClientsActivity.this, ClientsInfoActivity.class);
                        intent3.putExtra("clientKayitNo", cardList.get(position).getKayitNo());
                        intent3.putExtra("position", position);
                        startActivityForResult(intent3, 405);
                        break;
                    case 2:
                        Intent intent = new Intent(ClientsActivity.this, ClientsMapActivity.class);
                        intent.putExtra("clientKayitNo", cardList.get(position).getKayitNo());
                        startActivityForResult(intent, 105);
                        break;
                    case 3:
                        Intent intent1 = new Intent(ClientsActivity.this, ClientsExtractActivity.class);
                        intent1.putExtra("clientKayitNo", cardList.get(position).getKayitNo());
                        startActivityForResult(intent1, 205);
                        break;
                    case 4:
                        Intent intent4 = new Intent(ClientsActivity.this, ClientsDashboardActivity.class);
                        intent4.putExtra("clientKayitNo", cardList.get(position).getKayitNo());
                        startActivityForResult(intent4, 505);
                        break;
                    case 5:
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.login_internet_connection_btnCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setSingleChoiceItems(items, selectedItem[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItem[0] = which;
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 105) {
            double longitude = data.getDoubleExtra("longitude", -1);
            double latitude = data.getDoubleExtra("latitude", -1);
            String clientKod = data.getStringExtra("clientKod");
            int clientKayitNo = data.getIntExtra("clientKayitNo", -1);
            if (longitude != -1 && latitude != -1) {
                String webAddress = webSettingsMap.get("web");
                String phoneId = webSettingsMap.get("uuid");
                String login = userSettingMap.get(KEY_NAME);
                String password = userSettingMap.get(KEY_PASSWORD);
                RetrofitApi retrofitApi;
                retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
                Call<ClientsMap> query = retrofitApi.updateMapLocation(
                        phoneId,
                        login,
                        password,
                        FIRMA_NO,
                        DONEM_NO,
                        clientKod,
                        "KordinatLongitude|KordinatLatitute",
                        longitude + "|" + latitude,
                        clientKayitNo
                );
                query.enqueue(new Callback<ClientsMap>() {
                    @Override
                    public void onResponse(Call<ClientsMap> call, Response<ClientsMap> response) {
                        if (response.isSuccessful()) {
                            ClientsMap clientsMap = response.body();
                            if (!clientsMap.getHata()) {
                                databaseHandler.updateClientLocation(
                                        String.valueOf(latitude),
                                        String.valueOf(longitude),
                                        String.valueOf(clientKayitNo));
                                for (int i = 0; i < cardList.size(); i++) {
                                    if (cardList.get(i).getKod().equals(clientKod) && cardList.get(i).getKayitNo() == clientKayitNo) {
                                        cardList.get(i).setKordinatLatitute(latitude);
                                        cardList.get(i).setKordinatLongitude(longitude);
                                        clientsAdapter.notifyItemChanged(i);
                                        break;
                                    }
                                }
                                Toast.makeText(getApplicationContext(), R.string.client_map_save_status_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.client_map_save_status_failure, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ClientsMap> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), R.string.client_map_save_status_failure, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        if (resultCode == RESULT_OK && requestCode == 305) {
            int position = data.getIntExtra("position", -1);
            if (position != -1) {
                ClCard card = databaseHandler.selectClientById(cardList.get(position).getKayitNo());
                cardList.get(position).setSiparisGonderildi(card.getSiparisGonderildi());
                cardList.get(position).setSiparisBeklemede(card.getSiparisBeklemede());
                clientsAdapter.notifyItemChanged(position);
            }

        }
    }

    class GetDataFromDatabase extends AsyncTask<Void, Void, Void> {
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... items) {
            cardList = databaseHandler.selectAllClients(NAV_FILTER, NAV_ORDER);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            clientsAdapter.setCardList(cardList);
            products_progressBar.setVisibility(View.GONE);
        }
    }

    private class UpdateBalanceFromWeb extends AsyncTask<Void, String, Void> {
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);
        RetrofitApi retrofitApi;
        Call<ClientsQuery> queryList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
            retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
            queryList = retrofitApi.getClientListBalance(
                    phoneId,
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD),
                    DONEM_NO,
                    FIRMA_NO,
                    2);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Response<ClientsQuery> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    ClientsQuery clientsQuery = response.body();
                    if (clientsQuery.getClCard().size() > 0) {
                        for (int i = 2; i < clientsQuery.getClCard().size(); i++) {
                            String[] clients = clientsQuery.getClCard().get(i).split("\\|");
                            ClCard clCard = new ClCard();
                            clCard.setKayitNo(Integer.parseInt(clients[0]));
                            clCard.setKod(clients[1]);
                            clCard.setNetSatis(Double.parseDouble(clients[2]));
                            clCard.setNetTahsilat(Double.parseDouble(clients[3]));
                            clCard.setBakiye(Double.parseDouble(clients[4]));
                            databaseHandler.updateClientBalance(clCard, String.valueOf(clCard.getKayitNo()));
                        }
                        cardList = databaseHandler.selectAllClients(NAV_FILTER, NAV_ORDER);
                    }
                } else {
                    Toast.makeText(ClientsActivity.this, R.string.client_update_balance_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            clientsAdapter.setCardList(cardList);
            products_progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clients_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.client_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                clientsAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.client_balance_update) {
            new UpdateBalanceFromWeb().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        int index = 0;
        for (int i = 0; i < menuList.size(); i++) {
            if (menuList.get(i).getKayitNo() == item.getItemId()) {
                index = i;
                NAV_FILTER = menuList.get(i).getFiltre();
                NAV_ORDER = menuList.get(i).getSiralama();
                break;
            }
        }
        for (int i = 0; i < menuList.size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        navigationView.getMenu().getItem(index).setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        new GetDataFromDatabase().execute();
        return true;
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
