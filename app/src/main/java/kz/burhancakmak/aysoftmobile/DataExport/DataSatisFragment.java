package kz.burhancakmak.aysoftmobile.DataExport;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
import kz.burhancakmak.aysoftmobile.Models.DataExport.DataExportTask;
import kz.burhancakmak.aysoftmobile.R;

public class DataSatisFragment extends Fragment implements DataExportAdapter.DataExportListener{
    SessionManagement session;
    DatabaseHandler databaseHandler;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LANG = "language";
    RecyclerView recyclerView;
    DataExportAdapter adapter;
    List<DataExportTask> listSiparis = new ArrayList<>();
    CheckBox cbSelectAll;
    String currentDate;
    int spinnerSelected;
    boolean dataBoolean;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data_satis, container, false);
        session = new SessionManagement(getActivity());
        webSettingsMap = session.getWebSettings();
        userSettingMap = session.getUserDetails();
        databaseHandler = DatabaseHandler.getInstance(getActivity());

        return view;
    }

    private void initViews() {
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
        adapter = new DataExportAdapter(listSiparis, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {

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
                listSiparis.clear();
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