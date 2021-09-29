package kz.burhancakmak.aysoftmobile.Clients;

import static kz.burhancakmak.aysoftmobile.MainActivity.FIRMA_NO;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kz.burhancakmak.aysoftmobile.Database.DatabaseHandler;
import kz.burhancakmak.aysoftmobile.Login.SessionManagement;
import kz.burhancakmak.aysoftmobile.Models.Clients.ClCard;
import kz.burhancakmak.aysoftmobile.Models.Firms.CihazlarFirmaParametreler;
import kz.burhancakmak.aysoftmobile.R;

public class ClientsMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {
    private GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location lastKnownLocation, currentLocation;
    DatabaseHandler databaseHandler;
    SessionManagement session;
    HashMap<String, String> webSettingsMap;
    HashMap<String, String> userSettingMap;
    private static final String KEY_LANG = "language";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    ClCard card;
    int clientKayitNo;
    Location myLocation = null;
    protected LatLng start = null;
    protected LatLng end = null;
    private List<Polyline> polylines = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManagement(getApplicationContext());
        userSettingMap = session.getUserDetails();
        webSettingsMap = session.getWebSettings();

        if (!(userSettingMap.get(KEY_LANG) == null)) {
            setPhoneDefaultLanguage(userSettingMap.get(KEY_LANG));
        }
        setContentView(R.layout.activity_clients_map);
        Intent intent = getIntent();
        clientKayitNo = intent.getIntExtra("clientKayitNo", -1);
        databaseHandler = DatabaseHandler.getInstance(this);
        if (clientKayitNo != -1) {
            card = databaseHandler.selectClientById(clientKayitNo);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.client_maps_title);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clients_maps_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.client_maps_save) {
            Double longitude = null;
            Double latitude = null;
            if (lastKnownLocation != null) {
                longitude = lastKnownLocation.getLongitude();
                latitude = lastKnownLocation.getLatitude();
            }
            Intent intent = new Intent();
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);
            intent.putExtra("clientKayitNo", clientKayitNo);
            intent.putExtra("clientKod", card.getKod());
            setResult(RESULT_OK, intent);
            finish();
        }
        if (item.getItemId() == R.id.client_maps_route) {
            try {
                if (locationPermissionGranted) {
                    if (card != null && card.getKordinatLatitute() != 0.0 && card.getKordinatLongitude() != 0.0) {
                        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                        locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful()) {
                                    currentLocation = task.getResult();
                                    if (currentLocation != null) {
                                        LatLng destination = new LatLng(card.getKordinatLatitute(), card.getKordinatLongitude());
                                        LatLng location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                        Findroutes(location, destination);
                                    }
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ClientsMapActivity.this, R.string.client_maps_destination_not_defined, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    map.setMyLocationEnabled(false);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    map.getUiSettings().setZoomControlsEnabled(false);
                    getLocationPermission();
                }
            } catch (SecurityException e) {
                Log.e("Exception: %s", e.getMessage());
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        getLocationPermission();
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                getDeviceLocation();
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(false);
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                if (card != null && card.getKordinatLatitute() != 0.0 && card.getKordinatLongitude() != 0.0) {
                    LatLng latLng = new LatLng(card.getKordinatLatitute(), card.getKordinatLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng).title(card.getUnvani1());
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    map.addMarker(markerOptions);
                    map.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(latLng, 15));
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                    locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                lastKnownLocation = task.getResult();
                                if (lastKnownLocation != null) {
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    map.addMarker(markerOptions);
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(lastKnownLocation.getLatitude(),
                                                    lastKnownLocation.getLongitude()),
                                            15));
                                }
                            }
                        }
                    });
                }
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull @NotNull LatLng latLng) {
                        String params = parametreGetir(FIRMA_NO, "CariLokasyonDegistirebilir", "0");
                        if (params.equals("1")) {
                            map.clear();
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            map.addMarker(markerOptions);
                            Location location = new Location(LocationManager.GPS_PROVIDER);
                            location.setLatitude(latLng.latitude);
                            location.setLongitude(latLng.longitude);
                            lastKnownLocation = location;
                        } else if (params.equals("0")) {
                            if (card.getKordinatLatitute() == 0.0 && card.getKordinatLongitude() == 0.0) {
                                map.clear();
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                map.addMarker(markerOptions);
                                Location location = new Location(LocationManager.GPS_PROVIDER);
                                location.setLatitude(latLng.latitude);
                                location.setLongitude(latLng.longitude);
                                lastKnownLocation = location;
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        updateLocationUI();
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

    //to get user location
    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                myLocation = location;
                LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        ltlng, 16f);
                map.animateCamera(cameraUpdate);
            }
        });

        //get destination location when user click on map
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                end = latLng;
                map.clear();

                start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                //start route finding
                Findroutes(start, end);
            }
        });
    }

    public void Findroutes(LatLng Start, LatLng End) {
        if (Start == null || End == null) {
            Toast.makeText(ClientsMapActivity.this, R.string.client_maps_unable_get_location, Toast.LENGTH_LONG).show();
        } else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyB3fP9SQAruJcOkus5Po6v9ouryijdX5SU")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
//    Findroutes(start,end);
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(ClientsMapActivity.this, R.string.client_maps_finding_route, Toast.LENGTH_LONG).show();
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        map.clear();

        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {

            if (i == shortestRouteIndex) {
                polyOptions.color(getResources().getColor(R.color.colorPrimary));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = map.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k - 1);
                polylines.add(polyline);

            } else {

            }

        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title(getString(R.string.client_maps_my_location));
        map.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        endMarker.title(getString(R.string.client_maps_destination_location));
        map.addMarker(endMarker);
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(start, end);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(start, end);
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