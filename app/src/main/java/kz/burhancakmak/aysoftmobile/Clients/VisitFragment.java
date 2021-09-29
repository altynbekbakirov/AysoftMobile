package kz.burhancakmak.aysoftmobile.Clients;

import static kz.burhancakmak.aysoftmobile.Clients.ClientsTasksActivity.clientKayitNo;
import static kz.burhancakmak.aysoftmobile.MainActivity.DONEM_NO;
import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import kz.burhancakmak.aysoftmobile.Adapters.ZiyaretAdapter;
import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientZiyaret;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClientZiyaretResponse;
import kz.burhancakmak.aysoftmobile.R;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitApi;
import kz.burhancakmak.aysoftmobile.Retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;

public class VisitFragment extends Fragment implements ZiyaretAdapter.OnZiyaretListener {
    final int[] selectedItem = {0};
    Button visitStart, visitEnd;
    SessionManagement session;
    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    FusedLocationProviderClient fusedLocationProviderClient;
    View view;
    RecyclerView recyclerView;
    FloatingActionButton fabZiyaret;
    DatabaseHandler databaseHandler;
    ZiyaretAdapter adapter;
    List<ClientZiyaret> ziyaretList = new ArrayList<>();
    ClientZiyaret visit;
    Calendar myCalendar;
    private String date1, date2;
    HashMap<String, String> userSettingMap;
    HashMap<String, String> webSettingsMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        session = new SessionManagement(getActivity());
        webSettingsMap = session.getWebSettings();
        userSettingMap = session.getUserDetails();
        databaseHandler = DatabaseHandler.getInstance(getActivity());

        view = inflater.inflate(R.layout.fragment_visit, container, false);
        visitStart = view.findViewById(R.id.btnVisitStart);
        visitEnd = view.findViewById(R.id.btnVisitEnd);
        fabZiyaret = view.findViewById(R.id.fabZiyaret);
        visit = new ClientZiyaret();

