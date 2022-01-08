package kz.burhancakmak.aysoftmobile.DataExport;

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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Adapters.DataExportSiparisAdapter;
import kz.burhancakmak.aysoftmobile.Clients.SiparisIslemleriActivity;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientSepet;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientSiparis;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsSiparisResponse;
import kz.burhancakmak.aysoftmobile.Models.DataExport.DataExportSiparisTask;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;

public class DataSiparisFragment extends Fragment implements DataExportSiparisAdapter.DataExportSiparisListener {
    SessionManagement session;
    DatabaseHandler databaseHandler;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    RecyclerView recyclerView;
    DataExportSiparisAdapter adapter;
    List<DataExportSiparisTask> listSiparis = new ArrayList<>();
    List<CihazlarFirmaParametreler> parametrelerList = new ArrayList<>();
    CheckBox cbSelectAll;
    String currentDate;
    View view;
    String KurusHaneSayisiStokTutar, KurusHaneSayisiStokMiktar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data_siparis, container, false);
        session = new SessionManagement(getActivity());
        webSettingsMap = session.getWebSettings();
        userSettingMap = session.getUserDetails();
        databaseHandler = DatabaseHandler.getInstance(getActivity());

        initViews();
        new GetOrdersFromDatabase().execute();
        return view;
    }

    private void initViews() {
        setHasOptionsMenu(true);
        cbSelectAll = view.findViewById(R.id.cbSelectAll);
        cbSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbSelectAll.isChecked()) {
                    adapter.selectAll();
                } else {
                    adapter.deSelectAll();
                }
            }
        });

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = new Date();
        currentDate = dateFormat.format(date1);

        parametrelerList = databaseHandler.selectParametreList(FIRMA_NO);
        KurusHaneSayisiStokTutar = parametreGetir("KurusHaneSayisiStokTutar", "0");
        KurusHaneSayisiStokMiktar = parametreGetir("KurusHaneSayisiStokMiktar", "0");

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = new DataExportSiparisAdapter(listSiparis, this, Integer.parseInt(KurusHaneSayisiStokTutar));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), SiparisIslemleriActivity.class);
        intent.putExtra("islemTuru", 2);
        intent.putExtra("updateCart", 0);
        intent.putExtra("kayitNo", listSiparis.get(position).getCariKayitNo());
        intent.putExtra("siparisNo", listSiparis.get(position).getKayitNo());
        intent.putExtra("discountTotal", 1);
        intent.putExtra("discountPercent", 1);
        intent.putExtra("KurusHaneSayisiStokMiktar", KurusHaneSayisiStokTutar);
        intent.putExtra("KurusHaneSayisiStokTutar", KurusHaneSayisiStokMiktar);
        startActivityForResult(intent, 99);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.data_export_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.dataUpload) {
            new UploadOrderToWeb().execute();
        }
        if (item.getItemId() == R.id.dataFilter) {
            showFilterDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    class GetOrdersFromDatabase extends AsyncTask<Void, Void, Void> {
        RelativeLayout products_progressBar = view.findViewById(R.id.dataExport_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... items) {
            listSiparis = databaseHandler.selectDataExportSiparis(currentDate);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (int i = 0; i < listSiparis.size(); i++) {
                listSiparis.get(i).setCbChecked(false);
            }
            adapter.setDataList(listSiparis);
            products_progressBar.setVisibility(View.GONE);
        }
    }

    class UploadOrderToWeb extends AsyncTask<Void, Void, Void> {
        RelativeLayout products_progressBar = view.findViewById(R.id.dataExport_progressBar_layout);
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        RetrofitApi retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
        Call<ClientsSiparisResponse> siparisList;
        List<ClientSepet> sepetList = new ArrayList<>();
        boolean isFailed = false;
        String ziyaretSatiri = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... items) {
            for (int i = 0; i < listSiparis.size(); i++) {
                if (listSiparis.get(i).getTur() == 1 && listSiparis.get(i).getErpGonderildi() < 1 && listSiparis.get(i).getCbChecked()) {
                    DataExportSiparisTask siparis = listSiparis.get(i);
                    StringBuilder satir = new StringBuilder();
                    StringBuilder baslik = new StringBuilder();
                    baslik
                            .append(siparis.getKayitNo()).append("|")
                            .append(siparis.getIslemTipi()).append("|")
                            .append(0).append("|")
                            .append(siparis.getErpFisNo()).append("|")
                            .append(siparis.getTarih()).append("|")
                            .append(siparis.getCariKayitNo()).append("|")
                            .append(siparis.getKod()).append("|")
                            .append(siparis.getTarih()).append("|")
//                    .append(siparisList.get(integers[0]).getOdemeSekli()).append("|")
                            .append(0).append("|")
//                    .append(siparisList.get(integers[0]).getAciklama()).append("|")
                            .append("").append("|")
                            .append("25.35").append("|")
                            .append("35.25").append("|")
//                    .append(siparisList.get(integers[0]).getSiparisOncesiFoto1()).append("|")
                            .append("").append("|")
//                    .append(siparisList.get(integers[0]).getSiparisOncesiFoto2()).append("|")
                            .append("").append("|")
//                    .append(siparisList.get(integers[0]).getSiparisOncesiFoto3()).append("|")
                            .append("").append("|")
                            .append(siparis.getTutar()).append("|")
//                    .append(siparisList.get(integers[0]).getGenelIndirimOrani()).append("|")
                            .append(0).append("|")
//                    .append(siparisList.get(integers[0]).getGenelIndirimTutari()).append("|")
                            .append(0).append("|")
//                    .append(siparisList.get(integers[0]).getSatirIndirimTutari()).append("|")
                            .append(0).append("|")
//                    .append(siparisList.get(integers[0]).getNetTutar());
                            .append(0);

                    sepetList = databaseHandler.selectAllSepet(siparis.getKayitNo());

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
                                .append(siparis.getTarih()).append("|")
                                .append("\\n");
                    }
                    siparisList = retrofitApi.siparisHareketleriGuncelle(
                            phoneId,
                            userSettingMap.get(KEY_NAME),
                            userSettingMap.get(KEY_PASSWORD),
                            FIRMA_NO,
                            DONEM_NO,
                            1,
                            baslik.toString(),
                            satir.toString(),
                            ziyaretSatiri
                    );
                    try {
                        Response<ClientsSiparisResponse> response = siparisList.execute();
                        if (response.isSuccessful() && response.body() != null) {
                            ClientsSiparisResponse query = response.body();
                            if (!query.getHata()) {
                                ClientSiparis order = new ClientSiparis();
                                listSiparis.get(i).setErpGonderildi(1);
                                listSiparis.get(i).setErpKayitNo(query.getSiparisKayitNo());
                                listSiparis.get(i).setErpFisNo(query.getSiparisFisNo());
                                adapter.notifyItemChanged(i);
                                order.setErpSiparisFisNo(query.getSiparisFisNo());
                                order.setErpKayitNo(query.getSiparisKayitNo());
                                order.setErpGonderildi(1);
                                databaseHandler.updateSiparisIslemleriErpGonder(order, String.valueOf(listSiparis.get(i).getKayitNo()));
                            } else {
                                isFailed = true;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            products_progressBar.setVisibility(View.GONE);
            if (isFailed) uploadDataToWebFailDialog();
        }
    }

    private void showFilterDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setCancelable(true);
        View view = getLayoutInflater().inflate(R.layout.data_export_filter_layout, null);
        builder.setView(view);

        EditText dataDialogDate = view.findViewById(R.id.dataDialogDate);
        dataDialogDate.setText(currentDate);

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
                dataDialogDate.setText(sdf.format(myCalendar.getTime()));
                currentDate = sdf.format(myCalendar.getTime());
            }
        };
        dataDialogDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] date1Array = currentDate.split("-");
                new DatePickerDialog(getActivity(), date,
                        Integer.parseInt(date1Array[0]),
                        Integer.parseInt(date1Array[1]) - 1,
                        Integer.parseInt(date1Array[2])).show();
            }
        });
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listSiparis.clear();
                new GetOrdersFromDatabase().execute();
            }
        });
        builder.show();
    }

    private void uploadDataToWebFailDialog() {
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

    private String parametreGetir(String parametre, String deger) {
        String parametreDeger = deger;
        for (CihazlarFirmaParametreler parametreler : parametrelerList) {
            if (parametreler.getParametreAdi().equals(parametre)) {
                parametreDeger = parametreler.getParametreDegeri();
            }
        }
        return parametreDeger;
    }

}