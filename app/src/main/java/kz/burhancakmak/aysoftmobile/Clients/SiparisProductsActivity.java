package kz.burhancakmak.aysoftmobile.Clients;

import static kz.burhancakmak.aysoftmobile.Clients.ClientsTasksActivity.clientKayitNo;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.depo1Aciklama1;
import static kz.burhancakmak.aysoftmobile.MainActivity.depo2Aciklama1;
import static kz.burhancakmak.aysoftmobile.MainActivity.menuGrupKayitNo;
import static kz.burhancakmak.aysoftmobile.MainActivity.ondegerFiyatGrubu1;
import static kz.burhancakmak.aysoftmobile.MainActivity.ondegerFiyatGrubu2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Adapters.SiparisProductsAdapter;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientSepet;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarMenu;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsPrclist;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsWithPrices;
import kz.burhancakmak.aysoftmobile.Products.CameraCapture;
import kz.burhancakmak.aysoftmobile.R;

public class SiparisProductsActivity extends AppCompatActivity
        implements SiparisProductsAdapter.OnOrderClickListener, NavigationView.OnNavigationItemSelectedListener {
    SessionManagement session;
    HashMap<String, String> hashMap;
    DatabaseHandler databaseHandler;
    SearchView searchView;
    MenuItem searchItem;
    SiparisProductsAdapter itemsAdapter;
    RecyclerView recyclerView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ClCard card;
    private static int VIEW_TYPE;
    private static final String KEY_LANG = "language";
    private static String NAV_FILTER;
    List<ItemsWithPrices> productItemList = new ArrayList<>();
    List<ItemsPrclist> productPricesList = new ArrayList<>();
    List<ClientSepet> sepetList = new ArrayList<>();
    List<CihazlarMenu> menuList = new ArrayList<>();
    List<String> priceList = new ArrayList<>();
    List<CihazlarFirmaParametreler> parametrelerList = new ArrayList<>();
    String KurusHaneSayisiStokMiktar, KurusHaneSayisiStokTutar,
            IkiFiyatKullanimi, IkiDepoKullanimi, SiparisteFiyatDegistirebilir;
    private boolean isCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManagement(getApplicationContext());
        hashMap = session.getUserDetails();
        if (!(hashMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(hashMap.get(KEY_LANG));
        }
        setContentView(R.layout.activity_siparis_products);

        initViews();
        new GetDataFromDatabase().execute();
    }

    @Override
    public void onItemClick(int position) {
        chooseProduct(position);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        int index = 0;
        for (int i = 0; i < menuList.size(); i++) {
            if (menuList.get(i).getKayitNo() == item.getItemId()) {
                index = i;
                NAV_FILTER = menuList.get(i).getFiltre();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.siparis_islemleri_menu, menu);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.product_view_type) {
            alertOnLayoutViewChange();
        }
        if (item.getItemId() == R.id.product_barcode_scan) {
            IntentIntegrator intentIntegrator = new IntentIntegrator(SiparisProductsActivity.this);
            intentIntegrator.setPrompt(getString(R.string.products_camera_zoom_flash));
            intentIntegrator.setBeepEnabled(true);
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.setCaptureActivity(CameraCapture.class);
            intentIntegrator.initiateScan();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        for (int i = 0; i < sepetList.size(); i++) {
            if (sepetList.get(i).getStokMiktar() == 0) {
                sepetList.remove(i);
            }
        }
        intent.putExtra("orderList", (Serializable) sepetList);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            for (int i = 0; i < sepetList.size(); i++) {
                if (sepetList.get(i).getStokMiktar() == 0) {
                    sepetList.remove(i);
                }
            }
            intent.putExtra("orderList", (Serializable) sepetList);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initViews() {
        Intent intent = getIntent();
        KurusHaneSayisiStokMiktar = intent.getStringExtra("KurusHaneSayisiStokMiktar");
        KurusHaneSayisiStokTutar = intent.getStringExtra("KurusHaneSayisiStokTutar");
        if ((List<ClientSepet>) intent.getSerializableExtra("orderList") != null) {
            sepetList = (List<ClientSepet>) intent.getSerializableExtra("orderList");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.main_nav_product);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.products_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        databaseHandler = DatabaseHandler.getInstance(this);
        parametrelerList = databaseHandler.selectParametreList(FIRMA_NO);
        menuList = databaseHandler.selectCihazlarMenu(1, menuGrupKayitNo);
        productPricesList = databaseHandler.selectPrclist();
        IkiFiyatKullanimi = parametreGetir("IkiFiyatKullanimi", "0");
        IkiDepoKullanimi = parametreGetir("IkiDepoKullanimi", "0");
        SiparisteFiyatDegistirebilir = parametreGetir("SiparisteFiyatDegistirebilir", "0");
        priceList.add("");

        for (int i = 0; i < productPricesList.size(); i++) {
            priceList.add(productPricesList.get(i).getFiyatGrubu());
        }

        navigationView = findViewById(R.id.products_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().clear();

        recyclerView = findViewById(R.id.orderRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(1);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        itemsAdapter = new SiparisProductsAdapter(
                this,
                productItemList,
                this,
                Integer.parseInt(KurusHaneSayisiStokMiktar),
                Integer.parseInt(KurusHaneSayisiStokTutar),
                Integer.parseInt(IkiDepoKullanimi));
        recyclerView.setAdapter(itemsAdapter);

        if (VIEW_TYPE == 1) {
            itemsAdapter.setVIEW_TYPE(VIEW_TYPE);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(SiparisProductsActivity.this));
        }

        if (menuList.size() > 0) {
            for (int i = 0; i < menuList.size(); i++) {
                CihazlarMenu menu = menuList.get(i);
                navigationView.getMenu().add(i, menu.getKayitNo(), i, menu.getAciklama1()).setIcon(R.drawable.menu_ic_reports);
                if (menu.getOndeger() == 1) {
                    NAV_FILTER = menu.getFiltre();
                }
            }
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    private class GetDataFromDatabase extends AsyncTask<Void, Void, Void> {
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);

        @Override
        protected void onPreExecute() {
            if (clientKayitNo != -1) {
                card = databaseHandler.selectClientById(clientKayitNo);
            }
            super.onPreExecute();
            productItemList.clear();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... items) {
            productItemList = databaseHandler.selectAllItems(card.getFiyatGrubu(), card.getFiyatGrubu(), NAV_FILTER);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (sepetList != null) {
                if (sepetList.size() > 0 && productItemList.size() > 0) {
                    for (int i = 0; i < sepetList.size(); i++) {
                        String stokKodu = sepetList.get(i).getStokKodu();
                        int miktar = sepetList.get(i).getStokMiktar();
                        for (int j = 0; j < productItemList.size(); j++) {
                            if (stokKodu.equals(productItemList.get(j).getStokKodu())) {
                                productItemList.get(j).setMiktar(miktar);
                            }
                        }
                    }
                }
            }
            itemsAdapter.setItemsList(productItemList, VIEW_TYPE,
                    depo1Aciklama1 + " " + getString(R.string.items_kalan1_label),
                    depo2Aciklama1.isEmpty() ? "" : depo2Aciklama1 + " " + getString(R.string.items_kalan2_label),
                    ondegerFiyatGrubu1.isEmpty() ? "" : card.getFiyatGrubu() + " " + getString(R.string.items_fiyat1_label),
                    ondegerFiyatGrubu2.isEmpty() ? "" : ondegerFiyatGrubu2 + " " + getString(R.string.items_fiyat2_label));
            products_progressBar.setVisibility(View.GONE);
        }
    }

    public void chooseProduct(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog);
        View view = getLayoutInflater().inflate(R.layout.siparis_products_layout, null);
        builder.setView(view);
        TextView stokKodu = view.findViewById(R.id.stokKodu);
        TextView stokAciklama = view.findViewById(R.id.stokAciklama);
        TextView stokTutar = view.findViewById(R.id.stokTutar);
        EditText editFiyat = view.findViewById(R.id.editFiyat);
        EditText editMiktar = view.findViewById(R.id.editMiktar);
        ImageView stokResim = view.findViewById(R.id.stokResim);
        Button changePrice = view.findViewById(R.id.changePrice);
        Klavye klavye = view.findViewById(R.id.keyboard);

        isCount = false;

        ItemsWithPrices items = productItemList.get(position);
        stokKodu.setText(items.getStokKodu());
        stokAciklama.setText(items.getStokAdi1());
        editFiyat.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", items.getFiyat1()));
        stokTutar.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", items.getFiyat1()));

        for (int i = 0; i < sepetList.size(); i++) {
            if (sepetList.get(i).getStokKodu().equals(productItemList.get(position).getStokKodu())) {
                try {
                    NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
                    Number number = format.parse(editFiyat.getText().toString());
                    assert number != null;
                    double price = number.doubleValue();
                    editMiktar.setText(String.valueOf(sepetList.get(i).getStokMiktar()));
                    editFiyat.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", items.getFiyat1()));
                    stokTutar.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", Integer.parseInt(editMiktar.getText().toString()) * price));
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        }

        if (SiparisteFiyatDegistirebilir.equals("1")) {
            changePrice.setVisibility(View.VISIBLE);
        } else {
            changePrice.setVisibility(View.GONE);
        }

        changePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCount = !isCount;
                if (isCount) {
                    changePrice.setText("Ok");
                    editFiyat.setTextIsSelectable(true);
                    editFiyat.setSelectAllOnFocus(true);
                    editFiyat.requestFocus();
                    editFiyat.setRawInputType(InputType.TYPE_CLASS_TEXT);
                    editFiyat.setShowSoftInputOnFocus(false);
                    InputConnection ic = editFiyat.onCreateInputConnection(new EditorInfo());
                    klavye.setInputConnection(ic);

                    editFiyat.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try {
                                NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
                                Number number = format.parse(editFiyat.getText().toString());
                                assert number != null;
                                double price = number.doubleValue();
                                if (!editMiktar.getText().toString().isEmpty() && !editFiyat.getText().toString().isEmpty()) {
                                    stokTutar.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", Double.parseDouble(editMiktar.getText().toString()) * price));
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                } else {
                    changePrice.setText(getString(R.string.alert_siparis_change));
                    editMiktar.setRawInputType(InputType.TYPE_CLASS_TEXT);
                    editMiktar.setTextIsSelectable(true);
                    editMiktar.requestFocus();
                    editMiktar.setSelectAllOnFocus(true);
                    editMiktar.setShowSoftInputOnFocus(false);
                    InputConnection ic = editMiktar.onCreateInputConnection(new EditorInfo());
                    klavye.setInputConnection(ic);

                    editMiktar.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try {
                                NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
                                Number number = format.parse(editFiyat.getText().toString());
                                assert number != null;
                                double price = number.doubleValue();
                                if (!editMiktar.getText().toString().isEmpty() && !editFiyat.getText().toString().isEmpty()) {
                                    stokTutar.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", Double.parseDouble(editMiktar.getText().toString()) * price));
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            }
        });

        editMiktar.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editMiktar.setTextIsSelectable(true);
        editMiktar.requestFocus();
        editMiktar.setSelectAllOnFocus(true);
        editMiktar.setShowSoftInputOnFocus(false);
        InputConnection ic = editMiktar.onCreateInputConnection(new EditorInfo());
        klavye.setInputConnection(ic);

        editMiktar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!editMiktar.getText().toString().isEmpty() && !editFiyat.getText().toString().isEmpty()) {
                        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
                        Number number = format.parse(editFiyat.getText().toString());
                        assert number != null;
                        double price = number.doubleValue();
                        stokTutar.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", Double.parseDouble(editMiktar.getText().toString()) * price));
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!items.getStokResim().isEmpty()) {
            String path = getExternalFilesDir("/aysoft") + File.separator + items.getStokResim();
            File file = new File(path);
            if (file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(path);
                stokResim.setImageBitmap(image);
            }
        } else if (!items.getStokResim1().isEmpty()) {
            String path = getExternalFilesDir("/aysoft") + File.separator + items.getStokResim1();
            File file = new File(path);
            if (file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(path);
                stokResim.setImageBitmap(image);
            }
        } else if (!items.getStokResim2().isEmpty()) {
            String path = getExternalFilesDir("/aysoft") + File.separator + items.getStokResim2();
            File file = new File(path);
            if (file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(path);
                stokResim.setImageBitmap(image);
            }
        } else if (!items.getStokResim3().isEmpty()) {
            String path = getExternalFilesDir("/aysoft") + File.separator + items.getStokResim3();
            File file = new File(path);
            if (file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(path);
                stokResim.setImageBitmap(image);
            }
        } else {
            stokResim.setImageResource(R.drawable.items_image);
        }
        builder.setPositiveButton(R.string.clients_orders_menu_save, new DialogInterface.OnClickListener() {
            boolean isFound = false;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (!editMiktar.getText().toString().isEmpty()) {
                        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
                        Number number = format.parse(editFiyat.getText().toString());
                        assert number != null;
                        double price = number.doubleValue();
                        if (sepetList == null) {
                            ClientSepet sepet = new ClientSepet();
                            sepet.setStokKayitNo(productItemList.get(position).getKayitNo());
                            sepet.setStokKodu(productItemList.get(position).getStokKodu());
                            sepet.setStokAdi(productItemList.get(position).getStokAdi1());
                            sepet.setStokFiyat(price);
                            sepet.setStokBirim(productItemList.get(position).getBirim());
                            sepet.setStokMiktar(Integer.parseInt(editMiktar.getText().toString()));
                            sepet.setStokTutar(Double.parseDouble(editMiktar.getText().toString()) * price);
                            sepet.setSatirIndirimOrani(0.0);
                            sepet.setSatirIndirimTutari(0.0);
                            sepet.setGenelIndirimTutari(0.0);
                            sepet.setNetTutar(Double.parseDouble(editMiktar.getText().toString()) * price);
                            sepet.setStokResim1(productItemList.get(position).getStokResim());
                            sepet.setStokResim2(productItemList.get(position).getStokResim1());
                            sepet.setStokResim3(productItemList.get(position).getStokResim2());
                            sepet.setStokResim4(productItemList.get(position).getStokResim3());
                            sepetList.add(sepet);
                        } else {
                            for (int i = 0; i < sepetList.size(); i++) {
                                if (sepetList.get(i).getStokKodu().equals(productItemList.get(position).getStokKodu())) {
                                    sepetList.get(i).setStokMiktar(Integer.parseInt(editMiktar.getText().toString()));
                                    sepetList.get(i).setStokFiyat(price);
                                    sepetList.get(i).setStokTutar(Double.parseDouble(editMiktar.getText().toString()) * price);
                                    sepetList.get(i).setSatirIndirimOrani(0.0);
                                    sepetList.get(i).setSatirIndirimTutari(0.0);
                                    sepetList.get(i).setGenelIndirimTutari(0.0);
                                    sepetList.get(i).setNetTutar(Double.parseDouble(editMiktar.getText().toString()) * price);
                                    isFound = true;
                                    break;
                                }
                            }
                            if (!isFound) {
                                ClientSepet sepet = new ClientSepet();
                                sepet.setStokKayitNo(productItemList.get(position).getKayitNo());
                                sepet.setStokKodu(productItemList.get(position).getStokKodu());
                                sepet.setStokAdi(productItemList.get(position).getStokAdi1());
                                sepet.setStokFiyat(price);
                                sepet.setStokBirim(productItemList.get(position).getBirim());
                                sepet.setStokMiktar(Integer.parseInt(editMiktar.getText().toString()));
                                sepet.setStokTutar(Double.parseDouble(editMiktar.getText().toString()) * price);
                                sepet.setSatirIndirimOrani(0.0);
                                sepet.setSatirIndirimTutari(0.0);
                                sepet.setGenelIndirimTutari(0.0);
                                sepet.setNetTutar(Double.parseDouble(editMiktar.getText().toString()) * price);
                                sepet.setStokResim1(productItemList.get(position).getStokResim());
                                sepet.setStokResim2(productItemList.get(position).getStokResim1());
                                sepet.setStokResim3(productItemList.get(position).getStokResim2());
                                sepet.setStokResim4(productItemList.get(position).getStokResim3());
                                sepetList.add(sepet);
                            }
                        }
                        productItemList.get(position).setMiktar(Integer.parseInt(editMiktar.getText().toString()));
                        productItemList.get(position).setFiyat1(price);
                        productItemList.get(position).setClientNo(clientKayitNo);
                        itemsAdapter.notifyItemChanged(position);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        builder.show();
    }

    private void changePriceDialog(int position, double price) {
        MaterialAlertDialogBuilder builder1 = new MaterialAlertDialogBuilder(this);
        builder1.setCancelable(false);
        View view1 = getLayoutInflater().inflate(R.layout.siparis_price_change_layout, null);
        builder1.setView(view1);

        EditText oldPrice = view1.findViewById(R.id.editPrice1);
        EditText newPrice = view1.findViewById(R.id.editPrice2);

        oldPrice.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", price));
        newPrice.requestFocus();

        builder1.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chooseProduct(position);
            }
        });
        builder1.setNegativeButton(R.string.login_internet_connection_btnCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder1.show();
    }

    private void alertOnLayoutViewChange() {
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
                    recyclerView.setLayoutManager(new LinearLayoutManager(SiparisProductsActivity.this));
                    recyclerView.setAdapter(itemsAdapter);
                }
                if (gridButton.isChecked()) {
                    VIEW_TYPE = 1;
                    itemsAdapter.setVIEW_TYPE(VIEW_TYPE);
                    recyclerView.setLayoutManager(new GridLayoutManager(SiparisProductsActivity.this, 2));
                    recyclerView.setAdapter(itemsAdapter);
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    private String parametreGetir(String param, String deger) {
        String parametreDeger = deger;
        for (CihazlarFirmaParametreler parametreler : parametrelerList) {
            if (parametreler.getParametreAdi().equals(param)) {
                parametreDeger = parametreler.getParametreDegeri();
            }
        }
        return parametreDeger;
    }

}