        recyclerView = view.findViewById(R.id.ziyaretRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = new ZiyaretAdapter(ziyaretList, this);
        recyclerView.setAdapter(adapter);

        setHasOptionsMenu(true);
        myCalendar = Calendar.getInstance();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        date1 = dateFormat.format(date);
        date2 = dateFormat.format(date);

        new GetDataFromDatabase().execute(date1, date2);

        fabZiyaret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.getKeyVisit() != -1) {
                    addOrderFailDialog();
                } else {
                    if (locationPermissionGranted) {
                        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            addNewVisit();
                        } else {
                            locationFailDialog();
                        }
                    } else {
                        getLocationPermission();
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        choosePopupMenuOptions(position);
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

    private class GetDataFromDatabase extends AsyncTask<String, Void, Void> {
        RelativeLayout progressBar = view.findViewById(R.id.ziyaret_progressBar_layout);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ziyaretList.clear();
        }

        @Override
        protected Void doInBackground(String... items) {
            ziyaretList = databaseHandler.selectAllZiyaret(clientKayitNo, items[0], items[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.setZiyaretList(ziyaretList);
            progressBar.setVisibility(View.GONE);
        }
    }

    private class SendDataToServer extends AsyncTask<Integer, String, Void> {

        String webAddress = webSettingsMap.get("web");
        String phoneId = webSettingsMap.get("uuid");
        RelativeLayout products_progressBar = view.findViewById(R.id.ziyaret_progressBar_layout);
        RetrofitApi retrofitApi = RetrofitClient.getInstance(webAddress).create(RetrofitApi.class);
        Call<ClientZiyaretResponse> queryList;
        boolean isFailed = false;
        ClCard card;

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

            String ziyaretSatiri =
                    ziyaretList.get(integers[0]).getKayitNo() + "|" +
                            ziyaretList.get(integers[0]).getCariKayitNo() + "|" +
                            ziyaretList.get(integers[0]).getBeginKordinatLatitute() + "|" +
                            ziyaretList.get(integers[0]).getBeginKordinatLongitude() + "|" +
                            ziyaretList.get(integers[0]).getEndKordinatLatitute() + "|" +
                            ziyaretList.get(integers[0]).getEndKordinatLongitude() + "|" +
                            ziyaretList.get(integers[0]).getBaslangicTarihi() + "|" +
                            ziyaretList.get(integers[0]).getBaslangicSaati() + "|" +
                            ziyaretList.get(integers[0]).getBitisTarihi() + "|" +
                            ziyaretList.get(integers[0]).getBitisSaati() + "|" +
                            card.getKod() + "|" +
                            ziyaretList.get(integers[0]).getNotlar();

            queryList = retrofitApi.ziyaretEkle(
                    phoneId,
                    FIRMA_NO,
                    DONEM_NO,
                    userSettingMap.get(KEY_NAME),
                    userSettingMap.get(KEY_PASSWORD),
                    ziyaretSatiri);

            try {
                Response<ClientZiyaretResponse> response = queryList.execute();
                if (response.isSuccessful() && response.body() != null) {
                    ClientZiyaretResponse query = response.body();
                    if (!query.getHata()) {
                        ClientZiyaret ziyaret = new ClientZiyaret();
                        ziyaret.setErpGonderildi(1);
                        ziyaretList.get(integers[0]).setErpGonderildi(1);
                        adapter.notifyItemChanged(integers[0]);
                        databaseHandler.updateZiyaretErpGonderildi(ziyaret, String.valueOf(ziyaretList.get(integers[0]).getKayitNo()));
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
            if (isFailed) sendDataToServerFailDialog();
        }
    }

    private void getDeviceLocation() {
        try {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = new Date();
                            DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
                            Date date1 = new Date();
                        } else {
                            LocationRequest locationRequest = new LocationRequest()
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(5000)
                                    .setFastestInterval(500)
                                    .setNumUpdates(1);
                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    Location location1 = locationResult.getLastLocation();

                                }
                            };
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    }
                });
            } else {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }

    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void addOrderFailDialog() {
        ClCard client = databaseHandler.selectClientByZiyaret(session.getKeyVisit().intValue());
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.info_warning_title);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_dangerous);
        builder.setMessage(getString(R.string.client_visit_add_fail_info) + "\n\n" + client.getKod() + " - " + client.getUnvani1());
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void locationFailDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.info_warning_title);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_dangerous);
        builder.setMessage(getString(R.string.client_visit_location_enable_message));
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void choosePopupMenuOptions(int position) {
        String[] items = new String[]{
                getString(R.string.alert_kasa_menu_edit),
                getString(R.string.alert_kasa_menu_erp_gonder),
                getString(R.string.client_visit_end_session)};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.alert_kasa_title_choose);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_menu);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (selectedItem[0]) {
                    case 0:
                        if (ziyaretList.get(position).getKapatildi() == 0) {
                            editVisit(position);
                        }
                        break;
                    case 1:
                        if (ziyaretList.get(position).getKapatildi() == 0) {
                            closeVisitDialog();
                        } else {
                            new SendDataToServer().execute(position);
                        }
                        break;
                    case 2:
                        if (session.getKeyVisit() != -1) {
                            ClientZiyaret ziyaret = databaseHandler.selectZiyaretByCode(session.getKeyVisit().intValue());
                            if (ziyaret.getCariKayitNo().equals(clientKayitNo)) {
                                try {
                                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                                        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Location> task) {
                                                Location location = task.getResult();
                                                if (location != null) {
                                                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                                    Date date = new Date();
                                                    DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
                                                    Date date1 = new Date();
                                                    ClientZiyaret visit = new ClientZiyaret();
                                                    visit.setBitisTarihi(dateFormat.format(date));
                                                    visit.setBitisSaati(dateFormat1.format(date1));
                                                    visit.setEndKordinatLongitude(location.getLongitude());
                                                    visit.setEndKordinatLatitute(location.getLatitude());
                                                    visit.setKapatildi(1);
                                                    databaseHandler.updateZiyaretKapat(visit, String.valueOf(session.getKeyVisit()));
                                                    ziyaretList.get(position).setBitisTarihi(dateFormat.format(date));
                                                    ziyaretList.get(position).setBitisSaati(dateFormat1.format(date1));
                                                    ziyaretList.get(position).setEndKordinatLatitute(location.getLatitude());
                                                    ziyaretList.get(position).setEndKordinatLongitude(location.getLongitude());
                                                    ziyaretList.get(position).setKapatildi(1);
                                                    session.removeKeyVisit();
                                                    adapter.notifyItemChanged(position);
                                                } else {
                                                    LocationRequest locationRequest = new LocationRequest()
                                                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                                            .setInterval(5000)
                                                            .setFastestInterval(500)
                                                            .setNumUpdates(1);
                                                    LocationCallback locationCallback = new LocationCallback() {
                                                        @Override
                                                        public void onLocationResult(@NonNull LocationResult locationResult) {
                                                            super.onLocationResult(locationResult);
                                                            Location location1 = locationResult.getLastLocation();
                                                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                                            Date date = new Date();
                                                            DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
                                                            Date date1 = new Date();
                                                            ClientZiyaret visit = new ClientZiyaret();
                                                            visit.setBitisTarihi(dateFormat.format(date));
                                                            visit.setBitisSaati(dateFormat1.format(date1));
                                                            visit.setEndKordinatLongitude(location1.getLongitude());
                                                            visit.setEndKordinatLatitute(location1.getLatitude());
                                                            visit.setKapatildi(1);
                                                            databaseHandler.updateZiyaretKapat(visit, String.valueOf(session.getKeyVisit()));
                                                            ziyaretList.get(position).setBitisTarihi(dateFormat.format(date));
                                                            ziyaretList.get(position).setBitisSaati(dateFormat1.format(date1));
                                                            ziyaretList.get(position).setEndKordinatLatitute(location1.getLatitude());
                                                            ziyaretList.get(position).setEndKordinatLongitude(location1.getLongitude());
                                                            ziyaretList.get(position).setKapatildi(1);
                                                            session.removeKeyVisit();
                                                            adapter.notifyItemChanged(position);

                                                        }
                                                    };
                                                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                                                }
                                            }
                                        });
                                    } else {
                                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    }

                                } catch (SecurityException e) {
                                    Log.e("Exception: %s", e.getMessage(), e);
                                }
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

    private void addNewVisit() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.client_visit_change_data_layout, null);
        builder.setView(view);
        EditText ziyaretDateEdit, ziyaretTimeEdit, ziyaretDefinitionEdit;

        ziyaretDateEdit = view.findViewById(R.id.ziyaretDateEdit);
        ziyaretTimeEdit = view.findViewById(R.id.ziyaretTimeEdit);
        ziyaretDefinitionEdit = view.findViewById(R.id.ziyaretDefinitionEdit);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
        Date date1 = new Date();

        ziyaretDateEdit.setText(dateFormat.format(date));
        ziyaretTimeEdit.setText(dateFormat1.format(date1));

