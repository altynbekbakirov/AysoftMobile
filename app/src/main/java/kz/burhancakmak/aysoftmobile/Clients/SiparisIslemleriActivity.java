package kz.burhancakmak.aysoftmobile.Clients;

import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.SearchView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Adapters.SepetAdapter;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientSepet;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientSiparis;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.Models.Products.ItemsWithPrices;
import kz.burhancakmak.aysoftmobile.R;

public class SiparisIslemleriActivity extends AppCompatActivity implements SepetAdapter.OnOrderListener {
    SessionManagement session;
    HashMap<String, String> hashMap;
    DatabaseHandler databaseHandler;
    private static final String KEY_LANG = "language";
    int islemTuru, kayitNo, position, updateCart, spinnerSelected, OdemeSekli;
    long siparisNo;
    double discountTotal, discountPercent;
    RecyclerView recyclerView;
    SepetAdapter adapter;
    List<ClientSepet> sepetList = new ArrayList<>();
    List<CihazlarFirmaParametreler> parametrelerList = new ArrayList<>();
    ClCard card;
    SearchView searchView;
    private static String NAV_FILTER;
    LinearLayout mainlayout, bottomPanelCollapsable;
    TextView bottomPanelTotal, bottomPanelSale, bottomPanelNet, bottomPanelRowCount, bottomPanelCount;
    ImageView bottomPanelImage;
    String KurusHaneSayisiStokMiktar, KurusHaneSayisiStokTutar, Aciklama, SiparisTeslimTarihi, SiparisteFiyatDegistirebilir, siparisteHizliEkledeSatirBirlestirilecek;
    private boolean isCount;
    private int itemCount = 1;
    private boolean isItemFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManagement(getApplicationContext());
        hashMap = session.getUserDetails();
        if (!(hashMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(hashMap.get(KEY_LANG));
        }
        setContentView(R.layout.activity_siparis_islemleri);

        databaseHandler = DatabaseHandler.getInstance(this);
        initViews();
        new ClientCartTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (islemTuru != 2) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.clients_orders_menu, menu);
            if (card.getAdres1().isEmpty()) {
//                menu.findItem(R.id.client_orders_change_data).setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.client_orders_change_data) {
            if (sepetList.size() > 0) {
                editOrderInfo();
            } else {
                cartEmptyWarning();
            }
        }
        if (item.getItemId() == R.id.client_orders_discount) {
            if (sepetList.size() > 0) {
                chooseDiscountType();
            } else {
                cartEmptyWarning();
            }
        }
        if (item.getItemId() == R.id.client_orders_add_item) {
            chooseAddItemProduct();
        }
        if (item.getItemId() == R.id.client_orders_add) {
            Intent intent = new Intent(this, SiparisProductsActivity.class);
            intent.putExtra("KurusHaneSayisiStokMiktar", KurusHaneSayisiStokMiktar);
            intent.putExtra("KurusHaneSayisiStokTutar", KurusHaneSayisiStokTutar);
            if (sepetList.size() > 0) {
                intent.putExtra("orderList", (Serializable) sepetList);
            }
            startActivityForResult(intent, 55);
        }
        /*if (item.getItemId() == R.id.client_orders_info) {
            System.out.println("client_orders_info");
        }*/
        if (item.getItemId() == R.id.client_orders_save) {
            if (sepetList.size() > 0) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();
                ClientSiparis siparis = new ClientSiparis();
                siparis.setCariKayitNo(kayitNo);
                siparis.setTarih(dateFormat.format(date));
                siparis.setEklenmeSaati(dateFormat1.format(date));
                siparis.setDegisiklikSaati(dateFormat1.format(date));
                siparis.setAciklama(Aciklama);
                siparis.setOdemeSekli(OdemeSekli);
                siparis.setSiparisTeslimTarihi(SiparisTeslimTarihi == null ? dateFormat.format(date) : SiparisTeslimTarihi);
                siparis.setIslemTipi(islemTuru);
                siparis.setErpGonderildi(0);
                siparis.setZiyaretKayitNo(session.getKeyVisit().intValue());
                double total = 0.0;
                if (updateCart == -1) {
                    siparisNo = databaseHandler.insertSiparis(siparis);
                    for (int i = 0; i < sepetList.size(); i++) {
                        ClientSepet sepet = sepetList.get(i);
                        sepet.setSiparisKayitNo(siparisNo);
                        total += sepet.getStokTutar();
                        databaseHandler.insertSepet(sepet);
                    }
                    siparis.setTutar(total);
                    siparis.setNetTutar(total - discountTotal);
                    if (spinnerSelected == 0) {
                        siparis.setGenelIndirimOrani(discountPercent);
                        siparis.setGenelIndirimTutari(discountTotal);

                    } else {
                        siparis.setGenelIndirimOrani(0.0);
                        siparis.setGenelIndirimTutari(discountTotal);
                    }
                    databaseHandler.updateSiparis(siparis, String.valueOf(siparisNo));
                } else {
                    databaseHandler.deleteSepetBySiparisNo(String.valueOf(siparisNo));
                    for (int i = 0; i < sepetList.size(); i++) {
                        ClientSepet sepet = sepetList.get(i);
                        sepet.setSiparisKayitNo(siparisNo);
                        total += sepet.getStokTutar();
                        databaseHandler.insertSepet(sepet);
                    }
                    siparis.setTutar(total);
                    siparis.setNetTutar(total - discountTotal);
                    if (spinnerSelected == 0) {
                        siparis.setGenelIndirimOrani(discountPercent);
                        siparis.setGenelIndirimTutari(discountTotal);
                    } else {
                        siparis.setGenelIndirimOrani(0.0);
                        siparis.setGenelIndirimTutari(discountTotal);
                    }
                    databaseHandler.updateSiparis(siparis, String.valueOf(siparisNo));
                }
            } else {
                databaseHandler.deleteSiparis(String.valueOf(siparisNo));
                databaseHandler.deleteSepetBySiparisNo(String.valueOf(siparisNo));
            }
            setResult(RESULT_OK);
            finish();
        }
        if (item.getItemId() == R.id.client_orders_cancel) {
            confirmationCancel();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 55) {
            sepetList = (List<ClientSepet>) data.getSerializableExtra("orderList");
            if (sepetList != null) {
                if (sepetList.size() > 0) {
                    adapter.setOrderList(sepetList);
                    showBottomPanel(sepetList);
                }
            }
        }
    }

    @Override
    public void onOrderClick(int position) {
        if (islemTuru != 2) chooseMenuOptions(position);
    }

    @Override
    public void onBackPressed() {
        if (islemTuru != 2) {
            confirmationCancel();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void initViews() {
        Intent intent = getIntent();
        islemTuru = intent.getIntExtra("islemTuru", -1);
        position = intent.getIntExtra("position", -1);
        kayitNo = intent.getIntExtra("kayitNo", -1);
        siparisNo = intent.getLongExtra("siparisNo", -1);
        updateCart = intent.getIntExtra("updateCart", -1);
        KurusHaneSayisiStokMiktar = intent.getStringExtra("KurusHaneSayisiStokMiktar");
        KurusHaneSayisiStokTutar = intent.getStringExtra("KurusHaneSayisiStokTutar");
        card = databaseHandler.selectClientById(kayitNo);
        parametrelerList = databaseHandler.selectParametreList(FIRMA_NO);
        SiparisteFiyatDegistirebilir = parametreGetir("SiparisteFiyatDegistirebilir");
        siparisteHizliEkledeSatirBirlestirilecek = parametreGetir("SiparisteHizliEkledeSatirBirlestirilecek");
        if (intent.getDoubleExtra("discountPercent", -1) != -1) {
            discountPercent = intent.getDoubleExtra("discountPercent", -1);
            if (discountPercent > 0) {
                spinnerSelected = 0;
                discountTotal = discountPercent;
            } else {
                spinnerSelected = 1;
            }
        }
        if (intent.getDoubleExtra("discountTotal", -1) != -1) {
            discountTotal = intent.getDoubleExtra("discountTotal", -1);
        }

        String toolbarTitle;
        if (islemTuru == 0) {
            toolbarTitle = getString(R.string.alert_siparis_purchases);
        } else if (islemTuru == 1) {
            toolbarTitle = getString(R.string.alert_siparis_sales);
        } else {
            toolbarTitle = card.getUnvani1();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(toolbarTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (islemTuru != 2) {
                    confirmationCancel();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });

        recyclerView = findViewById(R.id.ordersRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = new SepetAdapter(this, sepetList, this, Integer.parseInt(KurusHaneSayisiStokMiktar), Integer.parseInt(KurusHaneSayisiStokTutar));
        recyclerView.setAdapter(adapter);

        mainlayout = findViewById(R.id.mainLinear);
        bottomPanelCollapsable = findViewById(R.id.bottomPanelCollapsable);
        bottomPanelCollapsable.setVisibility(View.GONE);
        bottomPanelImage = findViewById(R.id.bottomPanelImage);
        bottomPanelTotal = findViewById(R.id.bottomPanelTotal);
        bottomPanelSale = findViewById(R.id.bottomPanelSale);
        bottomPanelNet = findViewById(R.id.bottomPanelNet);
        bottomPanelRowCount = findViewById(R.id.bottomPanelRowCount);
        bottomPanelCount = findViewById(R.id.bottomPanelCount);

        mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomPanelCollapsable.getVisibility() == View.GONE) {
                    bottomPanelImage.setImageResource(R.drawable.ic_arrow_circle_down);
                    expand();
                } else {
                    bottomPanelImage.setImageResource(R.drawable.ic_arrow_circle_up);
                    collapse();
                }
            }
        });
    }

    private class ClientCartTask extends AsyncTask<Void, Void, Void> {
        RelativeLayout products_progressBar = findViewById(R.id.products_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... items) {
            sepetList = databaseHandler.selectAllSepet(siparisNo);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.setOrderList(sepetList);
            showBottomPanel(sepetList);
            products_progressBar.setVisibility(View.GONE);
        }
    }

    private void editOrderInfo() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.client_orders_change_data_layout, null);
        builder.setView(view);
        EditText deliveryDateEdit = view.findViewById(R.id.deliveryDateEdit);
        EditText deliveryAddressEdit = view.findViewById(R.id.deliveryAddressEdit);
        EditText deliveryDefinitionEdit = view.findViewById(R.id.deliveryDefinitionEdit);
        deliveryAddressEdit.setText(card.getAdres1());
        deliveryDefinitionEdit.requestFocus();
        deliveryDefinitionEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Spannable spannable = deliveryDefinitionEdit.getText();
                if (!TextUtils.isEmpty(s) && s.length() > 9) {
                    spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(SiparisIslemleriActivity.this, R.color.red)),
                            0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Spinner deliveryPaymentType = view.findViewById(R.id.deliveryPaymentType);
        String[] deliveryArray = new String[]{getString(R.string.client_order_shipping_payment_cash), getString(R.string.client_order_shipping_payment_charge)};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_order_item_layout, deliveryArray);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_order_dropdownitem_layout);
        deliveryPaymentType.setAdapter(arrayAdapter);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = new Date();
        deliveryDateEdit.setText(dateFormat.format(date1));

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
                deliveryDateEdit.setText(sdf.format(myCalendar.getTime()));
            }
        };
        deliveryDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SiparisIslemleriActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        builder.setPositiveButton(R.string.clients_orders_menu_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Aciklama = deliveryDefinitionEdit.getText().toString();
                SiparisTeslimTarihi = deliveryAddressEdit.getText().toString();
                OdemeSekli = deliveryPaymentType.getSelectedItemPosition();
            }
        });
        builder.setNegativeButton(R.string.clients_orders_menu_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void chooseDiscountType() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.client_orders_choose_discount_layout, null);
        builder.setView(view);
        EditText priceTypeValue = view.findViewById(R.id.priceTypeValue);
        EditText priceOrderTotal = view.findViewById(R.id.priceOrderTotal);
        EditText priceOrderNetTotal = view.findViewById(R.id.priceOrderNetTotal);
        Spinner priceTypeSpinner = view.findViewById(R.id.priceType);
        priceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!priceTypeValue.getText().toString().isEmpty()) {
                    double priceType = Double.parseDouble(priceTypeValue.getText().toString().split(",")[0]);
                    if (priceType > 0) {
                        if (position == 0) {
                            discountPercent = priceType;
                            discountTotal = Double.parseDouble(priceOrderTotal.getText().toString().split(",")[0]) * (discountPercent / 100);
                            priceOrderNetTotal.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", Double.parseDouble(priceOrderTotal.getText().toString().split(",")[0]) - discountTotal));
                        } else if (position == 1) {
                            discountPercent = 0;
                            discountTotal = priceType;
                            priceOrderNetTotal.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", Double.parseDouble(priceOrderTotal.getText().toString().split(",")[0]) - discountTotal));
                        }
                    }
                } else {
                    priceOrderNetTotal.setText(priceOrderTotal.getText().toString());
                }
                priceTypeValue.requestFocus();
                priceTypeValue.setSelectAllOnFocus(true);
                spinnerSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        String[] deliveryArray = new String[]{getString(R.string.client_order_discount_percent), getString(R.string.client_order_discount_number)};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_order_item_layout, deliveryArray);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_order_dropdownitem_layout);
        priceTypeSpinner.setAdapter(arrayAdapter);
        double total = 0, net = 0;
        if (sepetList.size() > 0) {
            for (ClientSepet sepet : sepetList) {
                total += sepet.getStokTutar();
                net += sepet.getNetTutar();
            }
        }
        priceTypeSpinner.setSelection(spinnerSelected);
        priceOrderTotal.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", total));
        priceTypeValue.requestFocus();
        if (discountPercent > 0) {
            if (priceTypeSpinner.getSelectedItemPosition() == 0) {
                priceTypeValue.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", discountPercent));
                priceOrderNetTotal.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", total - (discountPercent * total) / 100));
            } else {
                priceTypeValue.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", discountTotal));
                priceOrderNetTotal.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", net - discountTotal));
            }
            priceTypeValue.setSelectAllOnFocus(true);
        } else {
            priceTypeValue.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", discountTotal));
            priceOrderNetTotal.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", net - discountTotal));
            priceTypeValue.setSelectAllOnFocus(true);
        }
        priceTypeValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    if (Double.parseDouble(s.toString().split(",")[0]) > 0) {
                        if (priceTypeSpinner.getSelectedItemPosition() == 0) {
                            discountPercent = Double.parseDouble(priceTypeValue.getText().toString().split(",")[0]);
                            discountTotal = Double.parseDouble(priceOrderTotal.getText().toString().split(",")[0]) * (discountPercent / 100);
                            priceOrderNetTotal.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", Double.parseDouble(priceOrderTotal.getText().toString().split(",")[0]) - discountTotal));
                        } else if (priceTypeSpinner.getSelectedItemPosition() == 1) {
                            discountPercent = 0;
                            discountTotal = Double.parseDouble(priceTypeValue.getText().toString().split(",")[0]);
                            priceOrderNetTotal.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", Double.parseDouble(priceOrderTotal.getText().toString().split(",")[0]) - discountTotal));
                        }
                    }
                } else {
                    priceOrderNetTotal.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", Double.parseDouble(priceOrderTotal.getText().toString().split(",")[0])));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        builder.setPositiveButton(R.string.clients_orders_menu_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showBottomPanel(sepetList);
            }
        });
        builder.setNegativeButton(R.string.clients_orders_menu_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void cartEmptyWarning() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.info_warning_title);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_confirmation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMessage(Html.fromHtml("<b><font color='#000000' size='16'>" + getString(R.string.client_order_discount_cart_empty)
                    + "</font></b>", Html.FROM_HTML_MODE_COMPACT));
        } else {
            builder.setMessage(Html.fromHtml("<b><font color='#000000'>" + getString(R.string.client_order_discount_cart_empty) + "</font></b>"));
        }
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void chooseMenuOptions(int position) {
        int[] item = {0};
        String[] items = new String[]{
                getString(R.string.alert_kasa_menu_edit),
                getString(R.string.alert_kasa_menu_delete)};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.alert_kasa_title_choose);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_menu);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (item[0]) {
                    case 0:
                        chooseProduct(position);
                        break;
                    case 1:
                        deleteCurrentItem(position);
                        break;
                    case 2:
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
        builder.setSingleChoiceItems(items, item[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                item[0] = which;
            }
        });
        builder.show();
    }

    private void deleteCurrentItem(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.alert_kasa_delete_task_title);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_confirmation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMessage(Html.fromHtml("<b><font color='#FF0000'>" + getString(R.string.alert_kasa_delete_task_message) + "</font></b><br>"
                    + "<i>" + sepetList.get(position).getStokKodu() + ", " + sepetList.get(position).getStokAdi() + "</i>", Html.FROM_HTML_MODE_COMPACT));
        } else {
            builder.setMessage(Html.fromHtml("<b><font color='#FF0000'>" + getString(R.string.alert_kasa_delete_task_message) + "</font></b><br>"
                    + "<i>" + sepetList.get(position).getStokKodu() + ", " + sepetList.get(position).getStokAdi() + "</i>"));
        }

        builder.setPositiveButton(R.string.alert_exit_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sepetList.remove(position);
                adapter.notifyDataSetChanged();
                showBottomPanel(sepetList);
            }
        });
        builder.setNegativeButton(R.string.alert_exit_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void chooseProduct(int position) {
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

        stokKodu.setText(sepetList.get(position).getStokKodu());
        stokAciklama.setText(sepetList.get(position).getStokAdi());
        editFiyat.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", sepetList.get(position).getStokFiyat()));
        stokTutar.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", sepetList.get(position).getStokTutar()));

        if (sepetList.get(position).getStokMiktar() != null) {
            editMiktar.setText(String.valueOf(sepetList.get(position).getStokMiktar()));
//            stokTutar.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", Double.parseDouble(editMiktar.getText().toString().replaceAll("\\s+", "").replaceAll("\\.", "")) * Double.parseDouble(editFiyat.getText().toString().replaceAll("\\s+", "").replaceAll("\\.", ""))));
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
                            if (!editMiktar.getText().toString().isEmpty() && !editFiyat.getText().toString().isEmpty()) {
                                stokTutar.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", Double.parseDouble(editMiktar.getText().toString().replaceAll("\\s+", "").replaceAll("\\.", "")) * Double.parseDouble(editFiyat.getText().toString().replaceAll("\\s+", "").replaceAll("\\.", ""))));
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
                            if (!editMiktar.getText().toString().isEmpty() && !editFiyat.getText().toString().isEmpty()) {
                                stokTutar.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", Double.parseDouble(editMiktar.getText().toString().replaceAll("\\s+", "").replaceAll("\\.", "")) * Double.parseDouble(editFiyat.getText().toString().replaceAll("\\s+", "").replaceAll("\\.", ""))));
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
                if (!editMiktar.getText().toString().isEmpty() && !editFiyat.getText().toString().isEmpty()) {
                    stokTutar.setText(String.format("%." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", Integer.parseInt(editMiktar.getText().toString()) * Double.parseDouble(editFiyat.getText().toString())));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!sepetList.get(position).getStokResim1().isEmpty()) {
            String path = getExternalFilesDir("/aysoft") + File.separator + sepetList.get(position).getStokResim1();
            File file = new File(path);
            if (file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(path);
                stokResim.setImageBitmap(image);
            } else {
                stokResim.setImageResource(R.drawable.items_image);
            }
        } else if (!sepetList.get(position).getStokResim2().isEmpty()) {
            String path = getExternalFilesDir("/aysoft") + File.separator + sepetList.get(position).getStokResim2();
            File file = new File(path);
            if (file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(path);
                stokResim.setImageBitmap(image);
            } else {
                stokResim.setImageResource(R.drawable.items_image);
            }
        } else if (!sepetList.get(position).getStokResim3().isEmpty()) {
            String path = getExternalFilesDir("/aysoft") + File.separator + sepetList.get(position).getStokResim3();
            File file = new File(path);
            if (file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(path);
                stokResim.setImageBitmap(image);
            } else {
                stokResim.setImageResource(R.drawable.items_image);
            }
        } else if (!sepetList.get(position).getStokResim4().isEmpty()) {
            String path = getExternalFilesDir("/aysoft") + File.separator + sepetList.get(position).getStokResim4();
            File file = new File(path);
            if (file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(path);
                stokResim.setImageBitmap(image);
            } else {
                stokResim.setImageResource(R.drawable.items_image);
            }
        } else {
            stokResim.setImageResource(R.drawable.items_image);
        }

        builder.setPositiveButton(R.string.clients_orders_menu_save, new DialogInterface.OnClickListener() {
            final boolean isFound = false;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!editMiktar.getText().toString().isEmpty()) {
                    if (Integer.parseInt(editMiktar.getText().toString()) > 0) {
                        sepetList.get(position).setStokMiktar(Integer.parseInt(editMiktar.getText().toString()));
                        sepetList.get(position).setStokTutar(Double.parseDouble(editMiktar.getText().toString()) * Double.parseDouble(editFiyat.getText().toString()));
                        sepetList.get(position).setStokFiyat(Double.parseDouble(editFiyat.getText().toString()));
                        sepetList.get(position).setSatirIndirimOrani(0.0);
                        sepetList.get(position).setSatirIndirimTutari(0.0);
                        sepetList.get(position).setGenelIndirimTutari(0.0);
                        sepetList.get(position).setNetTutar(Double.parseDouble(editMiktar.getText().toString()) * Double.parseDouble(editFiyat.getText().toString()));
                    }
                    adapter.notifyItemChanged(position);
                    showBottomPanel(sepetList);
                }
            }
        });
        builder.show();
    }

    private void chooseAddItemProduct() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog);
        View view = getLayoutInflater().inflate(R.layout.siparis_products_add_item_layout, null);
        builder.setCancelable(false);
        builder.setView(view);
        searchView = view.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    isItemFound = false;
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

        builder.setPositiveButton(R.string.clients_orders_menu_close, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void confirmationCancel() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.alert_kasa_delete_task_title);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_confirmation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMessage(Html.fromHtml("<b><font color='#000000' size='16'>" + getString(R.string.alert_orders_cancel_message)
                    + "</font></b>", Html.FROM_HTML_MODE_COMPACT));
        } else {
            builder.setMessage(Html.fromHtml("<b><font color='#000000'>" + getString(R.string.alert_orders_cancel_message) + "</font></b>"));
        }
        builder.setPositiveButton(R.string.alert_exit_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        builder.setNegativeButton(R.string.alert_exit_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private String parametreGetir(String param) {
        String parametreDeger = "0";
        for (CihazlarFirmaParametreler parametreler : parametrelerList) {
            if (parametreler.getParametreAdi().equals(param)) {
                parametreDeger = parametreler.getParametreDegeri();
            }
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

    private void expand() {
        bottomPanelCollapsable.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        bottomPanelCollapsable.measure(widthSpec, heightSpec);
        ValueAnimator mAnimator = slideAnimator(0, bottomPanelCollapsable.getMeasuredHeight());
        mAnimator.start();
    }

    private void collapse() {
        int finalHeight = bottomPanelCollapsable.getHeight();
        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                bottomPanelCollapsable.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = bottomPanelCollapsable.getLayoutParams();
                layoutParams.height = value;
                bottomPanelCollapsable.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void showBottomPanel(List<ClientSepet> sepets) {
        double total = 0, net = 0;
        int count = 0;

        if (sepets.size() > 0) {
            for (int i = 0; i < sepets.size(); i++) {
                count += sepets.get(i).getStokMiktar();
                total += sepets.get(i).getStokTutar();
                net += sepets.get(i).getStokTutar();
            }
            bottomPanelTotal.setText(String.format("%,." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", total));
            bottomPanelSale.setText(String.format("%,." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", discountTotal));
            bottomPanelNet.setText(String.format("%,." + Integer.parseInt(KurusHaneSayisiStokTutar) + "f", total - discountTotal));
            bottomPanelRowCount.setText(String.valueOf(sepets.size()));
            bottomPanelCount.setText(String.valueOf(count));
        } else {
            bottomPanelTotal.setText("0");
            bottomPanelSale.setText("0");
            bottomPanelNet.setText("0");
            bottomPanelRowCount.setText("0");
            bottomPanelCount.setText("0");
        }
    }

    private class GetDataFromDatabase extends AsyncTask<Void, Void, Void> {
        String errorMessage = null;
        ItemsWithPrices item;
        boolean isFound = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... items) {
            try {
                item = databaseHandler.selectItemByBarcode(card.getFiyatGrubu(), card.getFiyatGrubu(), NAV_FILTER);
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (item.getKayitNo() != null) {
                isItemFound = true;
                if (siparisteHizliEkledeSatirBirlestirilecek.equals("0")) {
                    ClientSepet sepet = new ClientSepet();
                    sepet.setStokKayitNo(item.getKayitNo());
                    sepet.setStokKodu(item.getStokKodu());
                    sepet.setStokAdi(item.getStokAdi1());
                    sepet.setStokFiyat(item.getFiyat1());
                    sepet.setStokBirim(item.getBirim());
                    sepet.setStokMiktar(itemCount);
                    sepet.setStokTutar(itemCount * item.getFiyat1());
                    sepet.setSatirIndirimOrani(0.0);
                    sepet.setSatirIndirimTutari(0.0);
                    sepet.setGenelIndirimTutari(0.0);
                    sepet.setNetTutar(itemCount * item.getFiyat1());
                    sepet.setStokResim1(item.getStokResim());
                    sepet.setStokResim2(item.getStokResim1());
                    sepet.setStokResim3(item.getStokResim2());
                    sepet.setStokResim4(item.getStokResim3());
                    sepetList.add(sepet);
                    adapter.setOrderList(sepetList);
                    showBottomPanel(sepetList);
                } else {
                    if (sepetList == null) {
                        ClientSepet sepet = new ClientSepet();
                        sepet.setStokKayitNo(item.getKayitNo());
                        sepet.setStokKodu(item.getStokKodu());
                        sepet.setStokAdi(item.getStokAdi1());
                        sepet.setStokFiyat(item.getFiyat1());
                        sepet.setStokBirim(item.getBirim());
                        sepet.setStokMiktar(itemCount);
                        sepet.setStokTutar(itemCount * item.getFiyat1());
                        sepet.setSatirIndirimOrani(0.0);
                        sepet.setSatirIndirimTutari(0.0);
                        sepet.setGenelIndirimTutari(0.0);
                        sepet.setNetTutar(itemCount * item.getFiyat1());
                        sepet.setStokResim1(item.getStokResim());
                        sepet.setStokResim2(item.getStokResim1());
                        sepet.setStokResim3(item.getStokResim2());
                        sepet.setStokResim4(item.getStokResim3());
                        sepetList.add(sepet);
                    } else {
                        for (int i = 0; i < sepetList.size(); i++) {
                            if (sepetList.get(i).getStokKodu().equals(item.getStokKodu())) {
                                sepetList.get(i).setStokMiktar(sepetList.get(i).getStokMiktar() + itemCount);
                                sepetList.get(i).setStokFiyat(item.getFiyat1());
                                sepetList.get(i).setStokTutar((sepetList.get(i).getStokMiktar() + itemCount) * item.getFiyat1());
                                sepetList.get(i).setSatirIndirimOrani(0.0);
                                sepetList.get(i).setSatirIndirimTutari(0.0);
                                sepetList.get(i).setGenelIndirimTutari(0.0);
                                sepetList.get(i).setNetTutar((sepetList.get(i).getStokMiktar() + itemCount) * item.getFiyat1());
                                isFound = true;
                                break;
                            }
                        }
                        if (!isFound) {
                            ClientSepet sepet = new ClientSepet();
                            sepet.setStokKayitNo(item.getKayitNo());
                            sepet.setStokKodu(item.getStokKodu());
                            sepet.setStokAdi(item.getStokAdi1());
                            sepet.setStokFiyat(item.getFiyat1());
                            sepet.setStokBirim(item.getBirim());
                            sepet.setStokMiktar(itemCount);
                            sepet.setStokTutar(itemCount * item.getFiyat1());
                            sepet.setSatirIndirimOrani(0.0);
                            sepet.setSatirIndirimTutari(0.0);
                            sepet.setGenelIndirimTutari(0.0);
                            sepet.setNetTutar(itemCount * item.getFiyat1());
                            sepet.setStokResim1(item.getStokResim());
                            sepet.setStokResim2(item.getStokResim1());
                            sepet.setStokResim3(item.getStokResim2());
                            sepet.setStokResim4(item.getStokResim3());
                            sepetList.add(sepet);
                        }
                    }
                    adapter.setOrderList(sepetList);
                    showBottomPanel(sepetList);
                }
                if (isItemFound) {
                    searchView.setQuery("", false);
                    searchView.setIconified(true);
                    searchView.requestFocus();
                    InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                } else  {
                    InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        }
    }

}