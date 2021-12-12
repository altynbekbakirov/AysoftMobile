package kz.burhancakmak.aysoftmobile.Clients;

import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.clientFilterSelected;
import static kz.burhancakmak.aysoftmobile.MainActivity.clientMax;
import static kz.burhancakmak.aysoftmobile.MainActivity.menuGrupKayitNo;
import static kz.burhancakmak.aysoftmobile.MainActivity.clientMin;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonSyntaxException;
import com.santalu.maskara.widget.MaskEditText;

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
        alertChooseItemsMenuDialog(position);
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
        if (item.getItemId() == R.id.client_filter) {
            alertFilterDialog();
        }
        if (item.getItemId() == R.id.client_add_client) {
            alertAddNewClient();
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

        if (clientMin == 0) {
            clientMin = 1L;
        }
        if (clientMax == 0) {
            clientMax = 999999999999999L;
        }
        if (clientFilterSelected == 0) {
            new GetDataFromDatabase().execute();
        } else if (clientFilterSelected == 1) {
            new GetDataFromDatabaseBalance().execute(clientMin, clientMax);
        } else if (clientFilterSelected == 2) {
            new GetDataFromDatabaseBalance().execute(-clientMax, -clientMin);
        } else {
            new GetDataFromDatabaseBalanceZero().execute();
        }
    }

    private class GetDataFromDatabase extends AsyncTask<Void, Void, Void> {
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cardList.clear();
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

    private class GetDataFromDatabaseBalanceZero extends AsyncTask<Void, Void, Void> {
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
            cardList.clear();
        }

        @Override
        protected Void doInBackground(Void... items) {
            cardList = databaseHandler.selectAllClientsByBalanceZero(NAV_FILTER, NAV_ORDER);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            clientsAdapter.setCardList(cardList);
            products_progressBar.setVisibility(View.GONE);
        }
    }

    private class GetDataFromDatabaseBalance extends AsyncTask<Long, Void, Void> {
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cardList.clear();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Long... items) {
            cardList = databaseHandler.selectAllClientsByBalance(NAV_FILTER, NAV_ORDER, items[0], items[1]);
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

                        cardList.clear();
                        if (clientFilterSelected == 0) {
                            cardList = databaseHandler.selectAllClients(NAV_FILTER, NAV_ORDER);
                        } else if (clientFilterSelected == 1) {
                            cardList = databaseHandler.selectAllClientsByBalance(NAV_FILTER, NAV_ORDER, clientMin, clientMax);
                        } else if (clientFilterSelected == 2) {
                            cardList = databaseHandler.selectAllClientsByBalance(NAV_FILTER, NAV_ORDER, -clientMax, -clientMin);
                        } else {
                            cardList = databaseHandler.selectAllClientsByBalanceZero(NAV_FILTER, NAV_ORDER);
                        }
                    }
                } else {
                    Toast.makeText(ClientsActivity.this, R.string.client_update_balance_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (IllegalStateException | JsonSyntaxException | IOException e) {
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

    private class UpdateOrderDaysFromWeb extends AsyncTask<Integer, String, Void> {
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);
        RetrofitApi retrofitApi;
        Call<ClientsQuery> queryList;
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
            retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
        }

        @Override
        protected Void doInBackground(Integer... item) {
            queryList = retrofitApi.getClientOrderDays(
                    phoneId,
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD),
                    DONEM_NO,
                    FIRMA_NO,
                    cardList.get(item[0]).getKod(),
                    cardList.get(item[0]).getKayitNo(),
                    item[1],
                    item[2],
                    item[3],
                    item[4],
                    item[5],
                    item[6],
                    item[7],
                    item[8],
                    item[9],
                    item[10],
                    item[11],
                    item[12],
                    item[13],
                    item[14]
            );
            try {
                Response<ClientsQuery> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getHata()) {
                        cardList.get(item[0]).setPazartesi(String.valueOf(item[1]));
                        cardList.get(item[0]).setSali(String.valueOf(item[2]));
                        cardList.get(item[0]).setCarsamba(String.valueOf(item[3]));
                        cardList.get(item[0]).setPersembe(String.valueOf(item[4]));
                        cardList.get(item[0]).setCuma(String.valueOf(item[5]));
                        cardList.get(item[0]).setCumartesi(String.valueOf(item[6]));
                        cardList.get(item[0]).setPazar(String.valueOf(item[7]));
                        cardList.get(item[0]).setPazartesiSiraNo(String.valueOf(item[8]));
                        cardList.get(item[0]).setSaliSiraNo(String.valueOf(item[9]));
                        cardList.get(item[0]).setCarsambaSiraNo(String.valueOf(item[10]));
                        cardList.get(item[0]).setPersembeSiraNo(String.valueOf(item[11]));
                        cardList.get(item[0]).setCumaSiraNo(String.valueOf(item[12]));
                        cardList.get(item[0]).setCumartesiSiraNo(String.valueOf(item[13]));
                        cardList.get(item[0]).setPazartesiSiraNo(String.valueOf(item[14]));
                        databaseHandler.updateClientOrderDays(cardList.get(item[0]), String.valueOf(cardList.get(item[0]).getKayitNo()));
                        isSuccess = true;
                    } else {
                        isSuccess = false;
                    }
                } else {
                    isSuccess = false;
                }
            } catch (IllegalStateException | JsonSyntaxException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            clientsAdapter.setCardList(cardList);
            products_progressBar.setVisibility(View.GONE);
            if (isSuccess) {
                Toast.makeText(ClientsActivity.this, R.string.client_popup_order_update_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ClientsActivity.this, R.string.client_popup_order_update_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AddNewClientFromWeb extends AsyncTask<String, String, Void> {
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);
        RetrofitApi retrofitApi;
        Call<ClientsQuery> queryList;
        boolean isSuccess = false;
        String hata = null;
        Response<ClientsQuery> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
            retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
        }

        @Override
        protected Void doInBackground(String... item) {
            queryList = retrofitApi.addNewClient(
                    phoneId,
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD),
                    DONEM_NO,
                    FIRMA_NO,
                    item[0], // Unvani1
                    item[1], // Adres1
                    item[2], // Yetkili
                    item[3], // telefon1
                    item[4], // telefon2
                    item[5] // email
            );
            try {
                response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getHata()) {
                        ClCard card = new ClCard();
                        int max = 0;
                        for (int i = 0; i < cardList.size(); i++) {
                            if (cardList.get(i).getKayitNo() > max) {
                                max = cardList.get(i).getKayitNo();
                            }
                        }
                        card.setKayitNo(max + 1);
                        card.setKod(response.body().getYeniCariKod());
                        card.setOzelKod1(response.body().getYeniCariKodOzelKod1());
                        card.setOzelKod4(response.body().getYeniCariKodOzelKod4());
                        card.setTicariIslemGrubu(response.body().getYeniCariTicariIslemGrubu());
                        card.setUnvani1(item[0]);
                        card.setAdres1(item[1]);
                        card.setUnvani2(item[2]);
                        card.setTelefon1(item[3]);
                        card.setTelefon2(item[4]);
                        card.setEmailAdresi1(item[5]);
                        card.setKordinatLongitude(0.0);
                        card.setKordinatLatitute(0.0);
                        card.setSiparisBeklemede(0);
                        card.setSiparisGonderildi(0);
                        card.setPazartesi("0");
                        card.setPazartesiSiraNo("0");
                        card.setSali("0");
                        card.setSaliSiraNo("0");
                        card.setCarsamba("0");
                        card.setCarsambaSiraNo("0");
                        card.setPersembe("0");
                        card.setPersembeSiraNo("0");
                        card.setCuma("0");
                        card.setCumaSiraNo("0");
                        card.setCumartesi("0");
                        card.setCumartesiSiraNo("0");
                        card.setPazar("0");
                        card.setPazarSiraNo("0");
                        databaseHandler.insertClients(card);
                        cardList.set(0, card);
                        isSuccess = true;
                    } else {
                        isSuccess = false;
                    }
                } else {
                    isSuccess = false;
                }
            } catch (IllegalStateException | JsonSyntaxException | IOException e) {
                hata = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            products_progressBar.setVisibility(View.GONE);
            if (isSuccess) {
                clientsAdapter.setCardList(cardList);
                Toast.makeText(ClientsActivity.this, R.string.client_popup_order_update_success, Toast.LENGTH_SHORT).show();
            } else {
                sendDataToServerFailDialog(hata);
            }
        }
    }

    private void alertChooseItemsMenuDialog(int position) {
        String[] items = new String[]{
                getString(R.string.client_popup_show_operations),
                getString(R.string.client_popup_info),
                getString(R.string.client_popup_show_map),
                getString(R.string.client_menu_extract),
                getString(R.string.client_popup_analyze),
                getString(R.string.main_nav_reports),
                getString(R.string.client_popup_order_days)};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.alert_kasa_title_choose);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_menu);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (selectedItem[0]) {
                    case 0: // Tasks
                        Intent intent2 = new Intent(ClientsActivity.this, ClientsTasksActivity.class);
                        intent2.putExtra("clientKayitNo", cardList.get(position).getKayitNo());
                        intent2.putExtra("position", position);
                        startActivityForResult(intent2, 305);
                        break;
                    case 1: // Info
                        Intent intent3 = new Intent(ClientsActivity.this, ClientsInfoActivity.class);
                        intent3.putExtra("clientKayitNo", cardList.get(position).getKayitNo());
                        intent3.putExtra("position", position);
                        startActivityForResult(intent3, 405);
                        break;
                    case 2: // Geolocation
                        Intent intent = new Intent(ClientsActivity.this, ClientsMapActivity.class);
                        intent.putExtra("clientKayitNo", cardList.get(position).getKayitNo());
                        startActivityForResult(intent, 105);
                        break;
                    case 3: // Extract
                        Intent intent1 = new Intent(ClientsActivity.this, ClientsExtractActivity.class);
                        intent1.putExtra("clientKayitNo", cardList.get(position).getKayitNo());
                        startActivityForResult(intent1, 205);
                        break;
                    case 4: // Analyse
                        Intent intent4 = new Intent(ClientsActivity.this, ClientsDashboardActivity.class);
                        intent4.putExtra("clientKayitNo", cardList.get(position).getKayitNo());
                        startActivityForResult(intent4, 505);
                        break;
                    case 5: // Reports
                        break;
                    case 6: // Route
                        alertOrderDaysDialog(position);
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

    private void alertFilterDialog() {
        List<String> clientTaskList = new ArrayList<>();
        clientTaskList.add(getString(R.string.client_alert_list_all));
        clientTaskList.add(getString(R.string.client_alert_list_debtors));
        clientTaskList.add(getString(R.string.client_alert_list_creditors));
        clientTaskList.add(getString(R.string.client_alert_list_zero_debtors));

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.client_filter_layout, null);
        builder.setView(view);

        LinearLayout editLayout = view.findViewById(R.id.ll3);
        Spinner spinner1 = view.findViewById(R.id.client_menu_combo);
        EditText editDate1 = view.findViewById(R.id.editDate1);
        EditText editDate2 = view.findViewById(R.id.editDate2);

        if (clientFilterSelected == 0 || clientFilterSelected == 3) {
            editLayout.setVisibility(View.GONE);
        }

        ArrayAdapter<String> clientAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_layout, clientTaskList);
        clientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clientAdapter.notifyDataSetChanged();
        spinner1.setAdapter(clientAdapter);
        spinner1.setSelection(clientFilterSelected);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                clientFilterSelected = i;
                if (clientFilterSelected == 0) {
                    editLayout.setVisibility(View.GONE);
                } else if (clientFilterSelected == 1) {
                    editLayout.setVisibility(View.VISIBLE);
                    editDate1.setText(String.valueOf(clientMin));
                    editDate1.requestFocus();
                    editDate1.setSelectAllOnFocus(true);
                    editDate2.setText(String.valueOf(clientMax));
                } else if (clientFilterSelected == 2) {
                    editLayout.setVisibility(View.VISIBLE);
                    editDate1.setText(String.valueOf(clientMin));
                    editDate1.requestFocus();
                    editDate1.setSelectAllOnFocus(true);
                    editDate2.setText(String.valueOf(clientMax));
                } else {
                    editLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (clientFilterSelected == 0) {
                    new GetDataFromDatabase().execute();
                } else if (clientFilterSelected == 1) {
                    if (!editDate1.getText().toString().isEmpty()) {
                        clientMin = Long.parseLong(editDate1.getText().toString());
                    }
                    if (!editDate2.getText().toString().isEmpty()) {
                        clientMax = Long.parseLong(editDate2.getText().toString());
                    }
                    new GetDataFromDatabaseBalance().execute(clientMin, clientMax);
                } else if (clientFilterSelected == 2) {
                    if (!editDate1.getText().toString().isEmpty()) {
                        clientMin = Long.parseLong(editDate1.getText().toString());
                    }
                    if (!editDate2.getText().toString().isEmpty()) {
                        clientMax = Long.parseLong(editDate2.getText().toString());
                    }
                    new GetDataFromDatabaseBalance().execute(-clientMax, -clientMin);
                } else {
                    new GetDataFromDatabaseBalanceZero().execute();
                }
            }
        });
        builder.show();
    }

    private void alertOrderDaysDialog(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.client_route_layout, null);
        builder.setView(view);

        CheckBox checkMonday = view.findViewById(R.id.checkMonday);
        CheckBox checkTuesday = view.findViewById(R.id.checkTuesday);
        CheckBox checkWednesday = view.findViewById(R.id.checkWednesday);
        CheckBox checkThursday = view.findViewById(R.id.checkThursday);
        CheckBox checkFriday = view.findViewById(R.id.checkFriday);
        CheckBox checkSaturday = view.findViewById(R.id.checkSaturday);
        CheckBox checkSunday = view.findViewById(R.id.checkSunday);

        EditText mondayNo = view.findViewById(R.id.mondayNo);
        EditText tuesdayNo = view.findViewById(R.id.tuesdayNo);
        EditText wednesdayNo = view.findViewById(R.id.wednesdayNo);
        EditText thursdayNo = view.findViewById(R.id.thursdayNo);
        EditText fridayNo = view.findViewById(R.id.fridayNo);
        EditText saturdayNo = view.findViewById(R.id.saturdayNo);
        EditText sundayNo = view.findViewById(R.id.sundayNo);

        checkMonday.setChecked(cardList.get(position).getPazartesi().equals("1"));
        checkTuesday.setChecked(cardList.get(position).getSali().equals("1"));
        checkWednesday.setChecked(cardList.get(position).getCarsamba().equals("1"));
        checkThursday.setChecked(cardList.get(position).getPersembe().equals("1"));
        checkFriday.setChecked(cardList.get(position).getCuma().equals("1"));
        checkSaturday.setChecked(cardList.get(position).getCumartesi().equals("1"));
        checkSunday.setChecked(cardList.get(position).getPazar().equals("1"));

        mondayNo.setText(cardList.get(position).getPazartesiSiraNo());
        tuesdayNo.setText(cardList.get(position).getSaliSiraNo());
        wednesdayNo.setText(cardList.get(position).getCarsambaSiraNo());
        thursdayNo.setText(cardList.get(position).getPersembeSiraNo());
        fridayNo.setText(cardList.get(position).getCumaSiraNo());
        saturdayNo.setText(cardList.get(position).getCumartesiSiraNo());
        sundayNo.setText(cardList.get(position).getPazarSiraNo());

        builder.setPositiveButton(R.string.client_menu_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Integer[] values = new Integer[15];
                values[0] = position;
                values[1] = checkMonday.isChecked() ? 1 : 0;
                values[2] = checkTuesday.isChecked() ? 1 : 0;
                values[3] = checkWednesday.isChecked() ? 1 : 0;
                values[4] = checkThursday.isChecked() ? 1 : 0;
                values[5] = checkFriday.isChecked() ? 1 : 0;
                values[6] = checkSaturday.isChecked() ? 1 : 0;
                values[7] = checkSunday.isChecked() ? 1 : 0;
                values[8] = Integer.valueOf(mondayNo.getText().toString());
                values[9] = Integer.valueOf(tuesdayNo.getText().toString());
                values[10] = Integer.valueOf(wednesdayNo.getText().toString());
                values[11] = Integer.valueOf(thursdayNo.getText().toString());
                values[12] = Integer.valueOf(fridayNo.getText().toString());
                values[13] = Integer.valueOf(saturdayNo.getText().toString());
                values[14] = Integer.valueOf(sundayNo.getText().toString());
                new UpdateOrderDaysFromWeb().execute(values);
            }
        });

        builder.setNegativeButton(R.string.login_internet_connection_btnCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void alertAddNewClient() {
        View view = getLayoutInflater().inflate(R.layout.client_addnew_layout, null);
        final AlertDialog d = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(R.string.client_menu_save, null) //Set to null. We override the onclick
                .setNegativeButton(R.string.login_internet_connection_btnCancel, null)
                .create();

        EditText companyName = view.findViewById(R.id.companyName);
        EditText companyAddress = view.findViewById(R.id.companyAddress);
        EditText clientName = view.findViewById(R.id.clientName);
        EditText clientEmail = view.findViewById(R.id.clientEmail);
        MaskEditText clientPhone1 = view.findViewById(R.id.clientPhone1);
        MaskEditText clientPhone2 = view.findViewById(R.id.clientPhone2);

        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (companyName.getText().toString().trim().isEmpty()) {
                            companyName.setError(getString(R.string.client_popup_empty_error));
                            companyName.requestFocus();
                        } else if (companyAddress.getText().toString().trim().isEmpty()) {
                            companyAddress.setError(getString(R.string.client_popup_empty_error));
                            companyAddress.requestFocus();
                        } else if (clientName.getText().toString().trim().isEmpty()) {
                            clientName.setError(getString(R.string.client_popup_empty_error));
                            clientName.requestFocus();
                        } else if (clientPhone1.getText().toString().isEmpty()) {
                            clientPhone1.setError(getString(R.string.client_popup_empty_error));
                            clientPhone1.requestFocus();
                        } else if (!clientPhone1.isDone()) {
                            clientPhone1.setError(getString(R.string.client_popup_wrong_format));
                            clientPhone1.requestFocus();
                        } else if (!clientPhone2.getText().toString().isEmpty() && !clientPhone2.isDone()) {
                            clientPhone2.setError(getString(R.string.client_popup_wrong_format));
                            clientPhone2.requestFocus();
                        } else if (!TextUtils.isEmpty(clientEmail.getText().toString().trim()) && !Patterns.EMAIL_ADDRESS.matcher(clientEmail.getText().toString().trim()).matches()) {
                            clientEmail.setError(getString(R.string.client_popup_wrong_format));
                            clientEmail.requestFocus();
                        } else {
                            d.dismiss();
                            String[] client = new String[6];
                            client[0] = companyName.getText().toString();
                            client[1] = companyAddress.getText().toString();
                            client[2] = clientName.getText().toString();
                            client[3] = clientPhone1.getText().toString();
                            client[4] = clientPhone2.getText().toString();
                            client[5] = clientEmail.getText().toString();
                            new AddNewClientFromWeb().execute(client);
                        }
                    }
                });
            }
        });
        d.show();
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

