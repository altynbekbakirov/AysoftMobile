package kz.burhancakmak.aysoftmobile.DataExport;

import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
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

import kz.burhancakmak.aysoftmobile.Adapters.DataExportAdapter;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientKasa;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientSepet;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsKasaResponse;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientsSiparisResponse;
import kz.burhancakmak.aysoftmobile.Models.DataExport.DataExportTask;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;

public class DataKasaFragment extends Fragment implements DataExportAdapter.DataExportListener {
    SessionManagement session;
    DatabaseHandler databaseHandler;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    RecyclerView recyclerView;
    DataExportAdapter adapter;
    List<DataExportTask> listKasa = new ArrayList<>();
    CheckBox cbSelectAll;
    String currentDate;
    int spinnerSelected;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data_kasa, container, false);
        session = new SessionManagement(getActivity());
        webSettingsMap = session.getWebSettings();
        userSettingMap = session.getUserDetails();
        databaseHandler = DatabaseHandler.getInstance(getActivity());

        initViews();
        new GetDataFromDatabase().execute();

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

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = new DataExportAdapter(listKasa, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.data_export_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.dataUpload) {
            new UploadDataToWeb().execute();
        }
        if (item.getItemId() == R.id.dataFilter) {
            showFilterDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    class GetDataFromDatabase extends AsyncTask<Void, Void, Void> {
        RelativeLayout products_progressBar = view.findViewById(R.id.dataExport_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... items) {
            listKasa = databaseHandler.selectDataExportKasa(currentDate);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (int i = 0; i < listKasa.size(); i++) {
                listKasa.get(i).setCbChecked(false);
            }
            adapter.setDataList(listKasa);
            products_progressBar.setVisibility(View.GONE);
        }
    }

    class UploadDataToWeb extends AsyncTask<Void, Void, Void> {
        RelativeLayout products_progressBar = view.findViewById(R.id.dataExport_progressBar_layout);
        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        String login = userSettingMap.get(KEY_NAME);
        String password = userSettingMap.get(KEY_PASSWORD);
        RetrofitApi retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
        Call<ClientsSiparisResponse> siparisList;
        Call<ClientsKasaResponse> kasaList;
        List<ClientSepet> sepetList = new ArrayList<>();
        boolean isFailed = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            products_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... items) {
            for (int i = 0; i < listKasa.size(); i++) {
                if (listKasa.get(i).getTur() == 2 && listKasa.get(i).getErpGonderildi() < 1 && listKasa.get(i).getCbChecked()) {
                    kasaList = retrofitApi.kasaHareketleriGuncelle(
                            phoneId,
                            login,
                            password,
                            FIRMA_NO,
                            DONEM_NO,
                            listKasa.get(i).getIslemTipi(),
                            listKasa.get(i).getKayitNo(),
                            listKasa.get(i).getTarih(),
                            listKasa.get(i).getCariKayitNo(),
                            listKasa.get(i).getKod(),
                            listKasa.get(i).getTutar() < 0 ? -listKasa.get(i).getTutar() : listKasa.get(i).getTutar(),
                            listKasa.get(i).getAciklama(),
                            listKasa.get(i).getMakbuzNo(),
                            listKasa.get(i).getKasaKodu(),
                            ""
                    );
                    try {
                        Response<ClientsKasaResponse> response = kasaList.execute();
                        if (response.isSuccessful() && response.body() != null) {
                            ClientsKasaResponse query = response.body();
                            if (!query.getHata()) {
                                ClientKasa kasa = new ClientKasa();
                                listKasa.get(i).setErpGonderildi(1);
                                listKasa.get(i).setErpKayitNo(query.getKasaHareketKayitNo());
                                listKasa.get(i).setErpFisNo(query.getFisNo());
                                adapter.notifyItemChanged(i);
                                kasa.setErpFisNo(query.getFisNo());
                                kasa.setErpKayitNo(query.getKasaHareketKayitNo());
                                kasa.setErpGonderildi(1);
                                databaseHandler.updateKasaIslemleriErpGonder(kasa, String.valueOf(listKasa.get(i).getKayitNo()));
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

        /*SwitchCompat dataSwitch = view.findViewById(R.id.dataSwitch);
        dataSwitch.setChecked(dataBoolean);
        dataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataBoolean = true;
                } else {
                    dataBoolean = false;
                }
            }
        });*/

        /*Spinner dataSpinner = view.findViewById(R.id.dataSpinner);
        String[] deliveryArray = new String[]{
                getString(R.string.data_export_fiche_type_all),
                getString(R.string.data_export_fiche_type_order),
                getString(R.string.data_export_fiche_type_finance)};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_order_item_layout, deliveryArray);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_order_dropdownitem_layout);
        dataSpinner.setAdapter(arrayAdapter);
        dataSpinner.setSelection(spinnerSelected);
        dataSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

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
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listKasa.clear();
                new GetDataFromDatabase().execute();
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
}