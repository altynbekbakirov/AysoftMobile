package kz.burhancakmak.aysoftmobile.Clients;

import static android.app.Activity.RESULT_OK;
import static kz.burhancakmak.aysoftmobile.Clients.ClientsTasksActivity.clientKayitNo;
import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Adapters.KasaAdapter;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientKasa;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientZiyaret;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsCancelDataResponse;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsKasaResponse;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;

public class KasaFragment extends Fragment implements KasaAdapter.OnKasaListener {
    KasaAdapter clientsAdapter;
    List<ClientKasa> kasaList = new ArrayList<>();
    List<CihazlarFirmaParametreler> parametrelerList = new ArrayList<>();
    List<Integer> taskList = new ArrayList<>();
    final int[] selectedItem = {0}, selectedMenu = {0};
    View view;
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    RelativeLayout layout;
    DatabaseHandler databaseHandler;
    HashMap<String, String> webSettingsMap;
    HashMap<String, String> userSettingMap;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    SessionManagement session;
    String kurusHaneSayisiStokTutar, ziyaretSistemiKullanimi, CariIslemlerKasaTahsilatIslemi,
            CariIslemlerKasaOdemeIslemi, CariIslemlerKasaMasrafTahsilatIslemi, CariIslemlerKasaMasrafOdemeIslemi;
    private String date1, date2;
    Calendar myCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kasa, container, false);
        session = new SessionManagement(getActivity());
        webSettingsMap = session.getWebSettings();
        userSettingMap = session.getUserDetails();
        databaseHandler = DatabaseHandler.getInstance(getActivity());

        initViews();
        return view;
    }

    @Override
    public void onItemClick(int position) {
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
        int kayitNo, position;
        String makbuzNo, aciklama, tarih, eklenmeSaati, degisiklikSaati;
        double tutar;
        int islemTuru;
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                makbuzNo = data.getStringExtra("makbuzNo");
                aciklama = data.getStringExtra("aciklama");
                tarih = data.getStringExtra("tarih");
                eklenmeSaati = data.getStringExtra("eklenmeSaati");
                degisiklikSaati = data.getStringExtra("degisiklikSaati");
                tutar = data.getDoubleExtra("tutar", -1);
                islemTuru = data.getIntExtra("islemTuru", -1);
                if (tarih != null && aciklama != null) {
                    addItem(tarih, tutar, makbuzNo, aciklama, islemTuru, eklenmeSaati, degisiklikSaati);
                }
            }
            if (requestCode == 102) {
                position = data.getIntExtra("position", -1);
                kayitNo = data.getIntExtra("kayitNo", -1);
                makbuzNo = data.getStringExtra("makbuzNo");
                aciklama = data.getStringExtra("aciklama");
                tarih = data.getStringExtra("tarih");
                eklenmeSaati = data.getStringExtra("eklenmeSaati");
                degisiklikSaati = data.getStringExtra("degisiklikSaati");
                tutar = data.getDoubleExtra("tutar", -1);
                if (kayitNo != -1) {
                    updateItem(tarih, tutar, makbuzNo, aciklama, kayitNo, position, eklenmeSaati, degisiklikSaati);
                }
            }
        }
    }

    private void initViews() {
        parametrelerList = databaseHandler.selectParametreList(FIRMA_NO);
        kurusHaneSayisiStokTutar = parametreGetir("KurusHaneSayisiStokTutar", "0");
        ziyaretSistemiKullanimi = parametreGetir("ZiyaretSistemiKullanimi", "0");
        CariIslemlerKasaTahsilatIslemi = parametreGetir("CariIslemlerKasaTahsilatIslemi", "0");
        CariIslemlerKasaOdemeIslemi = parametreGetir("CariIslemlerKasaOdemeIslemi", "0");
        CariIslemlerKasaMasrafTahsilatIslemi = parametreGetir("CariIslemlerKasaMasrafTahsilatIslemi", "0");
        CariIslemlerKasaMasrafOdemeIslemi = parametreGetir("CariIslemlerKasaMasrafOdemeIslemi", "0");

        if (CariIslemlerKasaTahsilatIslemi.equals("1")) {
            taskList.add(11);
        }
        if (CariIslemlerKasaOdemeIslemi.equals("1")) {
            taskList.add(12);
        }
        if (CariIslemlerKasaMasrafTahsilatIslemi.equals("1")) {
            taskList.add(111);
        }
        if (CariIslemlerKasaMasrafOdemeIslemi.equals("1")) {
            taskList.add(112);
        }

        layout = view.findViewById(R.id.products_snackbar_relativelayout);
        recyclerView = view.findViewById(R.id.kasaRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        clientsAdapter = new KasaAdapter(kasaList, this, Integer.parseInt(kurusHaneSayisiStokTutar));
        recyclerView.setAdapter(clientsAdapter);
        floatingActionButton = view.findViewById(R.id.fabKasa);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ziyaretSistemiKullanimi.equals("1")) {
                    if (session.getKeyVisit() != -1) {
                        chooseKasaOperation();
                    } else {
                        startVisitDialog();
                    }
                } else {
                    chooseKasaOperation();
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

    private class GetDataFromDatabase extends AsyncTask<String, Void, Void> {
        RelativeLayout products_progressBar = view.findViewById(R.id.products_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
            kasaList.clear();
        }

        @Override
        protected Void doInBackground(String... items) {
            kasaList = databaseHandler.selectClientKasa(clientKayitNo, date1, date2);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            clientsAdapter.setKasaList(kasaList);
            products_progressBar.setVisibility(View.GONE);
        }
    }

    private class SendDataToServer extends AsyncTask<Integer, String, Void> {
        RelativeLayout products_progressBar = getActivity().findViewById(R.id.products_progressBar_layout);
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        RetrofitApi retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
        Call<ClientsKasaResponse> list;
        ClientsKasaResponse query;
        ClCard card;
        ClientZiyaret ziyaret;
        boolean isFailed = false;
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
            ziyaret = databaseHandler.selectClientByOpenZiyaret(kasaList.get(integers[0]).getZiyaretKayitNo());
            String ziyaretSatiri;

            if (ziyaretSistemiKullanimi.equals("1")) {
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

            list = retrofitApi.kasaHareketleriGuncelle(
                    phoneId,
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD),
                    String.valueOf(FIRMA_NO),
                    DONEM_NO,
                    kasaList.get(integers[0]).getIslemTipi(),
                    kasaList.get(integers[0]).getKayitNo(),
                    kasaList.get(integers[0]).getTarih(),
                    kasaList.get(integers[0]).getCariKayitNo(),
                    card.getKod(),
                    kasaList.get(integers[0]).getTutar() < 0 ? -(kasaList.get(integers[0]).getTutar()) : kasaList.get(integers[0]).getTutar(),
                    kasaList.get(integers[0]).getAciklama(),
                    kasaList.get(integers[0]).getMakbuzNo(),
                    kasaList.get(integers[0]).getKasaKodu(),
                    ziyaretSatiri,
                    kasaList.get(integers[0]).getEklenmeSaati(),
                    kasaList.get(integers[0]).getDegisiklikSaati()
            );
            try {
                Response<ClientsKasaResponse> response = list.execute();
                if (response.isSuccessful() && response.body() != null) {
                    query = response.body();
                    if (!query.getHata()) {
                        ClientKasa kasa = new ClientKasa();
                        kasaList.get(integers[0]).setErpGonderildi(1);
                        kasaList.get(integers[0]).setErpKayitNo(query.getKasaHareketKayitNo());
                        kasaList.get(integers[0]).setErpFisNo(query.getFisNo());
                        clientsAdapter.notifyItemChanged(integers[0]);
                        kasa.setErpFisNo(query.getFisNo());
                        kasa.setErpKayitNo(query.getKasaHareketKayitNo());
                        kasa.setErpGonderildi(1);
                        databaseHandler.updateKasaIslemleriErpGonder(kasa, String.valueOf(kasaList.get(integers[0]).getKayitNo()));
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
        RelativeLayout products_progressBar = getActivity().findViewById(R.id.products_progressBar_layout);
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        RetrofitApi retrofitApi;
        Call<ClientsCancelDataResponse> queryList;
        boolean isFailed = false;
        ClientsCancelDataResponse query;

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
                    1,
                    kasaList.get(integers[0]).getErpFisNo(),
                    kasaList.get(integers[0]).getTarih(),
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD),
                    kasaList.get(integers[0]).getErpKayitNo(),
                    kasaList.get(integers[0]).getCariKayitNo(),
                    kasaList.get(integers[0]).getTutar() < 0 ? -(kasaList.get(integers[0]).getTutar()) : kasaList.get(integers[0]).getTutar()
            );
            try {
                Response<ClientsCancelDataResponse> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    query = response.body();
                    if (!query.getHata()) {
                        ClientKasa kasa = new ClientKasa();
                        kasaList.get(integers[0]).setErpGonderildi(0);
                        kasaList.get(integers[0]).setErpKayitNo(0);
                        kasaList.get(integers[0]).setErpFisNo("");
                        clientsAdapter.notifyItemChanged(integers[0]);
                        kasa.setErpFisNo("");
                        kasa.setErpKayitNo(0);
                        kasa.setErpGonderildi(0);
                        databaseHandler.updateKasaIslemleriErpGonder(kasa, String.valueOf(kasaList.get(integers[0]).getKayitNo()));
                    } else {
                        isFailed = true;
                    }
                }
            } catch (IllegalStateException | JsonSyntaxException | IOException e) {
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
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_menu);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (selectedItem[0]) {
                    case 0:
                        if (ziyaretSistemiKullanimi.equals("1")) {
                            if (session.getKeyVisit() != -1 && session.getKeyVisit() == kasaList.get(position).getZiyaretKayitNo().intValue()) {
                                if (kasaList.get(position).getErpGonderildi() < 1) {
                                    Intent intent = new Intent(getActivity(), KasaIslemleriActivity.class);
                                    intent.putExtra("position", position);
                                    intent.putExtra("kayitNo", kasaList.get(position).getKayitNo());
                                    intent.putExtra("makbuzNo", kasaList.get(position).getMakbuzNo());
                                    intent.putExtra("aciklama", kasaList.get(position).getAciklama());
                                    intent.putExtra("tarih", kasaList.get(position).getTarih());
                                    intent.putExtra("tutar", kasaList.get(position).getTutar());
                                    startActivityForResult(intent, 102);
                                } else {
                                    sendDataFailDialog();
                                }
                            } else {
                                closeVisitDialog();
                            }
                        } else {
                            if (kasaList.get(position).getErpGonderildi() < 1) {
                                Intent intent = new Intent(getActivity(), KasaIslemleriActivity.class);
                                intent.putExtra("position", position);
                                intent.putExtra("kayitNo", kasaList.get(position).getKayitNo());
                                intent.putExtra("makbuzNo", kasaList.get(position).getMakbuzNo());
                                intent.putExtra("aciklama", kasaList.get(position).getAciklama());
                                intent.putExtra("tarih", kasaList.get(position).getTarih());
                                intent.putExtra("tutar", kasaList.get(position).getTutar());
                                startActivityForResult(intent, 102);
                            } else {
                                sendDataFailDialog();
                            }
                        }
                        break;
                    case 1:
                        Intent intent = new Intent(getActivity(), KasaIslemleriActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("kayitNo", kasaList.get(position).getKayitNo());
                        intent.putExtra("makbuzNo", kasaList.get(position).getMakbuzNo());
                        intent.putExtra("aciklama", kasaList.get(position).getAciklama());
                        intent.putExtra("tarih", kasaList.get(position).getTarih());
                        intent.putExtra("tutar", kasaList.get(position).getTutar());
                        intent.putExtra("incele", 1);
                        startActivityForResult(intent, 202);
                        break;
                    case 2:
                        if (ziyaretSistemiKullanimi.equals("1")) {
                            if (session.getKeyVisit() != -1 && session.getKeyVisit() == kasaList.get(position).getZiyaretKayitNo().intValue()) {
                                if (kasaList.get(position).getErpGonderildi() < 1) {
                                    deleteCurrentItem(position);
                                } else {
                                    sendDataFailDialog();
                                }
                            } else {
                                closeVisitDialog();
                            }
                        } else {
                            if (kasaList.get(position).getErpGonderildi() < 1) {
                                deleteCurrentItem(position);
                            } else {
                                sendDataFailDialog();
                            }
                        }
                        break;
                    case 3:
                        if (ziyaretSistemiKullanimi.equals("1")) {
                            if (session.getKeyVisit() != -1 && session.getKeyVisit() == kasaList.get(position).getZiyaretKayitNo().intValue()) {
                                closeVisitBeforeDialog();
                            } else {
                                if (kasaList.get(position).getErpGonderildi() < 1) {
                                    new SendDataToServer().execute(position);
                                } else {
                                    sendDataFailDialog();
                                }
                            }
                        } else {
                            if (kasaList.get(position).getErpGonderildi() < 1) {
                                new SendDataToServer().execute(position);
                            } else {
                                sendDataFailDialog();
                            }
                        }
                        break;
                    case 4:
                        if (ziyaretSistemiKullanimi.equals("1")) {
                            if (session.getKeyVisit() != -1 && session.getKeyVisit() == kasaList.get(position).getZiyaretKayitNo().intValue()) {
                                if (kasaList.get(position).getErpGonderildi() > 0) {
                                    closeVisitBeforeDialog();
                                }
                            } else {
                                new CancelDataToServer().execute(position);
                            }
                        } else {
                            if (kasaList.get(position).getErpGonderildi() > 0) {
                                new CancelDataToServer().execute(position);
                            }
                        }
                        break;
                    case 5:
                        chooseShareOptions(position);
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

    private void chooseShareOptions(int position) {
        int[] item = {0};
        String[] items = new String[]{
                getString(R.string.alert_kasa_menu_share_whatsapp),
                getString(R.string.alert_kasa_menu_share_telegram)};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.alert_kasa_menu_share);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_share);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (item[0]) {
                    case 0:
                        if (!isAppInstalled("com.whatsapp")) {
                            Toast.makeText(getActivity(), R.string.alert_kasa_menu_share_whatsapp_not_installed, Toast.LENGTH_SHORT).show();
                        } else {
                            ClCard card = databaseHandler.selectClientById(kasaList.get(position).getCariKayitNo());
                            String phonestr = card.getTelefon1();
                            phonestr = stripNonDigits(phonestr);
                            if (phonestr.isEmpty()) {
                                Toast.makeText(getActivity(), R.string.alert_kasa_menu_share_whatsapp_non_number, Toast.LENGTH_SHORT).show();
                            } else {
                                if (phonestr.length() == 10) {
                                    phonestr = "7" + phonestr;
                                }
                                String messagestr = getString(R.string.client_kasa_date_title) + ": " + kasaList.get(position).getTarih() + "\n"
                                        + getString(R.string.client_kasa_sum_title) + ": " + kasaList.get(position).getTutar() + "\n"
                                        + getString(R.string.client_kasa_definition_title) + ": " + kasaList.get(position).getAciklama();
                                if (!kasaList.get(position).getMakbuzNo().isEmpty()) {
                                    messagestr += "\n" + getString(R.string.client_kasa_ficheno_title) + ": " + kasaList.get(position).getMakbuzNo();
                                }
                                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + phonestr +
                                        "&text=" + messagestr));
                                startActivity(i);
                            }
                        }
                        break;
                    case 1:
                        if (!isAppInstalled("org.telegram.messenger")) {
                            Toast.makeText(getActivity(), R.string.alert_kasa_menu_share_telegram_not_installed, Toast.LENGTH_SHORT).show();
                        } else {
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/altynbek"));
                            i.setPackage("org.telegram.messenger");
                            startActivity(i);
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
        builder.setSingleChoiceItems(items, item[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                item[0] = which;
            }
        });
        builder.show();
    }

    private void chooseKasaOperation() {
//        String[] items = new String[]{getString(R.string.alert_kasa_collection), getString(R.string.alert_kasa_payment)};
        String[] items = new String[taskList.size()];
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i) == 11) {
                items[i] = getString(R.string.alert_kasa_collection);
            }
            if (taskList.get(i) == 12) {
                items[i] = getString(R.string.alert_kasa_payment);
            }
            if (taskList.get(i) == 111) {
                items[i] = getString(R.string.alert_kasa_collection_expense);
            }
            if (taskList.get(i) == 112) {
                items[i] = getString(R.string.alert_kasa_payment_expense);
            }
        }
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.alert_kasa_title_choose);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_input);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), KasaIslemleriActivity.class);
                intent.putExtra("islemTuru", taskList.get(selectedMenu[0]));
                startActivityForResult(intent, 101);
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

    private void addItem(String tarih, Double tutar, String makbuzNo, String aciklama, Integer islemTipi, String eklenmeSaati, String degisiklikSaati) {
        ClientKasa kasa = new ClientKasa();
        kasa.setTarih(tarih);
        kasa.setTutar(tutar);
        kasa.setMakbuzNo(makbuzNo);
        kasa.setAciklama(aciklama);
        kasa.setCariKayitNo(clientKayitNo);
        kasa.setZiyaretKayitNo(session.getKeyVisit().intValue());
        kasa.setIslemTipi(islemTipi);
        kasa.setErpGonderildi(0);
        kasa.setKapatildi(0);
        kasa.setDegisiklikSaati(degisiklikSaati);
        kasa.setEklenmeSaati(eklenmeSaati);
        long kayitno = databaseHandler.insertKasaIslemleri(kasa);
        kasa.setKayitNo(Integer.parseInt(String.valueOf(kayitno)));
        kasaList.add(kasa);
        clientsAdapter.notifyDataSetChanged();
    }

    private void updateItem(String tarih, Double tutar, String makbuzNo, String aciklama, int kayitNo, int position, String eklenmeSaati, String degisiklikSaati) {
        ClientKasa kasa = new ClientKasa();
        kasa.setTarih(tarih);
        kasa.setTutar(tutar);
        kasa.setMakbuzNo(makbuzNo);
        kasa.setAciklama(aciklama);
        kasa.setEklenmeSaati(eklenmeSaati);
        kasa.setDegisiklikSaati(degisiklikSaati);
        databaseHandler.updateKasaIslemleri(kasa, String.valueOf(kayitNo));
        kasaList.get(position).setAciklama(aciklama);
        kasaList.get(position).setMakbuzNo(makbuzNo);
        kasaList.get(position).setTutar(tutar);
        kasaList.get(position).setTarih(tarih);
        clientsAdapter.notifyItemChanged(position);
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
                databaseHandler.deleteKasaIslemleri(String.valueOf(kasaList.get(position).getKayitNo()));
                kasaList.remove(position);
                clientsAdapter.notifyDataSetChanged();
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

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(layout, "Islemler toplami", Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);
        View snackView = getLayoutInflater().inflate(R.layout.fragment_kasa_snackbar_layout, null);
        TextView textViewTop = (TextView) snackView.findViewById(R.id.snackbar_header);
        TextView snackbar_title_tahsilat = (TextView) snackView.findViewById(R.id.snackbar_title_tahsilat);
        TextView snackbar_text_tahsilat = (TextView) snackView.findViewById(R.id.snackbar_text_tahsilat);
        TextView snackbar_title_odeme = (TextView) snackView.findViewById(R.id.snackbar_title_odeme);
        TextView snackbar_text_odeme = (TextView) snackView.findViewById(R.id.snackbar_text_odeme);
        ImageView imageView = (ImageView) snackView.findViewById(R.id.snackbar_action);
        textViewTop.setText("new text");
        textViewTop.setTextColor(Color.WHITE);
        layout.setPadding(0, 0, 0, 0);
        layout.addView(snackView, 0);
        snackbar.show();
    }

    private boolean isAppInstalled(String app) {
        PackageManager packageManager = getActivity().getPackageManager();
        boolean whatsappInstalled;
        try {
            packageManager.getPackageInfo(app, PackageManager.GET_ACTIVITIES);
            whatsappInstalled = true;

        } catch (PackageManager.NameNotFoundException e) {
            whatsappInstalled = false;
        }
        return whatsappInstalled;
    }

    public static String stripNonDigits(final CharSequence input) {
        final StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c > 47 && c < 58) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void openTelegram(Activity activity, String userName) {
        Intent general = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.com/" + userName));
        HashSet<String> generalResolvers = new HashSet<>();
        List<ResolveInfo> generalResolveInfo = activity.getPackageManager().queryIntentActivities(general, 0);
        for (ResolveInfo info : generalResolveInfo) {
            if (info.activityInfo.packageName != null) {
                generalResolvers.add(info.activityInfo.packageName);
            }
        }

        Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/" + userName));
        int goodResolver = 0;
        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = activity.getPackageManager().queryIntentActivities(telegram, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName != null && !generalResolvers.contains(info.activityInfo.packageName)) {
                    goodResolver++;
                    telegram.setPackage(info.activityInfo.packageName);
                }
            }
        }
        //TODO: if there are several good resolvers create custom chooser
        if (goodResolver != 1) {
            telegram.setPackage(null);
        }
        if (telegram.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(telegram);
        }
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

    private void sendDataToServerFailDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
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

    private String parametreGetir(String parametre, String deger) {
        String parametreDeger = deger;
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

    private void closeVisitBeforeDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.info_warning_title);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_dangerous);
        builder.setMessage(R.string.client_visit_end_visit_message);
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