//        getDeviceLocation();

        builder.setPositiveButton(R.string.client_visit_start, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location != null) {
                                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = new Date();
                                    DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
                                    Date date1 = new Date();
                                    visit.setCariKayitNo(clientKayitNo);
                                    visit.setBeginKordinatLatitute(location.getLatitude());
                                    visit.setBeginKordinatLongitude(location.getLongitude());
                                    visit.setBaslangicTarihi(dateFormat.format(date));
                                    visit.setBaslangicSaati(dateFormat1.format(date1));
                                    visit.setNotlar(ziyaretDefinitionEdit.getText().toString());
                                    visit.setErpGonderildi(0);
                                    visit.setKapatildi(0);
                                    Long id = databaseHandler.insertZiyaret(visit);
                                    visit.setKayitNo(id.intValue());
                                    session.createVisit(id);
                                    ziyaretList.add(visit);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    LocationRequest locationRequest = new LocationRequest()
                                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                            .setInterval(5000)
                                            .setFastestInterval(500)
                                            .setNumUpdates(1);
                                    LocationCallback locationCallback = new LocationCallback() {
                                        @Override
                                        public void onLocationResult(@NonNull LocationResult locationResult) {
                                            super.onLocationResult(locationResult);
                                            Location location1 = locationResult.getLastLocation();
                                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                            Date date = new Date();
                                            DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
                                            Date date1 = new Date();
                                            visit.setCariKayitNo(clientKayitNo);
                                            visit.setBeginKordinatLatitute(location1.getLatitude());
                                            visit.setBeginKordinatLongitude(location1.getLongitude());
                                            visit.setBaslangicTarihi(dateFormat.format(date));
                                            visit.setBaslangicSaati(dateFormat1.format(date1));
                                            visit.setNotlar(ziyaretDefinitionEdit.getText().toString());
                                            visit.setErpGonderildi(0);
                                            visit.setKapatildi(0);
                                            Long id = databaseHandler.insertZiyaret(visit);
                                            visit.setKayitNo(id.intValue());
                                            session.createVisit(id);
                                            ziyaretList.add(visit);
                                            adapter.notifyDataSetChanged();

                                        }
                                    };
                                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                                }
                            }
                        });
                    } else {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                } catch (SecurityException e) {
                    Log.e("Exception: %s", e.getMessage(), e);
                }
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

    private void editVisit(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.client_visit_change_data_layout, null);
        builder.setView(view);
        EditText ziyaretDateEdit, ziyaretTimeEdit, ziyaretDefinitionEdit, ziyaretEndDateEdit, ziyaretEndTimeEdit;

        ziyaretDateEdit = view.findViewById(R.id.ziyaretDateEdit);
        ziyaretTimeEdit = view.findViewById(R.id.ziyaretTimeEdit);
        ziyaretEndDateEdit = view.findViewById(R.id.ziyaretEndDateEdit);
        ziyaretEndTimeEdit = view.findViewById(R.id.ziyaretEndTimeEdit);
        ziyaretDefinitionEdit = view.findViewById(R.id.ziyaretDefinitionEdit);

        ClientZiyaret ziyaret = ziyaretList.get(position);

        ziyaretDateEdit.setText(ziyaret.getBaslangicTarihi());
        ziyaretTimeEdit.setText(ziyaret.getBaslangicSaati());
        ziyaretEndDateEdit.setText(ziyaret.getBitisTarihi());
        ziyaretEndTimeEdit.setText(ziyaret.getBitisSaati());
        ziyaretDefinitionEdit.setText(ziyaret.getNotlar());

        builder.setPositiveButton(R.string.clients_orders_menu_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHandler.updateZiyaretDefinition(ziyaretDefinitionEdit.getText().toString(), String.valueOf(ziyaretList.get(position).getKayitNo()));
                ziyaretList.get(position).setNotlar(ziyaretDefinitionEdit.getText().toString());
                adapter.notifyItemChanged(position);
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

    private void closeVisitDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(R.string.info_warning_title);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_dangerous);
        builder.setMessage(R.string.client_visit_close_visit_before_send);
        builder.setPositiveButton(R.string.alert_confirm_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}