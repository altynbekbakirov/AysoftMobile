package kz.burhancakmak.aysoftmobile.Clients;

import static android.app.Activity.RESULT_OK;
import static kz.burhancakmak.aysoftmobile.Clients.ClientsTasksActivity.clientKayitNo;
import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Adapters.SiparisAdapter;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientSepet;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientSiparis;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientZiyaret;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsCancelDataResponse;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsSiparisResponse;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;

public class SiparisFragment extends Fragment implements SiparisAdapter.OrderClickListener {
    final int[] selectedItem = {0}, selectedMenu = {0};
    DatabaseHandler databaseHandler;
    FloatingActionButton fabSiparis;
    RecyclerView recyclerView;
    View view;
    SiparisAdapter adapter;
    List<ClientSiparis> siparisList = new ArrayList<>();
    List<ClientSepet> sepetList = new ArrayList<>();
    List<CihazlarFirmaParametreler> parametrelerList = new ArrayList<>();
    List<Integer> taskList = new ArrayList<>();
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    SessionManagement session;
    String KurusHaneSayisiStokTutar, KurusHaneSayisiStokMiktar, ziyaretSistemiKullanimi, CariIslemlerToptanSatisFaturasi,
            CariIslemlerToptanSatisIadeFaturasi, CariIslemlerPerakendeSatisFaturasi, CariIslemlerPerakendeSatisIadeFaturasi,
            CariIslemlerAlinanSiparis, CariIslemlerVerilenSiparis, CariIslemlerAlimFaturasi, CariIslemlerAlimIadeFaturasi,
            CariIslemlerSayimFisi, CariIslemlerTalepFisi;
    private String date1, date2;
    Calendar myCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_siparis, container, false);
        session = new SessionManagement(getActivity());
        webSettingsMap = session.getWebSettings();
        userSettingMap = session.getUserDetails();
        databaseHandler = DatabaseHandler.getInstance(getActivity());

        initViews();
        return view;
    }

    @Override
    public void orderClick(int position) {
        chooseMenuOptions(position);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.clients_visit_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.client_visit_filter) {
            showFilterDialog();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 88) {
            myCalendar = Calendar.getInstance();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            date1 = dateFormat.format(date);
            date2 = dateFormat.format(date);
            siparisList = databaseHandler.selectAllSiparis(clientKayitNo, date1, date2);
            adapter.setOrderList(siparisList);
        }
    }

    private class GetDataFromDatabase extends AsyncTask<String, Void, Void> {
        RelativeLayout products_progressBar = view.findViewById(R.id.siparis_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
            siparisList.clear();
        }

        @Override
        protected Void doInBackground(String... items) {
            siparisList = databaseHandler.selectAllSiparis(clientKayitNo, items[0], items[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.setOrderList(siparisList);
            products_progressBar.setVisibility(View.GONE);
        }
    }

    private class SendDataToServer extends AsyncTask<Integer, String, Void> {
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        RelativeLayout products_progressBar = getActivity().findViewById(R.id.siparis_progressBar_layout);
        RetrofitApi retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
        Call<ClientsSiparisResponse> queryList;
        ClientsSiparisResponse query;
        boolean isFailed = false;
        ClientZiyaret ziyaret;
        ClCard card;
        String ziyaretSatiri;
        String errorMessage;
        String hata = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
            if (clientKayitNo != -1) {
                card = databaseHandler.selectClientById(clientKayitNo);
            }
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            sepetList = databaseHandler.selectAllSepet(siparisList.get(integers[0]).getKayitNo());
            StringBuilder satir = new StringBuilder();

            if (ziyaretSistemiKullanimi.equals("1")) {
                ziyaret = databaseHandler.selectClientByOpenZiyaret(session.getKeyVisit().intValue());
                ziyaretSatiri =
                        ziyaret.getKayitNo() + "|" +
                                ziyaret.getCariKayitNo() + "|" +
                                ziyaret.getBeginKordinatLatitute() + "|" +
                                ziyaret.getBeginKordinatLongitude() + "|" +
                                ziyaret.getEndKordinatLatitute() + "|" +
                                ziyaret.getEndKordinatLongitude() + "|" +
                                ziyaret.getBaslangicTarihi() + "|" +
                                ziyaret.getBaslangicSaati() + "|" +
                                ziyaret.getBitisTarihi() + "|" +
                                ziyaret.getBitisSaati() + "|" +
                                card.getKod() + "|" +
                                ziyaret.getNotlar();
            } else {
                ziyaretSatiri = "";
            }

            for (ClientSepet sepet : sepetList) {
                satir
                        .append(sepet.getKayitNo()).append("|")
                        .append(sepet.getStokKayitNo()).append("|")
                        .append(sepet.getStokKodu()).append("|")
                        .append(sepet.getStokAdi()).append("|")
                        .append(0).append("|")
                        .append(0).append("|")
                        .append(0).append("|")
                        .append(sepet.getStokMiktar()).append("|")
                        .append(sepet.getStokFiyat()).append("|")
                        .append(sepet.getStokTutar()).append("|")
                        .append(sepet.getStokBirim()).append("|")
                        .append(0).append("|")
                        .append(0).append("|")
                        .append(0).append("|")
                        .append(sepet.getNetTutar()).append("|")
                        .append(siparisList.get(integers[0]).getTarih()).append("|")
                        .append("\\n");
            }
            String baslik = siparisList.get(integers[0]).getKayitNo() + "|" +
                    siparisList.get(integers[0]).getIslemTipi() + "|" +
                    0 + "|" +
                    siparisList.get(integers[0]).getErpSiparisFisNo() + "|" +
                    siparisList.get(integers[0]).getTarih() + "|" +
                    siparisList.get(integers[0]).getCariKayitNo() + "|" +
                    siparisList.get(integers[0]).getCariKod() + "|" +
                    siparisList.get(integers[0]).getTarih() + "|" +
                    0 + "|" +
                    "" + "|" +
                    siparisList.get(integers[0]).getKordinatLatitute() + "|" +
                    siparisList.get(integers[0]).getKordinatLongitude() + "|" +
                    "" + "|" +
                    "" + "|" +
                    "" + "|" +
                    siparisList.get(integers[0]).getTutar() + "|" +
                    siparisList.get(integers[0]).getGenelIndirimOrani() + "|" +
                    siparisList.get(integers[0]).getGenelIndirimTutari() + "|" +
                    0 + "|" +
                    siparisList.get(integers[0]).getNetTutar()
                    + "|" + siparisList.get(integers[0]).getEklenmeSaati()
                    + "|" + siparisList.get(integers[0]).getDegisiklikSaati();

            queryList = retrofitApi.siparisHareketleriGuncelle(
                    phoneId,
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD),
                    FIRMA_NO,
                    DONEM_NO,
                    1,
                    baslik,
                    satir.toString(),
                    ziyaretSatiri
            );
            try {
                Response<ClientsSiparisResponse> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getHata()) {
                        errorMessage = response.body().getMesaj();
                        ClientSiparis siparis = new ClientSiparis();
                        siparisList.get(integers[0]).setErpGonderildi(1);
                        siparisList.get(integers[0]).setErpKayitNo(response.body().getSiparisKayitNo());
                        siparisList.get(integers[0]).setErpSiparisFisNo(response.body().getSiparisFisNo());
                        adapter.notifyItemChanged(integers[0]);
                        siparis.setErpSiparisFisNo(response.body().getSiparisFisNo());
                        siparis.setErpKayitNo(response.body().getSiparisKayitNo());
                        siparis.setErpGonderildi(1);
                        databaseHandler.updateSiparisIslemleriErpGonder(siparis, String.valueOf(siparisList.get(integers[0]).getKayitNo()));
                    } else {
                        isFailed = true;
                    }
                }
            } catch (Exception e) {
                hata = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            products_progressBar.setVisibility(View.GONE);
            if (!hata.isEmpty()) {
                sendDataToServerFailDialog(hata);
            }
            if (isFailed) {
                sendDataToServerFailDialog();
            }
        }
    }

    private class CancelDataToServer extends AsyncTask<Integer, String, Void> {
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        RelativeLayout products_progressBar = getActivity().findViewById(R.id.siparis_progressBar_layout);
        RetrofitApi retrofitApi;
        Call<ClientsCancelDataResponse> queryList;
        ClientsCancelDataResponse query;
        boolean isFailed = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
            queryList = retrofitApi.geriAlIslemleri(
                    phoneId,
                    FIRMA_NO,
                    DONEM_NO,
                    2,
                    siparisList.get(integers[0]).getErpSiparisFisNo(),
                    siparisList.get(integers[0]).getTarih(),
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD),
                    siparisList.get(integers[0]).getErpKayitNo(),
                    siparisList.get(integers[0]).getCariKayitNo(),
                    siparisList.get(integers[0]).getTutar()
            );
            try {
                Response<ClientsCancelDataResponse> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    query = response.body();
                    if (!query.getHata()) {
                        ClientSiparis siparis = new ClientSiparis();
                        siparisList.get(integers[0]).setErpGonderildi(0);
                        siparisList.get(integers[0]).setErpKayitNo(0);
                        siparisList.get(integers[0]).setErpSiparisFisNo("");
                        adapter.notifyItemChanged(integers[0]);
                        siparis.setErpSiparisFisNo("");
                        siparis.setErpKayitNo(0);
                        siparis.setErpGonderildi(0);
                        databaseHandler.updateSiparisIslemleriErpGonder(siparis, String.valueOf(siparisList.get(integers[0]).getKayitNo()));
                    } else {
                        isFailed = true;
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
            products_progressBar.setVisibility(View.GONE);
            if (isFailed) {
                sendDataToServerFailDialog();
            }
        }
    }

    private void initViews() {
        parametrelerList = databaseHandler.selectParametreList(FIRMA_NO);
        KurusHaneSayisiStokTutar = parametreGetir("KurusHaneSayisiStokTutar");
        KurusHaneSayisiStokMiktar = parametreGetir("KurusHaneSayisiStokMiktar");
        ziyaretSistemiKullanimi = parametreGetir("ZiyaretSistemiKullanimi");
        CariIslemlerToptanSatisFaturasi = parametreGetir("CariIslemlerToptanSatisFaturasi");
        CariIslemlerToptanSatisIadeFaturasi = parametreGetir("CariIslemlerToptanSatisIadeFaturasi");
        CariIslemlerPerakendeSatisFaturasi = parametreGetir("CariIslemlerPerakendeSatisFaturasi");
        CariIslemlerPerakendeSatisIadeFaturasi = parametreGetir("CariIslemlerPerakendeSatisIadeFaturasi");
        CariIslemlerAlinanSiparis = parametreGetir("CariIslemlerAlinanSiparis");
        CariIslemlerVerilenSiparis = parametreGetir("CariIslemlerVerilenSiparis");
        CariIslemlerAlimFaturasi = parametreGetir("CariIslemlerAlimFaturasi");
        CariIslemlerAlimIadeFaturasi = parametreGetir("CariIslemlerAlimIadeFaturasi");
        CariIslemlerSayimFisi = parametreGetir("CariIslemlerSayimFisi");
        CariIslemlerTalepFisi = parametreGetir("CariIslemlerTalepFisi");

        if (CariIslemlerToptanSatisFaturasi.equals("1")) {
            taskList.add(8);
        }
        if (CariIslemlerToptanSatisIadeFaturasi.equals("1")) {
            taskList.add(3);
        }
        if (CariIslemlerPerakendeSatisFaturasi.equals("1")) {
            taskList.add(7);
        }
        if (CariIslemlerPerakendeSatisIadeFaturasi.equals("1")) {
            taskList.add(2);
        }
        if (CariIslemlerAlinanSiparis.equals("1")) {
            taskList.add(108);
        }
        if (CariIslemlerVerilenSiparis.equals("1")) {
            taskList.add(101);
        }
        if (CariIslemlerAlimFaturasi.equals("1")) {
            taskList.add(1);
        }
        if (CariIslemlerAlimIadeFaturasi.equals("1")) {
            taskList.add(6);
        }
        if (CariIslemlerTalepFisi.equals("1")) {
            taskList.add(200);
        }
        if (CariIslemlerSayimFisi.equals("1")) {
            taskList.add(201);
        }


        recyclerView = view.findViewById(R.id.siparisRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = new SiparisAdapter(getActivity(), siparisList, this, Integer.parseInt(KurusHaneSayisiStokTutar));
        recyclerView.setAdapter(adapter);
        fabSiparis = view.findViewById(R.id.fabSiparis);
        fabSiparis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ziyaretSistemiKullanimi.equals("1")) {
                    if (session.getKeyVisit() != -1) {
                        chooseOrderOperation();
                    } else {
                        startVisitDialog();
                    }
                } else {
                    chooseOrderOperation();
                }
            }
        });

        setHasOptionsMenu(true);
        myCalendar = Calendar.getInstance();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        date1 = dateFormat.format(date);
        date2 = dateFormat.format(date);

        new GetDataFromDatabase().execute(date1, date2);
    }

    private void chooseOrderOperation() {
        String[] items = new String[taskList.size()];
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i) == 8) {
                items[i] = getString(R.string.alert_siparis_sale_invoice);
            }
            if (taskList.get(i) == 3) {
                items[i] = getString(R.string.alert_siparis_sale_return_invoice);
            }
            if (taskList.get(i) == 7) {
                items[i] = getString(R.string.alert_siparis_sale_retail);
            }
            if (taskList.get(i) == 2) {
                items[i] = getString(R.string.alert_siparis_sale_retail_return);
            }
            if (taskList.get(i) == 108) {
                items[i] = getString(R.string.alert_siparis_purchases);
            }
            if (taskList.get(i) == 101) {
                items[i] = getString(R.string.alert_siparis_sales);
            }
            if (taskList.get(i) == 1) {
                items[i] = getString(R.string.alert_siparis_purchase_invoice);
            }
            if (taskList.get(i) == 6) {
                items[i] = getString(R.string.alert_siparis_purchase_return_invoice);
            }
            if (taskList.get(i) == 200) {
                items[i] = getString(R.string.alert_siparis_request_slip);
            }
            if (taskList.get(i) == 201) {
                items[i] = getString(R.string.alert_siparis_inventory);
            }
        }
        /*String[] items = new String[]{
                getString(R.string.alert_siparis_purchase_invoice),
                getString(R.string.alert_siparis_purchase_return_invoice),
                getString(R.string.alert_siparis_sale_invoice),
                getString(R.string.alert_siparis_sale_return_invoice),
                getString(R.string.alert_siparis_purchases),
                getString(R.string.alert_siparis_sales),
                getString(R.string.alert_siparis_request_slip),
                getString(R.string.alert_siparis_inventory)
        };*/
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.alert_kasa_title_choose);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_input);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), SiparisIslemleriActivity.class);
                intent.putExtra("islemTuru", taskList.get(selectedMenu[0]));
                intent.putExtra("kayitNo", clientKayitNo);
                intent.putExtra("KurusHaneSayisiStokMiktar", KurusHaneSayisiStokMiktar);
                intent.putExtra("KurusHaneSayisiStokTutar", KurusHaneSayisiStokTutar);
                startActivityForResult(intent, 88);
            }
        });
        builder.setNegativeButton(R.string.login_internet_connection_btnCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setSingleChoiceItems(items, selectedMenu[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedMenu[0] = which;
            }
        });
        builder.show();
    }

    private void chooseMenuOptions(int position) {
        String[] items = new String[]{
                getString(R.string.alert_kasa_menu_edit),
                getString(R.string.alert_kasa_menu_view),
                getString(R.string.alert_kasa_menu_delete),
                getString(R.string.alert_kasa_menu_erp_gonder),
                getString(R.string.alert_kasa_menu_erp_gerial),
                getString(R.string.alert_kasa_menu_share)};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.alert_kasa_title_choose);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_menu);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (selectedItem[0]) {
                    case 0:
                        if (ziyaretSistemiKullanimi.equals("1")) {
                            if (session.getKeyVisit() != -1) {
                                if (siparisList.get(position).getErpGonderildi() < 1) {
                                    Intent intent = new Intent(getActivity(), SiparisIslemleriActivity.class);
                                    intent.putExtra("islemTuru", selectedItem[0]);
                                    intent.putExtra("kayitNo", siparisList.get(position).getCariKayitNo());
                                    intent.putExtra("updateCart", 1);
                                    intent.putExtra("siparisNo", siparisList.get(position).getKayitNo());
                                    intent.putExtra("discountTotal", siparisList.get(position).getGenelIndirimTutari());
                                    intent.putExtra("discountPercent", siparisList.get(position).getGenelIndirimOrani());
                                    intent.putExtra("KurusHaneSayisiStokMiktar", KurusHaneSayisiStokMiktar);
                                    intent.putExtra("KurusHaneSayisiStokTutar", KurusHaneSayisiStokTutar);
                                    startActivityForResult(intent, 88);
                                } else {
                                    sendDataFailDialog();
                                }
                            } else {
                                closeVisitDialog();
                            }
                        } else {
                            if (siparisList.get(position).getErpGonderildi() < 1) {
                                Intent intent = new Intent(getActivity(), SiparisIslemleriActivity.class);
                                intent.putExtra("islemTuru", selectedItem[0]);
                                intent.putExtra("kayitNo", siparisList.get(position).getCariKayitNo());
                                intent.putExtra("updateCart", 1);
                                intent.putExtra("siparisNo", siparisList.get(position).getKayitNo());
                                intent.putExtra("discountTotal", siparisList.get(position).getGenelIndirimTutari());
                                intent.putExtra("discountPercent", siparisList.get(position).getGenelIndirimOrani());
                                intent.putExtra("KurusHaneSayisiStokMiktar", KurusHaneSayisiStokMiktar);
                                intent.putExtra("KurusHaneSayisiStokTutar", KurusHaneSayisiStokTutar);
                                startActivityForResult(intent, 88);
                            } else {
                                sendDataFailDialog();
                            }
                        }
                        break;
                    case 1:
                        Intent intent = new Intent(getActivity(), SiparisIslemleriActivity.class);
                        intent.putExtra("islemTuru", 2);
                        intent.putExtra("updateCart", 0);
                        intent.putExtra("kayitNo", siparisList.get(position).getCariKayitNo());
                        intent.putExtra("siparisNo", siparisList.get(position).getKayitNo());
                        intent.putExtra("discountTotal", siparisList.get(position).getGenelIndirimTutari());
                        intent.putExtra("discountPercent", siparisList.get(position).getGenelIndirimOrani());
                        intent.putExtra("KurusHaneSayisiStokMiktar", KurusHaneSayisiStokMiktar);
                        intent.putExtra("KurusHaneSayisiStokTutar", KurusHaneSayisiStokTutar);
                        startActivityForResult(intent, 99);
                        break;
                    case 2:
                        if (ziyaretSistemiKullanimi.equals("1")) {
                            if (session.getKeyVisit() != -1) {
                                if (siparisList.get(position).getErpGonderildi() < 1) {
                                    deleteCurrentItem(position);
                                } else {
                                    sendDataFailDialog();
                                }
                            } else {
                                closeVisitDialog();
                            }
                        } else {
                            if (siparisList.get(position).getErpGonderildi() < 1) {
                                deleteCurrentItem(position);
                            } else {
                                sendDataFailDialog();
                            }
                        }
                        break;
                    case 3:
                        if (ziyaretSistemiKullanimi.equals("1")) {
                            if (session.getKeyVisit() != -1) {
                                if (siparisList.get(position).getErpGonderildi() < 1) {
                                    new SendDataToServer().execute(position);
                                } else {
                                    sendDataFailDialog();
                                }
                            } else {
                                closeVisitDialog();
                            }
                        } else {
                            if (siparisList.get(position).getErpGonderildi() < 1) {
                                new SendDataToServer().execute(position);
                            } else {
                                sendDataFailDialog();
                            }
                        }
                        break;
                    case 4:
                        if (ziyaretSistemiKullanimi.equals("1")) {
                            if (session.getKeyVisit() != -1) {
                                if (siparisList.get(position).getErpGonderildi() > 0) {
                                    new CancelDataToServer().execute(position);
                                }
                            } else {
                                closeVisitDialog();
                            }
                        } else {
                            if (siparisList.get(position).getErpGonderildi() > 0) {
                                new CancelDataToServer().execute(position);
                            }
                        }
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

    private void deleteCurrentItem(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.alert_kasa_delete_task_title);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_confirmation);
        builder.setMessage(R.string.alert_kasa_delete_task_message);
        builder.setPositiveButton(R.string.alert_exit_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHandler.deleteSiparis(String.valueOf(siparisList.get(position).getKayitNo()));
                databaseHandler.deleteSepetBySiparisNo(String.valueOf(siparisList.get(position).getKayitNo()));
                siparisList.remove(position);
                adapter.notifyDataSetChanged();
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

    private void sendDataFailDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.info_warning_title);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_dangerous);
        builder.setMessage(R.string.info_warning_message);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void sendDataToServerFailDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.info_warning_title);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_dangerous);
        builder.setMessage(R.string.info_warning_sendto_server_fail);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void sendDataToServerFailDialog(String errorMessage) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
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

    private String parametreGetir(String parametre) {
        String parametreDeger = "0";
        for (CihazlarFirmaParametreler parametreler : parametrelerList) {
            if (parametreler.getParametreAdi().equals(parametre)) {
                parametreDeger = parametreler.getParametreDegeri();
            }
        }
        return parametreDeger;
    }

    private void startVisitDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.info_warning_title);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_dangerous);
        builder.setMessage(R.string.client_visit_start_visit_before_adding);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void closeVisitDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.info_warning_title);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_dangerous);
        builder.setMessage(R.string.client_visit_start_visit_before_adding);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showFilterDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
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
                new DatePickerDialog(getActivity(), datePicker1,
                        Integer.parseInt(date1Array[0]),
                        Integer.parseInt(date1Array[1]) - 1,
                        Integer.parseInt(date1Array[2])).show();
            }
        });
        editDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] date1Array = date2.split("-");
                new DatePickerDialog(getActivity(), datePicker2,
                        Integer.parseInt(date1Array[0]),
                        Integer.parseInt(date1Array[1]) - 1,
                        Integer.parseInt(date1Array[2])).show();
            }
        });
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new GetDataFromDatabase().execute(date1, date2);
            }
        });
        builder.show();
    }

    private void responseInfoDialog(String info) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.info_warning_title);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_dangerous);
        builder.setMessage(info);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}