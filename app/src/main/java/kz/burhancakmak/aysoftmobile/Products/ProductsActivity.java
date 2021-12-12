package kz.burhancakmak.aysoftmobile.Products;

import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.depo1Aciklama1;
import static kz.burhancakmak.aysoftmobile.MainActivity.depo2Aciklama1;
import static kz.burhancakmak.aysoftmobile.MainActivity.itemFilterSelected;
import static kz.burhancakmak.aysoftmobile.MainActivity.itemMax;
import static kz.burhancakmak.aysoftmobile.MainActivity.itemMin;
import static kz.burhancakmak.aysoftmobile.MainActivity.menuGrupKayitNo;
import static kz.burhancakmak.aysoftmobile.MainActivity.ondegerFiyatGrubu1;
import static kz.burhancakmak.aysoftmobile.MainActivity.ondegerFiyatGrubu2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Adapters.ItemsAdapter;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.LoginActivity;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarMenu;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsPrclist;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsQuery;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsToplamlar;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsWithPrices;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;

public class ProductsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ItemsAdapter.ItemsListener {

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    SessionManagement session;
    ItemsAdapter itemsAdapter;
    DatabaseHandler databaseHandler;
    RecyclerView recyclerView;
    SearchView searchView;
    MenuItem searchItem;
    Toolbar toolbar;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    private static String NAV_FILTER;
    private static int VIEW_TYPE;
    List<ItemsWithPrices> productItemList = new ArrayList<>();
    List<CihazlarMenu> menuList = new ArrayList<>();
    List<ItemsPrclist> productPricesList = new ArrayList<>();
    List<String> priceList = new ArrayList<>();
    final int[] selectedItem = {0};
    String kurusHaneSayisiStokMiktar, kurusHaneSayisiStokTutar, ikiDepoKullanimi;
    int ikiFiyatKullanimi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        if (!(userSettingMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(userSettingMap.get(KEY_LANG));
        }

        setContentView(R.layout.activity_products);
        webSettingsMap = session.getWebSettings();

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            initViews();
            if (itemFilterSelected == 0) {
                new GetDataFromDatabase().execute();
            } else if (itemFilterSelected == 1) {
                new GetDataFromDatabaseStock().execute(itemMin, itemMax);
            } else if (itemFilterSelected == 2) {
                new GetDataFromDatabaseStock().execute(-itemMax, -itemMin);
            } else {
                new GetDataFromDatabaseZero().execute();
            }
        }
    }

    @Override
    public void onItemListener(int position) {
        alertChooseItemsMenuDialog(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items_menu, menu);
        searchItem = menu.findItem(R.id.product_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setIconified(false);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemsAdapter.getFilter().filter(newText);
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                invalidateOptionsMenu();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.product_price) {
            alertPriceChangeDialog();
        }
        if (item.getItemId() == R.id.product_view_type) {
            alertLayoutChangeDialog();
        }
        if (item.getItemId() == R.id.product_barcode_scan) {
            IntentIntegrator intentIntegrator = new IntentIntegrator(ProductsActivity.this);
            intentIntegrator.setPrompt(getString(R.string.products_camera_zoom_flash));
            intentIntegrator.setBeepEnabled(true);
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.setCaptureActivity(CameraCapture.class);
            intentIntegrator.initiateScan();
        }
        if (item.getItemId() == R.id.product_update) {
            new GetDataFromWeb().execute();
        }
        if (item.getItemId() == R.id.product_filter) {
            alertFilterDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        for (int i = 0; i < menuList.size(); i++) {
            if (menuList.get(i).getKayitNo() == item.getItemId()) {
                navigationView.getMenu().getItem(i).setChecked(true);
                NAV_FILTER = menuList.get(i).getFiltre();
            } else {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        if (itemFilterSelected == 0) {
            new GetDataFromDatabase().execute();
        } else if (itemFilterSelected == 1) {
            new GetDataFromDatabaseStock().execute(itemMin, itemMax);
        } else if (itemFilterSelected == 2) {
            new GetDataFromDatabaseStock().execute(-itemMax, -itemMin);
        } else {
            new GetDataFromDatabaseZero().execute();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getContents() != null) {
            String itemCode = databaseHandler.selectItemCode(intentResult.getContents());
            if (itemCode != null) {
                searchView.onActionViewExpanded();
                MenuItemCompat.expandActionView(searchItem);
                searchView.setQuery(itemCode, true);
                searchView.requestFocus();
            } else {
                Toast.makeText(this, R.string.items_barcode_not_found, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.main_nav_product);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.products_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        databaseHandler = DatabaseHandler.getInstance(this);
        menuList = databaseHandler.selectCihazlarMenu(1, menuGrupKayitNo);
        kurusHaneSayisiStokMiktar = parametreGetir(FIRMA_NO, "KurusHaneSayisiStokMiktar", "0");
        kurusHaneSayisiStokTutar = parametreGetir(FIRMA_NO, "KurusHaneSayisiStokTutar", "0");
        ikiFiyatKullanimi = Integer.parseInt(parametreGetir(FIRMA_NO, "IkiFiyatKullanimi", "0"));
        ikiDepoKullanimi = parametreGetir(FIRMA_NO, "IkiDepoKullanimi", "0");
        productPricesList = databaseHandler.selectPrclist();
        priceList.add("");
        for (int i = 0; i < productPricesList.size(); i++) {
            priceList.add(productPricesList.get(i).getFiyatGrubu());
        }

        navigationView = findViewById(R.id.products_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().clear();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        itemsAdapter = new ItemsAdapter(
                productItemList,
                this,
                this,
                Integer.parseInt(kurusHaneSayisiStokMiktar),
                Integer.parseInt(kurusHaneSayisiStokTutar),
                ikiFiyatKullanimi,
                Integer.parseInt(ikiDepoKullanimi)
        );
        recyclerView.setAdapter(itemsAdapter);

        if (VIEW_TYPE == 1) {
            itemsAdapter.setVIEW_TYPE(VIEW_TYPE);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(ProductsActivity.this));
        }

        if (menuList.size() > 0) {
            Menu menu = navigationView.getMenu();
            for (int i = 0; i < menuList.size(); i++) {
                int sayi = databaseHandler.selectCihazlarUrunSayisi(menuList.get(i).getFiltre());
                menu.add(
                        i,
                        menuList.get(i).getKayitNo(),
                        i,
                        menuList.get(i).getAciklama1() + " (" + sayi + ") ").setIcon(R.drawable.menu_ic_reports);
                if (menuList.get(i).getOndeger() == 1) {
                    NAV_FILTER = menuList.get(i).getFiltre();
                }
            }
            navigationView.getMenu().getItem(0).setChecked(true);

            if (itemMin == 0) {
                itemMin = 1L;
            }
            if (itemMax == 0) {
                itemMax = 999999999999999L;
            }

            if (itemFilterSelected == 0) {
                productItemList = databaseHandler.selectAllItems(ondegerFiyatGrubu1, ondegerFiyatGrubu2, NAV_FILTER);
            } else if (itemFilterSelected == 1) {
                productItemList = databaseHandler.selectAllItemsByStock(ondegerFiyatGrubu1, ondegerFiyatGrubu2, NAV_FILTER, itemMin, itemMax);
            } else if (itemFilterSelected == 2) {
                productItemList = databaseHandler.selectAllItemsByStock(ondegerFiyatGrubu1, ondegerFiyatGrubu2, NAV_FILTER, -itemMax, -itemMin);
            } else {
                productItemList = databaseHandler.selectAllItemsZero(ondegerFiyatGrubu1, ondegerFiyatGrubu2, NAV_FILTER);
            }
        }
    }

    private class GetDataFromDatabase extends AsyncTask<Void, Void, Void> {
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productItemList.clear();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... items) {
            productItemList = databaseHandler.selectAllItems(ondegerFiyatGrubu1, ondegerFiyatGrubu2, NAV_FILTER);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            itemsAdapter.setItemsList(
                    productItemList,
                    VIEW_TYPE,
                    depo1Aciklama1 + " " + getString(R.string.items_kalan1_label),
                    depo2Aciklama1.isEmpty() ? "" : depo2Aciklama1 + " " + getString(R.string.items_kalan2_label),
                    ondegerFiyatGrubu1.isEmpty() ? "" : ondegerFiyatGrubu1 + " " + getString(R.string.items_fiyat1_label),
                    ondegerFiyatGrubu2.isEmpty() ? "" : ondegerFiyatGrubu2 + " " + getString(R.string.items_fiyat2_label));
            products_progressBar.setVisibility(View.GONE);
        }
    }

    private class GetDataFromDatabaseZero extends AsyncTask<Void, Void, Void> {
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productItemList.clear();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... items) {
            productItemList = databaseHandler.selectAllItemsZero(ondegerFiyatGrubu1, ondegerFiyatGrubu2, NAV_FILTER);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            itemsAdapter.setItemsList(
                    productItemList,
                    VIEW_TYPE,
                    depo1Aciklama1 + " " + getString(R.string.items_kalan1_label),
                    depo2Aciklama1.isEmpty() ? "" : depo2Aciklama1 + " " + getString(R.string.items_kalan2_label),
                    ondegerFiyatGrubu1.isEmpty() ? "" : ondegerFiyatGrubu1 + " " + getString(R.string.items_fiyat1_label),
                    ondegerFiyatGrubu2.isEmpty() ? "" : ondegerFiyatGrubu2 + " " + getString(R.string.items_fiyat2_label));
            products_progressBar.setVisibility(View.GONE);
        }
    }

    private class GetDataFromDatabaseStock extends AsyncTask<Long, Void, Void> {
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productItemList.clear();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Long... items) {
            productItemList = databaseHandler.selectAllItemsByStock(ondegerFiyatGrubu1, ondegerFiyatGrubu2, NAV_FILTER, items[0], items[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            itemsAdapter.setItemsList(
                    productItemList,
                    VIEW_TYPE,
                    depo1Aciklama1 + " " + getString(R.string.items_kalan1_label),
                    depo2Aciklama1.isEmpty() ? "" : depo2Aciklama1 + " " + getString(R.string.items_kalan2_label),
                    ondegerFiyatGrubu1.isEmpty() ? "" : ondegerFiyatGrubu1 + " " + getString(R.string.items_fiyat1_label),
                    ondegerFiyatGrubu2.isEmpty() ? "" : ondegerFiyatGrubu2 + " " + getString(R.string.items_fiyat2_label));
            products_progressBar.setVisibility(View.GONE);
        }
    }

    private class GetDataFromWeb extends AsyncTask<Void, String, Void> {
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        ProgressDialog pd;
        Call<ItemsQuery> queryList;
        RetrofitApi retrofitApi;
        int counter = 0;

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pd.setMessage(getString(R.string.data_import_progressbar_products) + " " + values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(ProductsActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage(getString(R.string.data_import_progressbar_products_dowloading));
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();

            retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
            queryList = retrofitApi.getStockListQuick(phoneId,
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD),
                    DONEM_NO,
                    FIRMA_NO,
                    2);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Response<ItemsQuery> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {

                    ItemsQuery itemsQuery = response.body();
                    if (itemsQuery.getItemsToplamlar().size() > 0) {
                        for (int i = 2; i < itemsQuery.getItemsToplamlar().size(); i++) {
                            String[] toplams = itemsQuery.getItemsToplamlar().get(i).split("\\|");
                            ItemsToplamlar toplamlar = new ItemsToplamlar();
                            toplamlar.setKayitNo(Integer.parseInt(toplams[0]));
                            toplamlar.setStokKayitNo(Integer.parseInt(toplams[1]));
                            toplamlar.setDepoNo(Integer.parseInt(toplams[2]));
                            toplamlar.setDepoAdi(toplams[3]);
                            toplamlar.setToplam(Double.parseDouble(toplams[4]));
                            toplamlar.setStokKodu(toplams[5]);
                            publishProgress(++counter + " / " + itemsQuery.getItemsToplamlar().size());
                            databaseHandler.updateToplam(toplamlar, String.valueOf(toplamlar.getKayitNo()));
                        }

                        productItemList.clear();
                        if (itemFilterSelected == 0) {
                            productItemList = databaseHandler.selectAllItems(ondegerFiyatGrubu1, ondegerFiyatGrubu2, NAV_FILTER);
                        } else if (itemFilterSelected == 1) {
                            productItemList = databaseHandler.selectAllItemsByStock(ondegerFiyatGrubu1, ondegerFiyatGrubu2, NAV_FILTER, itemMin, itemMax);
                        } else if (itemFilterSelected == 2) {
                            productItemList = databaseHandler.selectAllItemsByStock(ondegerFiyatGrubu1, ondegerFiyatGrubu2, NAV_FILTER, -itemMax, -itemMin);
                        } else {
                            productItemList = databaseHandler.selectAllItemsZero(ondegerFiyatGrubu1, ondegerFiyatGrubu2, NAV_FILTER);
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
            pd.dismiss();
        }
    }

    private void alertChooseItemsMenuDialog(int position) {
        String[] items = new String[]{
                getString(R.string.products_info_toolbar_title),
                getString(R.string.products_info_stock_status),
                getString(R.string.client_menu_extract),
                getString(R.string.main_nav_dashboard),
                getString(R.string.products_stock_documents)};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.alertItemsStyle);
        builder.setTitle(productItemList.get(position).getStokAdi1());
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedItem[0] == 0) {
                    Intent intent = new Intent(ProductsActivity.this, ProductsInfoActivity.class);
                    intent.putExtra("kayitNo", productItemList.get(position).getKayitNo());
                    startActivity(intent);
                } else if (selectedItem[0] == 1) {
                    Intent intent = new Intent(ProductsActivity.this, ProductsStockActivity.class);
                    intent.putExtra("kayitNo", productItemList.get(position).getKayitNo());
                    startActivity(intent);
                } else if (selectedItem[0] == 2) {
                    Intent intent = new Intent(ProductsActivity.this, ProductsExtractActivity.class);
                    intent.putExtra("kayitNo", productItemList.get(position).getKayitNo());
                    intent.putExtra("kayitKod", productItemList.get(position).getStokKodu());
                    intent.putExtra("kayitAciklama", productItemList.get(position).getStokAdi1());
                    startActivity(intent);
                } else if (selectedItem[0] == 3) {
                    Intent intent = new Intent(ProductsActivity.this, ProductsDashboardActivity.class);
                    intent.putExtra("kayitNo", productItemList.get(position).getKayitNo());
                    startActivity(intent);
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
        List<String> itemTaskList = new ArrayList<>();
        itemTaskList.add(getString(R.string.client_alert_list_all));
        itemTaskList.add("Stokta olanlar");
        itemTaskList.add("Eksiye dusenler");
        itemTaskList.add("Stogu 0 olanlar");

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.item_filter_layout, null);
        builder.setView(view);

        LinearLayout editLayout = view.findViewById(R.id.itemLayout2);
        Spinner spinner1 = view.findViewById(R.id.item_menu_combo);
        EditText editDate1 = view.findViewById(R.id.editDate1);
        EditText editDate2 = view.findViewById(R.id.editDate2);

        if (itemFilterSelected == 0 || itemFilterSelected == 3) {
            editLayout.setVisibility(View.GONE);
        }

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_layout, itemTaskList);
        itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemAdapter.notifyDataSetChanged();
        spinner1.setAdapter(itemAdapter);
        spinner1.setSelection(itemFilterSelected);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                itemFilterSelected = i;
                if (itemFilterSelected == 0) {
                    editLayout.setVisibility(View.GONE);
                } else if (itemFilterSelected == 1) {
                    editLayout.setVisibility(View.VISIBLE);
                    editDate1.setText(String.valueOf(itemMin));
                    editDate1.requestFocus();
                    editDate1.setSelectAllOnFocus(true);
                    editDate2.setText(String.valueOf(itemMax));
                } else if (itemFilterSelected == 2) {
                    editLayout.setVisibility(View.VISIBLE);
                    editDate1.setText(String.valueOf(itemMin));
                    editDate1.requestFocus();
                    editDate1.setSelectAllOnFocus(true);
                    editDate2.setText(String.valueOf(itemMax));
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
                if (itemFilterSelected == 0) {
                    new GetDataFromDatabase().execute();
                } else if (itemFilterSelected == 1) {
                    if (!editDate1.getText().toString().isEmpty()) {
                        itemMin = Long.parseLong(editDate1.getText().toString());
                    }
                    if (!editDate2.getText().toString().isEmpty()) {
                        itemMax = Long.parseLong(editDate2.getText().toString());
                    }
                    new GetDataFromDatabaseStock().execute(itemMin, itemMax);
                } else if (itemFilterSelected == 2) {
                    if (!editDate1.getText().toString().isEmpty()) {
                        itemMin = Long.parseLong(editDate1.getText().toString());
                    }
                    if (!editDate2.getText().toString().isEmpty()) {
                        itemMax = Long.parseLong(editDate2.getText().toString());
                    }
                    new GetDataFromDatabaseStock().execute(-itemMax, -itemMin);
                } else {
                    new GetDataFromDatabaseZero().execute();
                }
            }
        });
        builder.show();
    }

    private void alertPriceChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.alert_change_price_layout, null);
        builder.setView(layout);

        Spinner spinner1 = layout.findViewById(R.id.alert_change_price1);
        Spinner spinner2 = layout.findViewById(R.id.alert_change_price2);
        TextView spinner2Label = layout.findViewById(R.id.alert_change_price2_label);
        ArrayAdapter<String> priceAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_layout, priceList);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceAdapter.notifyDataSetChanged();
        spinner1.setAdapter(priceAdapter);
        spinner1.setSelection(priceAdapter.getPosition(ondegerFiyatGrubu1));

        if (ikiFiyatKullanimi == 1) {
//            if (!ondegerFiyatGrubu2.isEmpty()) {
            spinner2.setAdapter(priceAdapter);
            spinner2.setSelection(priceAdapter.getPosition(ondegerFiyatGrubu2));
//            }
            /*else {
                spinner2Label.setVisibility(View.GONE);
                spinner2.setVisibility(View.GONE);
            }*/
        } else {
            spinner2Label.setVisibility(View.GONE);
            spinner2.setVisibility(View.GONE);
        }

        builder.setPositiveButton(getString(R.string.alert_change_language_button_apply), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ondegerFiyatGrubu1 = spinner1.getSelectedItem().toString();
                if (ikiFiyatKullanimi == 1) {
//                    if (!ondegerFiyatGrubu2.trim().isEmpty()) {
                    ondegerFiyatGrubu2 = spinner2.getSelectedItem().toString();
//                    }
                }
                new GetDataFromDatabase().execute();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void alertLayoutChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.alert_change_view_layout, null);
        builder.setTitle(getString(R.string.alert_change_layout_title));
        builder.setView(layout);
        RadioButton listButton = layout.findViewById(R.id.alert_list_radio);
        RadioButton gridButton = layout.findViewById(R.id.alert_list_grid);

        if (VIEW_TYPE == 0) {
            listButton.setChecked(true);
        } else {
            gridButton.setChecked(true);
        }

        builder.setPositiveButton(getString(R.string.alert_change_language_button_apply), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listButton.isChecked()) {
                    VIEW_TYPE = 0;
                    itemsAdapter.setVIEW_TYPE(VIEW_TYPE);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ProductsActivity.this));
                    recyclerView.setAdapter(itemsAdapter);
                }
                if (gridButton.isChecked()) {
                    VIEW_TYPE = 1;
                    itemsAdapter.setVIEW_TYPE(VIEW_TYPE);
                    recyclerView.setLayoutManager(new GridLayoutManager(ProductsActivity.this, 2));
                    recyclerView.setAdapter(itemsAdapter);
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

}